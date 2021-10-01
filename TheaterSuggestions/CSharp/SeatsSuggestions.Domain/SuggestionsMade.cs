using System;
using System.Collections.Generic;
using System.Linq;
using Value;
using Value.Shared;

namespace SeatsSuggestions.Domain
{
    /// <summary>
    ///     Occurs when a bunch of Suggestion are made.
    /// </summary>
    public class SuggestionsMade: ValueType<SuggestionsMade>, IProvideDomainEvent
    {
        public ShowId ShowId { get; }
        public PartyRequested PartyRequested { get; }

        private Dictionary<PricingCategory, ListByValue<SuggestionMade>> ForCategory { get; } =
            new Dictionary<PricingCategory, ListByValue<SuggestionMade>>();

        public IEnumerable<string> SeatsInFirstPricingCategory => SeatNames(PricingCategory.First);
        public IEnumerable<string> SeatsInSecondPricingCategory => SeatNames(PricingCategory.Second);
        public IEnumerable<string> SeatsInThirdPricingCategory => SeatNames(PricingCategory.Third);
        public IEnumerable<string> SeatsInMixedPricingCategory => SeatNames(PricingCategory.Mixed);

        public SuggestionsMade(ShowId showId, PartyRequested partyRequested)
        {
            ShowId = showId;
            PartyRequested = partyRequested;
            InstantiateAnEmptyListForEveryPricingCategory();
        }

        public IEnumerable<string> SeatNames(PricingCategory pricingCategory)
        {
            var suggestionsMade = ForCategory[pricingCategory];
            return suggestionsMade.Select(s => string.Join("-", s.SeatNames()));
        }

        private void InstantiateAnEmptyListForEveryPricingCategory()
        {
            foreach (PricingCategory pricingCategory in Enum.GetValues(typeof(PricingCategory)))
                ForCategory[pricingCategory] = new ListByValue<SuggestionMade>();
        }

        public void Add(IEnumerable<SuggestionMade> suggestions)
        {
            foreach (var suggestionMade in suggestions) ForCategory[suggestionMade.PricingCategory].Add(suggestionMade);
        }

        public bool MatchExpectations()
        {
            return ForCategory.SelectMany(s => s.Value).Any(x => x.MatchExpectation());
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { ShowId, PartyRequested, new DictionaryByValue<PricingCategory, ListByValue<SuggestionMade>>(ForCategory) };
        }
    }
}