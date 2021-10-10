using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Value;

namespace SeatsSuggestions.DeepModel
{
    /// <summary>
    ///     Business Rule: offer adjacent seats to members of the same party
    /// </summary>
    public class OfferAdjacentSeatsToMembersOfTheSameParty : ValueType<OfferAdjacentSeatsToMembersOfTheSameParty>
    {
        private readonly SuggestionRequest _suggestionRequest;

        public OfferAdjacentSeatsToMembersOfTheSameParty(SuggestionRequest suggestionRequest)
        {
            _suggestionRequest = suggestionRequest;
        }

        private List<Seat> NoSeatSuggested { get; } = new List<Seat>();

        public List<Seat> SuggestAdjacentSeats(IEnumerable<SeatWithDistanceFromTheMiddleOfTheRow> seatsWithDistances)
        {
            var groupOfAdjacentSeats = SplitInGroupsOfAdjacentSeats(seatsWithDistances);
            return SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(groupOfAdjacentSeats)
                .ToList();
        }

        private IEnumerable<Seat> SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
            IEnumerable<List<SeatWithDistanceFromTheMiddleOfTheRow>> groupOfAdjacentSeats)
        {
            var bestDistances = new SortedDictionary<int, List<List<SeatWithDistanceFromTheMiddleOfTheRow>>>();

            // To select the best group of adjacent seats, we sort them by their distances
            foreach (var seatsWithDistance in groupOfAdjacentSeats
                .Select(seatWithDistances => seatWithDistances.OrderBy(s => s.DistanceFromTheMiddleOfTheRow).ToList())
                .ToList())
            {
                if (!IsMatchingPartyRequested(_suggestionRequest, seatsWithDistance)) continue;

                var sumOfDistances = seatsWithDistance.Sum(s => s.DistanceFromTheMiddleOfTheRow);

                if (!bestDistances.ContainsKey(sumOfDistances))
                    bestDistances[sumOfDistances] = new List<List<SeatWithDistanceFromTheMiddleOfTheRow>>();

                bestDistances[sumOfDistances].Add(seatsWithDistance);
            }

            return bestDistances.Any()
                // if bestDistances contains two proposals for the best distance, the shorter one.
                // We to take the right side and not the left side, it's a domain expert choice.
                ? bestDistances.Values.First()[0]
                    .Select(seatsWithDistance => seatsWithDistance.Seat).ToList()
                : NoSeatSuggested;
        }

        private static bool IsMatchingPartyRequested(SuggestionRequest suggestionRequest, ICollection seatWithDistances)
        {
            return seatWithDistances.Count >= suggestionRequest.PartyRequested;
        }

        private static IEnumerable<List<SeatWithDistanceFromTheMiddleOfTheRow>> SplitInGroupsOfAdjacentSeats(
            IEnumerable<SeatWithDistanceFromTheMiddleOfTheRow> seatsWithDistances)
        {
            var groupOfSeatDistance = new List<SeatWithDistanceFromTheMiddleOfTheRow>();
            var groupsOfSeatDistance = new List<List<SeatWithDistanceFromTheMiddleOfTheRow>>();

            // To find adjacent seats, we need to sort seats by their numbers
            using (var enumerator = seatsWithDistances.OrderBy(s => s.Seat.Number).GetEnumerator())
            {
                SeatWithDistanceFromTheMiddleOfTheRow seatWithDistancePrevious = null;

                while (enumerator.MoveNext())
                {
                    var seatWithDistance = enumerator.Current;
                    if (seatWithDistancePrevious == null)
                    {
                        seatWithDistancePrevious = seatWithDistance;
                        groupOfSeatDistance.Add(seatWithDistancePrevious);
                    }
                    else
                    {
                        if (seatWithDistance?.Seat.Number == seatWithDistancePrevious.Seat.Number + 1)
                        {
                            groupOfSeatDistance.Add(seatWithDistance);
                            seatWithDistancePrevious = seatWithDistance;
                        }
                        else
                        {
                            groupsOfSeatDistance.Add(groupOfSeatDistance);
                            groupOfSeatDistance = new List<SeatWithDistanceFromTheMiddleOfTheRow> { seatWithDistance };
                            seatWithDistancePrevious = null;
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