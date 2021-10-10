using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using NFluent;
using NUnit.Framework;
using SeatsSuggestions.Model;
using Value;

namespace SeatsSuggestions.Tests.UnitTests
{
    [TestFixture]
    public class RowShould
    {
        [Test]
        public void Be_a_Value_Type()
        {
            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);

            // Two different instances with same values should be equals
            var rowFirstInstance = new Row("A", new List<Seat> { a1, a2 });
            var rowSecondInstance = new Row("A", new List<Seat> { a1, a2 });
            Check.That(rowSecondInstance).IsEqualTo(rowFirstInstance);

            // Should not mutate existing instance 
            var a3 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            rowSecondInstance.AddSeat(a3);
            Check.That(rowSecondInstance).IsEqualTo(rowFirstInstance);
        }

        [Test]
        public void Suggest_seas_from_the_middle_of_row_is_even_when_party_size_is_greater_than_one()
        {
            var partySize = 2;

            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
            var a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 });

            Check.That(SuggestSeatsNearTheMiddleOfRow(row, partySize)).ContainsExactly(a5, a6);
        }

        [Test]
        public void Suggest_seats_from_the_middle_of_row_is_odd_when_party_size_is_greater_than_one()
        {
            var partySize = 5;

            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9 });

            Check.That(SuggestSeatsNearTheMiddleOfRow(row, partySize)).ContainsExactly(a2, a3, a5, a6, a7);
        }

        [Test]
        public void Suggest_seats_from_the_middle_of_row_is_odd_when_party_size_and_pricing_category_are_requested()
        {
            var partySize = 5;

            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9 });

            Check.That(SuggestSeatsNearTheMiddleOfRow(row, partySize, PricingCategory.First)).ContainsExactly(a3, a5, a6, a7);
        }
        [Test]
        public void Suggest_groups_of_adjacent_seats_when_row_contains_some_reserved_seats()
        {
            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
            var a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 });

            var seatWithDistances = ComputeDistancesForRow(row);
            var withDistances = seatWithDistances.Where(s => s.Seat.IsAvailable());
            var groupOfAdjacentSeats = RetrieveGroupOfAdjacentSeats(withDistances);

            Check.That(groupOfAdjacentSeats).HasSize(3);
            Check.That(groupOfAdjacentSeats[0][0].Seat).IsEqualTo(a1);
            Check.That(groupOfAdjacentSeats[0][1].Seat).IsEqualTo(a2);
            Check.That(groupOfAdjacentSeats[0][2].Seat).IsEqualTo(a3);

            Check.That(groupOfAdjacentSeats[1][0].Seat).IsEqualTo(a5);
            Check.That(groupOfAdjacentSeats[1][1].Seat).IsEqualTo(a6);
            Check.That(groupOfAdjacentSeats[1][2].Seat).IsEqualTo(a7);


            Check.That(groupOfAdjacentSeats[2][0].Seat).IsEqualTo(a9);
            Check.That(groupOfAdjacentSeats[2][1].Seat).IsEqualTo(a10);

            var seats = SelectGroupsWithShorterDistanceOfMiddleOfTheRow(groupOfAdjacentSeats, 3);

            Check.That(seats).ContainsExactly(a5, a6, a7);
        }


        [Test]
        public void Suggest_adjacent_seats_nearer_the_middle_of_row()
        {
            var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
            var a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 });

            var seats = row.SuggestAdjacentSeatsNearedTheMiddleOfRow(new SuggestionRequest(3, PricingCategory.Mixed));

            Check.That(seats).ContainsExactly(a5, a6, a7);
        }

        private static List<Seat> SelectGroupsWithShorterDistanceOfMiddleOfTheRow(IEnumerable<List<SeatWithDistance>> groupOfAdjacentSeats, int partySize)
        {
            var subGroups = groupOfAdjacentSeats.Where(g => g.Count >= partySize);
            
            var bestDistances = new SortedDictionary<int, List<SeatWithDistance>>();
            foreach (var seatWithDistances in subGroups)
            {
                bestDistances.Add(seatWithDistances.Sum(s => s.DistanceFromTheMiddle), seatWithDistances);
            }

            return bestDistances.Values.First().Select(seatsWithDistance => seatsWithDistance.Seat).ToList();
        }
        private static IOrderedEnumerable<Seat> SuggestSeatsNearTheMiddleOfRow(Row row, int partySize,
            PricingCategory pricingCategory = PricingCategory.Mixed)
        {
            return ComputeDistancesForRow(row)
            .Select(s => s.Seat)
            .Where(s => s.IsAvailable())
            .Where(s => MatchCurrentPricingCategory(s, pricingCategory))
            .Take(partySize)
            .OrderBy(s => s.Number);
            
        }

        private static bool MatchCurrentPricingCategory(Seat seat, PricingCategory pricingCategory)
        {
            return seat.PricingCategory == pricingCategory || pricingCategory == PricingCategory.Mixed;
        }

        private static List<List<SeatWithDistance>> RetrieveGroupOfAdjacentSeats(
            IEnumerable<SeatWithDistance> seatsWithDistances)
        {
            var groupOfSeatDistance = new List<SeatWithDistance>();
            var groupsOfSeatDistance = new List<List<SeatWithDistance>>();

            using (var enumerator = seatsWithDistances.OrderBy(s => s.Seat.Number).GetEnumerator())
            {
                SeatWithDistance seatWithDistancePrevious = null;

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
                            groupOfSeatDistance = new List<SeatWithDistance> { seatWithDistance };
                            seatWithDistancePrevious = null;
                        }
                    }
                }
            }

            groupsOfSeatDistance.Add(groupOfSeatDistance);

            return groupsOfSeatDistance;
        }

        private static IEnumerable<SeatWithDistance> ComputeDistancesForRow(Row row)
        {
            var theMiddleOfRow = GetTheMiddleOfRow(row);

            var seatsWithDistancesFromTheMiddle =
                Split(row.Seats, seat => IsMiddle(row.Seats, seat, theMiddleOfRow), theMiddleOfRow).ToList();

            seatsWithDistancesFromTheMiddle.Add(GetSeatsInTheMiddle(row, theMiddleOfRow));

            return seatsWithDistancesFromTheMiddle.SelectMany(s => s).OrderBy(s => s.DistanceFromTheMiddle).ToList();
        }

        private static List<SeatWithDistance> GetSeatsInTheMiddle(Row row, int theMiddleOfRow)
        {
            return GetSeatsInTheMiddle(row.Seats, theMiddleOfRow).Select(s => new SeatWithDistance(s, 0)).ToList();
        }

        private static int GetTheMiddleOfRow(Row row)
        {
            return row.Seats.Count % 2 == 0 ? row.Seats.Count / 2 : Math.Abs(row.Seats.Count / 2) + 1;
        }

        private static List<Seat> GetSeatsInTheMiddle(IReadOnlyList<Seat> seats, int middle)
        {
            return seats.Count % 2 == 0
                ? new List<Seat> { seats[middle - 1], seats[middle] }
                : new List<Seat> { seats[middle - 1] };
        }

        private static bool IsMiddle(ICollection seats, Seat seat, int middle)
        {
            switch (seats.Count % 2)
            {
                case 0 when Math.Abs(seat.Number - middle) == 0:
                case 0 when seat.Number - (middle + 1) == 0:
                    return true;
                default:
                    return Math.Abs(seat.Number - middle) == 0;
            }
        }

        public static IEnumerable<List<SeatWithDistance>> Split(List<Seat> seats, Func<Seat, bool> predicate,
            int middle)
        {
            var seatWithDistances = new List<SeatWithDistance>();
            foreach (var seat in seats)
                if (!predicate(seat))
                {
                    int distance;
                    if (seats.Count % 2 == 0)
                    {
                        if (seat.Number - middle > 0)
                            distance = (int)Math.Abs(seat.Number - middle);
                        else
                            distance = (int)Math.Abs(seat.Number - (middle + 1));
                    }
                    else
                    {
                        distance = (int)Math.Abs(seat.Number - middle);
                    }

                    seatWithDistances.Add(new SeatWithDistance(seat, distance));
                }
                else
                {
                    if (seatWithDistances.Count > 0)
                        yield return seatWithDistances;
                    seatWithDistances = new List<SeatWithDistance>();
                }

            if (seatWithDistances.Count > 0)
                yield return seatWithDistances;
        }

        public class SeatWithDistance : ValueType<Seat>
        {
            public SeatWithDistance(Seat seat, int distanceFromTheMiddle)
            {
                Seat = seat;
                DistanceFromTheMiddle = distanceFromTheMiddle;
            }

            public Seat Seat { get; }
            public int DistanceFromTheMiddle { get; }

            public override string ToString()
            {
                return $"{Seat.RowName}{Seat.Number}";
            }

            protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
            {
                return new object[] { Seat, DistanceFromTheMiddle };
            }
        }
    }
}