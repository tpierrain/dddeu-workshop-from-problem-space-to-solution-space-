﻿using System.Collections;
using System.Collections.Generic;
using System.Linq;
using Value;

namespace SeatsSuggestions.Domain.DeepModel
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

        private static IEnumerable<Seat> NoSeatSuggested { get; } = new List<Seat>();

        public IEnumerable<Seat> OfferAdjacentSeats(
            IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances)
        {
            return SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(SplitInGroupsOfAdjacentSeats(seatsWithDistances));
        }

        private IEnumerable<Seat> SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
            IEnumerable<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupOfAdjacentSeats)
        {
            var theBestDistancesNearerToTheMiddleOfTheRowPerGroup =
                new SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>>();

            // To select the best group of adjacent seats, we sort them by their distances
            foreach (var seatsWithDistanceFromMiddleOfTheRow in sortSeatsByDistanceFromMiddleOfTheRow(
                groupOfAdjacentSeats))
            {
                if (!IsMatchingPartyRequested(_suggestionRequest, seatsWithDistanceFromMiddleOfTheRow)) continue;

                var sumOfDistances = seatsWithDistanceFromMiddleOfTheRow.Sum(s => s.DistanceFromTheMiddleOfTheRow);

                if (!theBestDistancesNearerToTheMiddleOfTheRowPerGroup.ContainsKey(sumOfDistances))
                    theBestDistancesNearerToTheMiddleOfTheRowPerGroup[sumOfDistances] =
                        new List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>();

                theBestDistancesNearerToTheMiddleOfTheRowPerGroup[sumOfDistances]
                    .Add(seatsWithDistanceFromMiddleOfTheRow);
            }

            return theBestDistancesNearerToTheMiddleOfTheRowPerGroup.Any()
                ? SelectTheBestGroup(theBestDistancesNearerToTheMiddleOfTheRowPerGroup)
                : NoSeatSuggested;
        }

        private static List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> sortSeatsByDistanceFromMiddleOfTheRow(
            IEnumerable<IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupOfAdjacentSeats)
        {
            return groupOfAdjacentSeats
                .Select(seatWithDistances => seatWithDistances.OrderBy(s => s.DistanceFromTheMiddleOfTheRow).ToList())
                .ToList();
        }

        private static IEnumerable<Seat> SelectTheBestGroup(
            SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups)
        {
            return HasTheBestGroupWithoutConflict(bestGroups)
                ? ProjectToSeats(bestGroups)
                : DecideBetweenIdenticalScores(bestGroups);
        }

        private static IEnumerable<Seat> DecideBetweenIdenticalScores(
            SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups)
        {
            var decideBetweenIdenticalScores = PopulateTheBestGroups(bestGroups);

            return SelectTheGroupWhoseSizeIsTheLargestWithEqualScore(decideBetweenIdenticalScores);
        }

        private static SortedDictionary<int, IEnumerable<Seat>> PopulateTheBestGroups(
            SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups)
        {
            SortedDictionary<int, IEnumerable<Seat>> decideBetweenIdenticalScores = new();

            foreach (var seatWithTheDistanceFromTheMiddleOfTheRows in bestGroups
                .Values.SelectMany(collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows => collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows))
                decideBetweenIdenticalScores[seatWithTheDistanceFromTheMiddleOfTheRows.Count] = ProjectToSeats(seatWithTheDistanceFromTheMiddleOfTheRows);

            return decideBetweenIdenticalScores;
        }

        private static IEnumerable<Seat> ProjectToSeats(
            SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups)
        {
            return ProjectToSeats(bestGroups.Values.First()[0]);
        }

        private static IEnumerable<Seat> ProjectToSeats(
            IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows)
        {
            return seatWithTheDistanceFromTheMiddleOfTheRows
                .Select(seatWithTheDistanceFromTheMiddleOfTheRow => seatWithTheDistanceFromTheMiddleOfTheRow.Seat);

        }

        private static bool HasTheBestGroupWithoutConflict(
            SortedDictionary<int, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups)
        {
            // if the first entry is alone, there is no conflict
            return bestGroups.Values.First().Count == 1;
        }

        private static IEnumerable<Seat> SelectTheGroupWhoseSizeIsTheLargestWithEqualScore(
            SortedDictionary<int, IEnumerable<Seat>> groupsWithHighScores)
        {
            return groupsWithHighScores.LastOrDefault().Value;
        }

        private static bool IsMatchingPartyRequested(SuggestionRequest suggestionRequest, ICollection seatWithDistances)
        {
            return seatWithDistances.Count >= suggestionRequest.PartyRequested.PartySize;
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
                            groupOfSeatDistance = 
                                new List<SeatWithTheDistanceFromTheMiddleOfTheRow> { seatWithDistance };
                            seatWithTheDistancePrevious = null;
                        }
                    }
                }
            }

            groupsOfSeatDistance.Add(groupOfSeatDistance);


            return groupsOfSeatDistance;
        }

        private static IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> OrderSeatsByTheirNumberToGroupAdjacent(
            IEnumerable<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances)
        {
            return seatsWithDistances.OrderBy(s => s.Seat.Number);
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { _suggestionRequest };
        }
    }
}