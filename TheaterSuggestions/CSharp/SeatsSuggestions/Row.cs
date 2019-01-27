using System.Collections.Generic;
using System.Linq;
using Value;

namespace SeatsSuggestions
{
    public class Row : ValueType<Row>
    {
        public string Name { get; }
        public List<Seat> Seats { get; }

        public Row(string name, List<Seat> seats)
        {
            Name = name;
            Seats = seats;
        }

        public Row AddSeat(Seat seat)
        {
            var updatedList = Seats.Select(s => s == seat ? seat : s).ToList();

            return new Row(Name, updatedList);
        }

        public SeatOptionsSuggested SuggestSeatingOption(int partyRequested, PricingCategory pricingCategory)
        {
            foreach (var seat in Seats)
            {
                if (seat.IsAvailable() && seat.MatchCategory(pricingCategory))
                {
                    var seatAllocation = new SeatOptionsSuggested(partyRequested, pricingCategory);

                    seatAllocation.AddSeat(seat);

                    if (seatAllocation.MatchExpectation())
                    {
                        return seatAllocation;
                    }
                }
            }

            return new SeatingOptionNotAvailable(partyRequested, pricingCategory);
        }

        public Row Allocate(Seat seat)
        {
            var newVersionOfSeats = new List<Seat>();

            foreach (var currentSeat in Seats)
            {
                if (currentSeat.SameSeatLocation(seat))
                {
                    newVersionOfSeats.Add(new Seat(seat.RowName, seat.Number, seat.PricingCategory,
                        SeatAvailability.Allocated));
                }
                else
                {
                    newVersionOfSeats.Add(currentSeat);
                }
            }

            return new Row(seat.RowName, newVersionOfSeats);
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] {Name, new ListByValue<Seat>(Seats)};
        }
    }
}