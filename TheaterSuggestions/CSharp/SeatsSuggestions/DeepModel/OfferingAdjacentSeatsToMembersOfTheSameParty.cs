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
                // if bestDistances contain two proposals for the best distance (i.e. the shorter one).
                // We to take the right side and not the left side, it's a domain expert choice.
                ? SelectTheBestGroup(bestDistances)
                : NoSeatSuggested;
        }

        private static IEnumerable<Seat> SelectTheBestGroup(SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestDistances)
        {
            var seatWithTheDistanceFromTheMiddleOfTheRows = bestDistances.Values.First();

            if (seatWithTheDistanceFromTheMiddleOfTheRows.Count > 1)
            {
                foreach (var seatWithTheDistanceFromTheMiddleOfTheRow in seatWithTheDistanceFromTheMiddleOfTheRows)
                {
                    if (seatWithTheDistanceFromTheMiddleOfTheRow.Any(s => s.DistanceFromTheMiddleOfTheRow == 0))
                    {
                        return seatWithTheDistanceFromTheMiddleOfTheRow.Select(seatsWithDistance => seatsWithDistance.Seat); ;
                    }
                }
            }

            return bestDistances.Values.First()[0]
                .Select(seatsWithDistance => seatsWithDistance.Seat);
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

            // To find adjacent seats, we need to sort seats by their numbers
            using (var enumerator = seatsWithDistances.OrderBy(s => s.Seat.Number).GetEnumerator())
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
                            groupOfSeatDistance = new List<SeatWithTheDistanceFromTheMiddleOfTheRow> { seatWithDistance };
                            seatWithTheDistancePrevious = null;
                        }
                    }
                }
            }

            groupsOfSeatDistance.Add(groupOfSeatDistance);


            return groupsOfSeatDistance;
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { _suggestionRequest };
        }
    }
}