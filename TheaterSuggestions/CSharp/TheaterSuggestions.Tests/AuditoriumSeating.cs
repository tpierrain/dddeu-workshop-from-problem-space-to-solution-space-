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

        public SuggestionMade MakeSuggestionFor(int partyRequested, PricingCategory pricingCategory)
        {
            foreach (var row in Rows.Values)
            {
                var seatAllocation = row.FindAllocation(partyRequested, pricingCategory);

                if (seatAllocation.MatchExpectation())
                {
                    // Cool, we mark the seat as Suggested (that we turns into a SuggestionMode)
                    return seatAllocation.ConfirmInterest();
                }
            }

            return new NotSuggestionMatchedExpectation(partyRequested, pricingCategory);
        }
    }
}