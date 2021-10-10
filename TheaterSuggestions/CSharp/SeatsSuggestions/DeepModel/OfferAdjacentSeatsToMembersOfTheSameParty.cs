﻿using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Value;

namespace SeatsSuggestions.DeepModel
{
    /// <summary>
    ///
    /// Business Rule: offer adjacent seats to members of the same party
    /// 
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
            var bestDistances = new SortedDictionary<int, List<SeatWithDistanceFromTheMiddleOfTheRow>>();
            foreach (var seatsWithDistance in groupOfAdjacentSeats)
                if (IsMatchingPartyRequested(_suggestionRequest, seatsWithDistance))
                {
                    var sumOfDistances = seatsWithDistance.Sum(s => s.DistanceFromTheMiddle);

                    if (!bestDistances.ContainsKey(sumOfDistances))
                        bestDistances.Add(sumOfDistances, seatsWithDistance);
                }

            return bestDistances.Any()
                ? bestDistances.Values.First().Select(seatsWithDistance => seatsWithDistance.Seat).ToList()
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
                            groupsOfSeatDistance.Add(groupOfSeatDistance.OrderBy(s => s.DistanceFromTheMiddle)
                                .ToList());
                            groupOfSeatDistance = new List<SeatWithDistanceFromTheMiddleOfTheRow> { seatWithDistance };
                            seatWithDistancePrevious = null;
                        }
                    }
                }
            }

            groupsOfSeatDistance.Add(groupOfSeatDistance);

            return groupsOfSeatDistance
                .Select(seatWithDistances => seatWithDistances.OrderBy(s => s.DistanceFromTheMiddle).ToList()).ToList();
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { _suggestionRequest };
        }
    }
}