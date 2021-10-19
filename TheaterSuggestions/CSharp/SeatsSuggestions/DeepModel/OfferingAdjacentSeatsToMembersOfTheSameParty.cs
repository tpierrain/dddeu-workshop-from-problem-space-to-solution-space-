using System.Collections;
using System.Collections.Generic;
using System.Linq;

namespace SeatsSuggestions.DeepModel
{
    /// <summary>
    ///     Business Rule: offer adjacent seats to members of the same party
    /// </summary>
    public static class OfferingAdjacentSeatsToMembersOfTheSameParty
    {
        private static AdjacentSeats NoAdjacentSeatFound { get; } = new(new List<SeatWithTheDistanceFromTheMiddleOfTheRow>());

        public static IEnumerable<Seat>
            OfferAdjacentSeats(
                SuggestionRequest suggestionRequest,
                IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistanceFromTheMiddleOfRow)
        {
            var splitInGroupsOfAdjacentSeats = SplitInGroupsOfAdjacentSeats(seatsWithDistanceFromTheMiddleOfRow);

            var offerAdjacentSeats = SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
                    suggestionRequest,
                    splitInGroupsOfAdjacentSeats)
                .SeatsWithDistance
                .Select(s => s.Seat);

            return offerAdjacentSeats;
        }

        private static AdjacentSeats
            SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
                SuggestionRequest suggestionRequest,
                AdjacentSeatsGroups groupOfAdjacentSeats)
        {
            var theBestDistancesNearerTheMiddleOfTheRowPerGroup =
                new SortedDictionary<int, AdjacentSeatsGroups>();

            // To select the best group of adjacent seats, we sort them by their distances
            foreach (var adjacentSeats in SortGroupsByDistanceFromMiddleOfTheRow(suggestionRequest,
                groupOfAdjacentSeats).Groups)
            {
                if (!IsMatchingPartyRequested(suggestionRequest, adjacentSeats.SeatsWithDistance)) continue;

                var sumOfDistances = adjacentSeats.SeatsWithDistance.Sum(s => s.DistanceFromTheMiddleOfTheRow);

                if (!theBestDistancesNearerTheMiddleOfTheRowPerGroup.ContainsKey(sumOfDistances))
                {
                    var adjacentSeatsGroups = new AdjacentSeatsGroups();
                    theBestDistancesNearerTheMiddleOfTheRowPerGroup[sumOfDistances] = adjacentSeatsGroups;
                }

                theBestDistancesNearerTheMiddleOfTheRowPerGroup[sumOfDistances].Groups.Add(adjacentSeats);
            }

            return theBestDistancesNearerTheMiddleOfTheRowPerGroup.Any()
                ? SelectTheBestGroup(theBestDistancesNearerTheMiddleOfTheRowPerGroup)
                : NoAdjacentSeatFound;
        }

        private static AdjacentSeatsGroups
            SortGroupsByDistanceFromMiddleOfTheRow(SuggestionRequest suggestionRequest,
                AdjacentSeatsGroups groupOfAdjacentSeats)
        {
            var adjacentSeatsGroups = new AdjacentSeatsGroups();

            foreach (var seats in groupOfAdjacentSeats.Groups.Select(adjacentSeats =>
                adjacentSeats.SeatsWithDistance.OrderBy(s => s.DistanceFromTheMiddleOfTheRow)))
                adjacentSeatsGroups.Groups.Add(new AdjacentSeats(seats.Take(suggestionRequest.PartyRequested)));

            return adjacentSeatsGroups;
        }

        private static AdjacentSeats
            SelectTheBestGroup(SortedDictionary<int, AdjacentSeatsGroups> bestGroups)
        {
            return HasOnlyOneBestGroup(bestGroups)
                ? ProjectToAdjacentSeats(bestGroups)
                : DecideWhichGroupIsTheBestWhenDistancesAreEqual(bestGroups);
        }

        private static AdjacentSeats
            DecideWhichGroupIsTheBestWhenDistancesAreEqual(SortedDictionary<int, AdjacentSeatsGroups> bestGroups)
        {
            SortedDictionary<int, AdjacentSeats> decideBetweenIdenticalScores = new();

            foreach (var adjacentSeats in bestGroups
                .Values.SelectMany(adjacentSeatsGroups => adjacentSeatsGroups.Groups))
                SelectTheBestScoreBetweenGroups(decideBetweenIdenticalScores, adjacentSeats);

            return decideBetweenIdenticalScores.LastOrDefault().Value;
        }

        private static void SelectTheBestScoreBetweenGroups(
            SortedDictionary<int, AdjacentSeats> decideBetweenIdenticalScores, AdjacentSeats adjacentSeats)
        {
            if (decideBetweenIdenticalScores.ContainsKey(adjacentSeats.SeatsWithDistance.Count))
            {
                var (bestGroupScore, bestGroupScoreForContained) =
                    ExtractScores(adjacentSeats, decideBetweenIdenticalScores);

                if (bestGroupScore < bestGroupScoreForContained)
                    decideBetweenIdenticalScores[adjacentSeats.SeatsWithDistance.Count] = adjacentSeats;
            }
            else
            {
                decideBetweenIdenticalScores[adjacentSeats.SeatsWithDistance.Count] = adjacentSeats;
            }
        }

        private static (long bestGroupScore, long bestGroupScoreForContained)
            ExtractScores(AdjacentSeats adjacentSeats,
                IReadOnlyDictionary<int, AdjacentSeats> decideBetweenIdenticalScores)
        {
            // Groups are equivalents, the domain expert have decided to select the group with the smallest seat numbers
            // =========================================================================================================
            return (bestGroupScore: adjacentSeats.SeatsWithDistance.Sum(s => s.Seat.Number),
                bestGroupScoreForContained: decideBetweenIdenticalScores[adjacentSeats.SeatsWithDistance.Count]
                    .SeatsWithDistance.Sum(s => s.Seat.Number));
        }

        private static AdjacentSeats ProjectToAdjacentSeats(SortedDictionary<int, AdjacentSeatsGroups> bestGroups)
        {
            return FirstValues(bestGroups).Groups[0];
        }

        private static AdjacentSeatsGroups FirstValues(SortedDictionary<int, AdjacentSeatsGroups> bestGroups)
        {
            return bestGroups.Values.First();
        }


        private static bool HasOnlyOneBestGroup(SortedDictionary<int, AdjacentSeatsGroups> bestGroups)
        {
            return bestGroups.FirstOrDefault().Value.Groups.Count == 1;
        }

        private static bool IsMatchingPartyRequested(SuggestionRequest suggestionRequest, ICollection seatWithDistances)
        {
            return seatWithDistances.Count >= suggestionRequest.PartyRequested;
        }

        private static AdjacentSeatsGroups SplitInGroupsOfAdjacentSeats(
            IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances)
        {
            var adjacentSeats = new AdjacentSeats(new List<SeatWithTheDistanceFromTheMiddleOfTheRow>());
            var groupsOfAdjacentSeats = new AdjacentSeatsGroups();

            using (var enumerator = seatsWithDistances.OrderBy(s => s.Seat.Number).GetEnumerator())
            {
                SeatWithTheDistanceFromTheMiddleOfTheRow seatWithTheDistancePrevious = null;

                while (enumerator.MoveNext())
                {
                    var seatWithDistance = enumerator.Current;
                    if (seatWithTheDistancePrevious == null)
                    {
                        seatWithTheDistancePrevious = seatWithDistance;
                        adjacentSeats.AddSeat(seatWithTheDistancePrevious);
                    }
                    else
                    {
                        if (seatWithDistance?.Seat.Number == seatWithTheDistancePrevious.Seat.Number + 1)
                        {
                            adjacentSeats.AddSeat(seatWithDistance);
                            seatWithTheDistancePrevious = seatWithDistance;
                        }
                        else
                        {
                            groupsOfAdjacentSeats.Groups.Add(adjacentSeats);
                            adjacentSeats = new AdjacentSeats(new List<SeatWithTheDistanceFromTheMiddleOfTheRow>() {seatWithDistance});
                            seatWithTheDistancePrevious = null;
                        }
                    }
                }
            }

            groupsOfAdjacentSeats.Groups.Add(adjacentSeats);

            return groupsOfAdjacentSeats;
        }
    }
}