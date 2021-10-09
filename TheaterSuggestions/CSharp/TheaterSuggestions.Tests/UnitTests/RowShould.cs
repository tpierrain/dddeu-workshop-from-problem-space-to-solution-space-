using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using NFluent;
using NUnit.Framework;
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

            Check.That(RetrieveSuggestedSeats(row, partySize)).ContainsExactly(a5, a6);
        }

        [Test]
        public void Suggest_seas_from_the_middle_of_row_is_odd_when_party_size_is_greater_than_one()
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

            Check.That(RetrieveSuggestedSeats(row, partySize)).ContainsExactly(a2, a3, a5, a6, a7);
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
            var computeGroupOfAdjacentSeats = RetrieveGroupOfAdjacentSeats(withDistances);

            Check.That(computeGroupOfAdjacentSeats).HasSize(3);
            Check.That(computeGroupOfAdjacentSeats[0][0].Seat).IsEqualTo(a1);
            Check.That(computeGroupOfAdjacentSeats[0][1].Seat).IsEqualTo(a2);
            Check.That(computeGroupOfAdjacentSeats[0][2].Seat).IsEqualTo(a3);

            Check.That(computeGroupOfAdjacentSeats[1][0].Seat).IsEqualTo(a5);
            Check.That(computeGroupOfAdjacentSeats[1][1].Seat).IsEqualTo(a6);
            Check.That(computeGroupOfAdjacentSeats[1][2].Seat).IsEqualTo(a7);


            Check.That(computeGroupOfAdjacentSeats[2][0].Seat).IsEqualTo(a9);
            Check.That(computeGroupOfAdjacentSeats[2][1].Seat).IsEqualTo(a10);
        }


        private static IOrderedEnumerable<Seat> RetrieveSuggestedSeats(Row row, int partySize)
        {
            return ComputeDistancesForRow(row)
                .Select(s => s.Seat)
                .Where(s => s.IsAvailable())
                .Take(partySize)
                .OrderBy(s => s.Number);
        }

        private static List<List<SeatWithDistance>> RetrieveGroupOfAdjacentSeats(
            IEnumerable<SeatWithDistance> seatsWithDistances)
        {
            var groupSeatDistance = new List<SeatWithDistance>();
            var groups = new List<List<SeatWithDistance>>();

            using (var enumerator = seatsWithDistances.OrderBy(s => s.Seat.Number).GetEnumerator())
            {
                SeatWithDistance seatPrevious = null;
                while (enumerator.MoveNext())
                {
                    var seatWithDistance = enumerator.Current;
                    if (seatPrevious == null)
                    {
                        seatPrevious = seatWithDistance;
                        groupSeatDistance.Add(seatPrevious);
                    }
                    else
                    {
                        if (seatWithDistance?.Seat.Number == seatPrevious.Seat.Number + 1)
                        {
                            groupSeatDistance.Add(seatWithDistance);
                            seatPrevious = seatWithDistance;
                        }
                        else
                        {
                            groups.Add(groupSeatDistance);
                            groupSeatDistance = new List<SeatWithDistance> { seatWithDistance };
                            seatPrevious = null;
                        }
                    }
                }
            }

            groups.Add(groupSeatDistance);

            return groups;
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
            var list = new List<SeatWithDistance>();
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

                    list.Add(new SeatWithDistance(seat, distance));
                }
                else
                {
                    if (list.Count > 0)
                        yield return list;
                    list = new List<SeatWithDistance>();
                }

            if (list.Count > 0)
                yield return list;
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