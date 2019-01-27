using System.Collections.Generic;

namespace SeatsSuggestions
{
    public class Row
    {
        public string Name { get; }
        public List<Seat> Seats { get; }

        public Row(string name, List<Seat> seats)
        {
            Name = name;
            Seats = seats;
        }

        public void AddSeat(Seat seat)
        {
            Seats.Add(seat);
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
    }
}