using System.Threading.Tasks;
using SeatsSuggestions.Domain.Ports;

namespace SeatsSuggestions.Domain
{
    /// <summary>
    ///     The Hexagon.
    /// </summary>
    public class SeatAllocator : IRequestSuggestions
    {
        private readonly IProvideUpToDateAuditoriumSeating _auditoriumSeatingProvider;

        public async Task<SuggestionsMade> MakeSuggestions(ShowId showId, PartyRequested partyRequested)
        {
            var auditoriumSeating = await _auditoriumSeatingProvider.GetAuditoriumSeating(showId);
            var suggestionsBuilder = new SuggestionsBuilder(partyRequested, auditoriumSeating);

            var suggestionsMade = new SuggestionsMade(showId, partyRequested);
            suggestionsMade.Add(suggestionsBuilder.BuildSuggestions(PricingCategory.First));
            suggestionsMade.Add(suggestionsBuilder.BuildSuggestions(PricingCategory.Second));
            suggestionsMade.Add(suggestionsBuilder.BuildSuggestions(PricingCategory.Third));
            suggestionsMade.Add(suggestionsBuilder.BuildSuggestions(PricingCategory.Mixed));

            if (suggestionsMade.MatchExpectations())
            {
                return suggestionsMade;
            }

            return new SuggestionNotAvailable(showId, partyRequested);
        }

        public SeatAllocator(IProvideUpToDateAuditoriumSeating auditoriumSeatingProvider)
        {
            _auditoriumSeatingProvider = auditoriumSeatingProvider;
        }
    }
}