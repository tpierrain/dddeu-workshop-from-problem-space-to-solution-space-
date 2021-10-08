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
            var A1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var A2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);

            // Two different instances with same values should be equals
            var rowFirstInstance = new Row("A", new List<Seat> { A1, A2 });
            var rowSecondInstance = new Row("A", new List<Seat> { A1, A2 });
            Check.That(rowSecondInstance).IsEqualTo(rowFirstInstance);

            // Should not mutate existing instance 
            var A3 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            rowSecondInstance.AddSeat(A3);
            Check.That(rowSecondInstance).IsEqualTo(rowFirstInstance);
        }

        [Test]
        public void Suggest_seas_from_the_middle_of_row_is_even_when_party_size_is_greater_than_one()
        {
            var partySize = 2;

            var A1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var A2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var A3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var A4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var A5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var A6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var A7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var A8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var A9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
            var A10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { A1, A2, A3, A4, A5, A6, A7, A8, A9, A10 });

            Check.That(GetSuggestedSeats(row, partySize)).ContainsExactly(A5, A6);
        }

        [Test]
        public void Suggest_seas_from_the_middle_of_row_is_odd_when_party_size_is_greater_than_one()
        {
            var partySize = 5;

            var A1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
            var A2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
            var A3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
            var A4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
            var A5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
            var A6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
            var A7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
            var A8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
            var A9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);

            var row = new Row("A", new List<Seat> { A1, A2, A3, A4, A5, A6, A7, A8, A9 });

            Check.That(GetSuggestedSeats(row, partySize)).ContainsExactly(A2, A3, A5, A6, A7);
        }

        private static IOrderedEnumerable<Seat> GetSuggestedSeats(Row row, int partySize)
        {
            return ComputeDistancesForRow(row)
                .Select(s => s.Seat)
                .Where(s => s.IsAvailable())
                .Take(partySize)
                .OrderBy(s => s.Number);
        }

        private static IEnumerable<SeatDistance> ComputeDistancesForRow(Row row)
        {
            var theMiddleOfRow = GetTheMiddleOfRow(row);

            var seatsWithDistancesFromTheMiddle = Split(row.Seats, seat => IsMiddle(row.Seats, seat, theMiddleOfRow), theMiddleOfRow).ToList();
            
            seatsWithDistancesFromTheMiddle.Add(GetSeatsInTheMiddle(row, theMiddleOfRow));
            
            return seatsWithDistancesFromTheMiddle.SelectMany(s => s).OrderBy(s => s.Distance).ToList();
        }

        private static List<SeatDistance> GetSeatsInTheMiddle(Row row, int theMiddleOfRow)
        {
            return GetSeatsInTheMiddle(row.Seats, theMiddleOfRow).Select(s => new SeatDistance(s, 0)).ToList();
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

        public static IEnumerable<List<SeatDistance>> Split(List<Seat> seats, Func<Seat, bool> predicate, int middle)
        {
            var list = new List<SeatDistance>();
            foreach (var seat in seats)
            {
                if (!predicate(seat))
                {
                    var distance = 0;
                    if (seats.Count % 2 == 0)
                    {
                        if (seat.Number - middle > 0)
                            distance = (int)Math.Abs(seat.Number - (middle));
                        else
                        {
                            distance = (int)Math.Abs(seat.Number - (middle + 1));
                        }
                    }
                    else
                    {
                        distance = (int)Math.Abs(seat.Number - (middle));
                    }

                    list.Add(new SeatDistance(seat, distance));
                }
                else
                {
                    if (list.Count > 0)
                        yield return list;
                    list = new List<SeatDistance>();
                }
            }

            if (list.Count > 0)
                yield return list;
        }

        public class SeatDistance : ValueType<Seat>
        {
            public Seat Seat { get; }
            public int Distance { get; }

            public SeatDistance(Seat seat, int distance)
            {
                Seat = seat;
                Distance = distance;
            }

            protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
            {
                return new object[] { Seat, Distance };
            }
        }
    }
}