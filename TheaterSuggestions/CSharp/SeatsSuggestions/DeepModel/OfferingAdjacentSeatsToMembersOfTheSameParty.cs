using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Value;

namespace SeatsSuggestions.DeepModel
{
    /// <summary>
    ///     Business Rule: offer adjacent seats to members of the same party
    /// </summary>
    public class OfferingAdjacentSeatsToMembersOfTheSameParty : ValueType<OfferingAdjacentSeatsToMembersOfTheSameParty>
    {
        private readonly SuggestionRequest _suggestionRequest;

        public OfferingAdjacentSeatsToMembersOfTheSameParty(SuggestionRequest suggestionRequest)
        {
            _suggestionRequest = suggestionRequest;
        }

        private IEnumerable<Seat> NoSeatSuggested { get; } = new List<Seat>();

        public IEnumerable<Seat> OfferAdjacentSeats(
            IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances)
        {
            return SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
                SplitInGroupsOfAdjacentSeats(seatsWithDistances));
        }

        private IEnumerable<Seat> SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
            IEnumerable<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupOfAdjacentSeats)
        {
            var bestDistances = new SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>>();

            // To select the best group of adjacent seats, we sort them by their distances
            foreach (var seatsWithDistance in groupOfAdjacentSeats
                .Select(seatWithDistances => seatWithDistances.OrderBy(s => s.DistanceFromTheMiddleOfTheRow).ToList())
                .ToList())
            {
                if (!IsMatchingPartyRequested(_suggestionRequest, seatsWithDistance)) continue;

                var sumOfDistances = seatsWithDistance.Sum(s => s.DistanceFromTheMiddleOfTheRow);

                if (!bestDistances.ContainsKey(sumOfDistances))
                    bestDistances[sumOfDistances] = new List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>();

                bestDistances[sumOfDistances].Add(seatsWithDistance);
            }

            return bestDistances.Any()
                ? SelectTheBestGroup(bestDistances)
                : NoSeatSuggested;
        }

        private static IEnumerable<Seat> SelectTheBestGroup(
            SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestDistances)
        {
            var seatsWithTheDistanceFromTheMiddleOfTheRows = bestDistances.Values.First();

            if (HaveMultipleBestHighScores(seatsWithTheDistanceFromTheMiddleOfTheRows))
            {
                var decideBetweenIdenticalScores = new SortedDictionary<int, List<SeatWithTheDistanceFromTheMiddleOfTheRow>>();

                foreach (var seatWithTheDistanceFromTheMiddleOfTheRow in bestDistances.Values.First())
                    decideBetweenIdenticalScores[seatWithTheDistanceFromTheMiddleOfTheRow.Count] =
                        seatWithTheDistanceFromTheMiddleOfTheRow;

                if (decideBetweenIdenticalScores.Count == 1 && decideBetweenIdenticalScores.First().Value.Count > 1)
                {
                    return SelectTheOnlyBestGroup(bestDistances);
                }

                return SelectTheGroupWhoseSizeIsTheLargestWithEqualScore(decideBetweenIdenticalScores);
            }

            return SelectTheOnlyBestGroup(bestDistances);
        }

        private static IEnumerable<Seat> SelectTheOnlyBestGroup(
            SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestDistances)
        {
            return bestDistances.Values.First()[0]
                .Select(seatsWithDistance => seatsWithDistance.Seat);
        }

        private static IEnumerable<Seat> SelectTheGroupWhoseSizeIsTheLargestWithEqualScore(
            SortedDictionary<int, List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupsWithHighScores)
        {
            return groupsWithHighScores.OrderByDescending(key => key.Key).First().Value
                .Select(seatsWithDistance => seatsWithDistance.Seat);
        }

        private static bool HaveMultipleBestHighScores(
            List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> seatsWithTheDistanceFromTheMiddleOfTheRows)
        {
            return seatsWithTheDistanceFromTheMiddleOfTheRows.Count > 1;
        }

        private static bool IsMatchingPartyRequested(SuggestionRequest suggestionRequest, ICollection seatWithDistances)
        {
            return seatWithDistances.Count >= suggestionRequest.PartyRequested;
        }

        private static IEnumerable<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> SplitInGroupsOfAdjacentSeats(
            IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances)
        {
            var groupOfSeatDistance = new List<SeatWithTheDistanceFromTheMiddleOfTheRow>();
            var groupsOfSeatDistance = new List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>();

            using (var enumerator = OrderSeatsByTheirNumberToGroupAdjacent(seatsWithDistances).GetEnumerator())
            {
                SeatWithTheDistanceFromTheMiddleOfTheRow seatWithTheDistancePrevious = null;

                while (enumerator.MoveNext())
                {
                    var seatWithDistance = enumerator.Current;
                    if (seatWithTheDistancePrevious == null)
                    {
                        seatWithTheDistancePrevious = seatWithDistance;
                        groupOfSeatDistance.Add(seatWithTheDistancePrevious);
                    }
                    else
                    {
                        if (seatWithDistance?.Seat.Number == seatWithTheDistancePrevious.Seat.Number + 1)
                        {
                            groupOfSeatDistance.Add(seatWithDistance);
                            seatWithTheDistancePrevious = seatWithDistance;
                        }
                        else
                        {
                            groupsOfSeatDistance.Add(groupOfSeatDistance);
                            groupOfSeatDistance = new List<SeatWithTheDistanceFromTheMiddleOfTheRow>
                                { seatWithDistance };
                            seatWithTheDistancePrevious = null;
                        }
                    }
                }
            }

            groupsOfSeatDistance.Add(groupOfSeatDistance);


            return groupsOfSeatDistance;
        }

        private static IOrderedEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> OrderSeatsByTheirNumberToGroupAdjacent(IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances)
        {
            return seatsWithDistances.OrderBy(s => s.Seat.Number);
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { _suggestionRequest };
        }
    }
}