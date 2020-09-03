using System.Collections.Generic;

namespace SeatsSuggestions.Domain
{
    public class SuggestionsBuilder
    {
        private const int NumberOfSuggestionsPerPricingCategory = 3;

        public SuggestionsBuilder(PartyRequested partyRequested, AuditoriumSeating auditoriumSeating)
        {
            PartyRequested = partyRequested;
            AuditoriumSeating = auditoriumSeating;
        }

        public AuditoriumSeating AuditoriumSeating { get; set; }

        public PartyRequested PartyRequested { get; set; }

        public IEnumerable<SuggestionMade> BuildSuggestions(PricingCategory pricingCategory)
        {
            return GiveMeSuggestionsFor(AuditoriumSeating, PartyRequested, pricingCategory);
        }

        private static IEnumerable<SuggestionMade> GiveMeSuggestionsFor(AuditoriumSeating auditoriumSeating, PartyRequested partyRequested, PricingCategory pricingCategory)
        {
            var foundedSuggestions = new List<SuggestionMade>();

            for (var i = 0; i < NumberOfSuggestionsPerPricingCategory; i++)
            {
                var seatOptionsSuggested = auditoriumSeating.SuggestSeatingOptionFor(new SuggestionRequest(partyRequested, pricingCategory));

                if (seatOptionsSuggested.MatchExpectation())
                {
                    // We get the new version of the Auditorium after the allocation
                    auditoriumSeating = auditoriumSeating.Allocate(seatOptionsSuggested);

                    foundedSuggestions.Add(new SuggestionMade(partyRequested, pricingCategory, seatOptionsSuggested.Seats));
                }
            }

            return foundedSuggestions;
        }
    }
}