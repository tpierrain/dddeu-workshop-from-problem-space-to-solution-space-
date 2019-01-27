using System.Collections.Generic;

namespace SeatsSuggestions
{
    internal class NotSuggestionMatchedExpectation : SuggestionMade
    {
        public NotSuggestionMatchedExpectation(int partyRequested, PricingCategory pricingCategory) : base(
            partyRequested, pricingCategory, new List<Seat>())
        {
        }
    }
}