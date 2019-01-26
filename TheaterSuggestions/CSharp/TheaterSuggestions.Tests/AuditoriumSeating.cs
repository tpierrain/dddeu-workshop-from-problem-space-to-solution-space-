using System.Collections.Generic;

namespace SeatsSuggestions.Tests
{
    public class AuditoriumSeating
    {
        public readonly Dictionary<string, Row> Rows;

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