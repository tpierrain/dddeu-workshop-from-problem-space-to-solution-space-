using System.Collections.Generic;
using System.Linq;
using Value;

namespace SeatsSuggestions.Domain
{
    /// <summary>
    ///     Occurs when a Suggestion is made.
    /// </summary>
    public class SuggestionMade : ValueType<SuggestionMade>, IProvideDomainEvent
    {
        private readonly List<Seat> _suggestedSeats;
        public PartyRequested PartyRequested { get; }
        public PricingCategory PricingCategory { get; }

        public IReadOnlyList<Seat> SuggestedSeats => _suggestedSeats;

        public SuggestionMade(PartyRequested partyRequested, PricingCategory pricingCategory, List<Seat> seats)
        {
            PartyRequested = partyRequested;
            PricingCategory = pricingCategory;
            _suggestedSeats = seats;
        }

        public IEnumerable<string> SeatNames()
        {
            return _suggestedSeats.Select(s => s.ToString());
        }

        public bool MatchExpectation()
        {
            return _suggestedSeats.Count == PartyRequested.PartySize;
        }

        protected override IEnumerable<object> GetAllAttributesToBeUsedForEquality()
        {
            return new object[] { PartyRequested, PricingCategory, new ListByValue<Seat>(_suggestedSeats) };
        }
    }
}