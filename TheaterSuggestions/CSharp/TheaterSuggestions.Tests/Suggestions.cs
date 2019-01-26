using System.Collections.Generic;

namespace SeatsSuggestions.Tests
{
    public class Suggestions
    {
        public string ShowId { get; }

        public Dictionary<PricingCategory, List<Suggestion>> ProposalsPerCategory { get; } =
            new Dictionary<PricingCategory, List<Suggestion>>();

        public Suggestions(string showId)
        {
            ShowId = showId;
        }

        public void AddSuggestion(PricingCategory pricingCategory, Suggestion suggestion)
        {
            if (!ProposalsPerCategory.ContainsKey(pricingCategory))
            {
                ProposalsPerCategory[pricingCategory] = new List<Suggestion>();
            }

            ProposalsPerCategory[pricingCategory].Add(suggestion);
        }
    }
}