using System.Collections.Generic;
using System.Threading.Tasks;
using SeatsSuggestions.Domain.Port;

namespace SeatsSuggestions.Domain
{
    public class SeatAllocator : IProvideAuditoriumSeating
    {
        private const int NumberOfSuggestionsPerPricingCategory = 3;
        private readonly IRetrieveAuditoriumSeating _retrieveAuditoriumSeating;

        public async Task<SuggestionsMade> MakeSuggestions(ShowId showId, PartyRequested partyRequested)
        {
            var suggestionsMade = new SuggestionsMade(showId, partyRequested);

            var auditoriumSeating = await _retrieveAuditoriumSeating.GetById(showId);

            foreach (var pricingCategory in new List<PricingCategory> { PricingCategory.First, PricingCategory.Second, 
                                                                        PricingCategory.Third, PricingCategory.Mixed })
            {
                suggestionsMade.Add(GiveMeSuggestionsFor(auditoriumSeating, partyRequested, pricingCategory));
            }

            _retrieveAuditoriumSeating.Save(auditoriumSeating);

            return suggestionsMade.MatchExpectations() ? suggestionsMade : new SuggestionNotAvailable(showId, partyRequested);
        }

        public SeatAllocator(IRetrieveAuditoriumSeating retrieveAuditoriumSeating)
        {
            _retrieveAuditoriumSeating = retrieveAuditoriumSeating;
        }

        private static IEnumerable<SuggestionMade> GiveMeSuggestionsFor(
            AuditoriumSeating auditoriumSeating,
            PartyRequested partyRequested,
            PricingCategory pricingCategory)
        {
            var foundedSuggestions = new List<SuggestionMade>();

            for (var i = 0; i < NumberOfSuggestionsPerPricingCategory; i++)
            {
                var seatOptionsSuggested = auditoriumSeating
                    .SuggestSeatingOptionFor(new SuggestionRequest(partyRequested, pricingCategory));

                if (seatOptionsSuggested.MatchExpectation())
                {
                    // We get the new version of the Auditorium after the allocation
                    auditoriumSeating = auditoriumSeating.Allocate(seatOptionsSuggested);

                    foundedSuggestions.Add(new SuggestionMade(partyRequested, pricingCategory,
                        seatOptionsSuggested.Seats));
                }
            }

            return foundedSuggestions;
        }
    }
}