using System;
using System.Collections.Generic;
using System.Linq;
using Value;

namespace SeatsSuggestions.Model
{
    public class Row : ValueType<Row>
    {
        public Row(string name, List<Seat> seats)
        {
            Name = name;
            Seats = seats;
        }

        public string Name { get; }
        public List<Seat> Seats { get; }

        public Row AddSeat(Seat seat)
        {
            var updatedList = Seats.Select(s => s == seat ? seat : s).ToList();

            return new Row(Name, updatedList);
        }

        public SeatingOptionSuggested SuggestSeatingOption(SuggestionRequest suggestionRequest)
        {
            var seatingOptionSuggested = new SeatingOptionSuggested(suggestionRequest);

            foreach (var seat in SuggestAdjacentSeatsNearedTheMiddleOfRow(suggestionRequest))
            {
                seatingOptionSuggested.AddSeat(seat);

                if (seatingOptionSuggested.MatchExpectation()) return seatingOptionSuggested;
            }

            return new SeatingOptionNotAvailable(suggestionRequest);
        }

        public Row Allocate(Seat seat)
        {
            var newVersionOfSeats = new List<Seat>();

            foreach (var currentSeat in Seats)
                if (currentSeat.SameSeatLocation(seat))
                    newVersionOfSeats.Add(new Seat(seat.RowName, seat.Number, seat.PricingCategory,
                        SeatAvailability.Allocated));
                else
                    newVersionOfSeats.Add(currentSeat);

            return new Row(seat.RowName, newVersionOfSeats);
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { Name, new ListByValue<Seat>(Seats) };
        }

        public List<Seat> SuggestAdjacentSeatsNearedTheMiddleOfRow(SuggestionRequest suggestionRequest)
        {
            var seatsWithDistances = SuggestSeatsNearerTheMiddleOfTheRow(suggestionRequest.PricingCategory);

            return SuggestAdjacentSeats(seatsWithDistances);
        }

        private static List<Seat> SuggestAdjacentSeats(IEnumerable<SeatWithDistance> seatsWithDistances)
        {
            var groupOfAdjacentSeats = SplitInGroupsOfAdjacentSeats(seatsWithDistances);
            return SelectGroupsWithShorterDistanceFromTheMiddleOfTheRow(groupOfAdjacentSeats).ToList();
        }

        private IEnumerable<SeatWithDistance> SuggestSeatsNearerTheMiddleOfTheRow(PricingCategory pricingCategory)
        {
            return ComputeDistancesNearerTheMiddleOfTheRow()
                .Where(seatWithDistance => seatWithDistance.Seat.MatchCategory(pricingCategory))
                .Where(seatWithDistance => seatWithDistance.Seat.IsAvailable());
        }

        private static IEnumerable<Seat> SelectGroupsWithShorterDistanceFromTheMiddleOfTheRow(
            IEnumerable<List<SeatWithDistance>> groupOfAdjacentSeats)
        {
            var bestDistances = new SortedDictionary<int, List<SeatWithDistance>>();
            foreach (var seatWithDistances in groupOfAdjacentSeats)
            {
                var sumOfDistances = seatWithDistances.Sum(s => s.DistanceFromTheMiddle);
                if (!bestDistances.ContainsKey(sumOfDistances))
                {
                    bestDistances.Add(sumOfDistances, seatWithDistances);
                }
            }

            return bestDistances.Count == 0 ? 
                new List<Seat>() 
                : bestDistances.Values.First().Select(seatsWithDistance => seatsWithDistance.Seat).ToList();
        }

        private static IEnumerable<List<SeatWithDistance>> SplitInGroupsOfAdjacentSeats(
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
                            groupsOfSeatDistance.Add(groupOfSeatDistance.OrderBy(s => s.DistanceFromTheMiddle).ToList());
                            groupOfSeatDistance = new List<SeatWithDistance> { seatWithDistance };
                            seatWithDistancePrevious = null;
                        }
                    }
                }
            }

            groupsOfSeatDistance.Add(groupOfSeatDistance);

            return groupsOfSeatDistance.Select(seatWithDistances => seatWithDistances.OrderBy(s => s.DistanceFromTheMiddle).ToList()).ToList();
        }

        private IEnumerable<SeatWithDistance> ComputeDistancesNearerTheMiddleOfTheRow()
        {
            var theMiddleOfRow = GetTheMiddleOfRow();

            var seatsWithDistancesFromTheMiddle =
                SplitSeatsByDistanceNearerTheMiddleOfTheRow(seat => IsMiddle(seat, theMiddleOfRow), theMiddleOfRow).ToList();

            seatsWithDistancesFromTheMiddle.Add(GetSeatsInTheMiddle(theMiddleOfRow));

            return seatsWithDistancesFromTheMiddle.SelectMany(s => s).OrderBy(s => s.DistanceFromTheMiddle).ToList();
        }

        private List<SeatWithDistance> GetSeatsInTheMiddle(int theMiddleOfRow)
        {
            return GetSeatsInTheMiddle(Seats, theMiddleOfRow).Select(s => new SeatWithDistance(s, 0)).ToList();
        }

        private int GetTheMiddleOfRow()
        {
            return Seats.Count % 2 == 0 ? Seats.Count / 2 : Math.Abs(Seats.Count / 2) + 1;
        }

        private static List<Seat> GetSeatsInTheMiddle(IReadOnlyList<Seat> seats, int middle)
        {
            return seats.Count % 2 == 0
                ? new List<Seat> { seats[middle - 1], seats[middle] }
                : new List<Seat> { seats[middle - 1] };
        }

        private  bool IsMiddle(Seat seat, int middle)
        {
            switch (Seats.Count % 2)
            {
                case 0 when Math.Abs(seat.Number - middle) == 0:
                case 0 when seat.Number - (middle + 1) == 0:
                    return true;
                default:
                    return Math.Abs(seat.Number - middle) == 0;
            }
        }

        private IEnumerable<List<SeatWithDistance>> SplitSeatsByDistanceNearerTheMiddleOfTheRow(Func<Seat, bool> predicate,
            int middle)
        {
            var seatWithDistances = new List<SeatWithDistance>();
            foreach (var seat in Seats)
                if (!predicate(seat))
                {
                    int distance;
                    if (Seats.Count % 2 == 0)
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
    }
}