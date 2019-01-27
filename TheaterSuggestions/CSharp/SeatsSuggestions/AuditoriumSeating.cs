using System.Collections.Generic;

namespace SeatsSuggestions
{
    public class AuditoriumSeating
    {
        public IReadOnlyDictionary<string, Row> Rows => _rows;

        private readonly Dictionary<string, Row> _rows;

        public AuditoriumSeating(Dictionary<string, Row> rows)
        {
            _rows = rows;
        }

        public SeatAllocation MakeAllocationFor(int partyRequested, PricingCategory pricingCategory)
        {
            foreach (var row in _rows.Values)
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