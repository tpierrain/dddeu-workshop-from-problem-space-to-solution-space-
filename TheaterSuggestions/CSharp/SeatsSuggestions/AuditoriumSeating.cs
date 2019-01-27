using System.Collections.Generic;

namespace SeatsSuggestions
{
    public class AuditoriumSeating
    {
        private Dictionary<string, Row> Rows { get; }

        public AuditoriumSeating(Dictionary<string, Row> rows)
        {
            Rows = rows;
        }

        public SeatAllocation MakeAllocationFor(int partyRequested, PricingCategory pricingCategory)
        {
            foreach (var row in Rows.Values)
            {
                var seatAllocation = row.FindAllocation(partyRequested, pricingCategory);

                if (seatAllocation.MatchExpectation())
                {
                    return seatAllocation;
                }
            }

            return new AllocationNotAvailable(partyRequested, pricingCategory);
        }
    }
}