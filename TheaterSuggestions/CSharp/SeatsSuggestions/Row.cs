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

        public SeatAllocation FindAllocation(int partyRequested, PricingCategory pricingCategory)
        {
            foreach (var seat in Seats)
            {
                if (seat.IsAvailable() && seat.MatchCategory(pricingCategory))
                {
                    var seatAllocation = new SeatAllocation(partyRequested, pricingCategory);

                    seatAllocation.AddSeat(seat);

                    if (seatAllocation.MatchExpectation())
                    {
                        return seatAllocation;
                    }
                }
            }

            return new AllocationNotAvailable(partyRequested, pricingCategory);
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] {Name, new ListByValue<Seat>(Seats)};
        }

        public Row UpdateSeat(Seat updatedSeat)
        {
            var newVersionOfSeats = new List<Seat>();

            foreach (var currentSeat in Seats)
            {
                if (currentSeat.SameSeatLocation(updatedSeat))
                {
                    newVersionOfSeats.Add(new Seat(updatedSeat.RowName, updatedSeat.Number, updatedSeat.PricingCategory,
                        updatedSeat.SeatAvailability));
                }
                else
                {
                    newVersionOfSeats.Add(currentSeat);
                }
            }

            return new Row(updatedSeat.RowName, newVersionOfSeats);
        }
    }
}