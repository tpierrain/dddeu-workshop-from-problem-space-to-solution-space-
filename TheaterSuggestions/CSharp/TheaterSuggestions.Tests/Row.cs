using System.Collections.Generic;

namespace SeatsSuggestions.Tests
{
    public class Row
    {
        public string Name { get; set; }
        public List<Seat> Seats { get; set; } = new List<Seat>();

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