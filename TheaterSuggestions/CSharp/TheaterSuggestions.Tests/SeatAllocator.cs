using System.Collections.Generic;

namespace SeatsSuggestions.Tests
{
    public class SeatAllocator
    {
        private readonly AuditoriumSeatingAdapter _auditoriumSeatingAdapter;

        public SeatAllocator(AuditoriumSeatingAdapter auditoriumSeatingAdapter)
        {
            _auditoriumSeatingAdapter = auditoriumSeatingAdapter;
        }

        public SuggestionsMade MakeSuggestions(string showId, int partyRequested)
        {
            var auditoriumSeating = _auditoriumSeatingAdapter.GetAuditoriumSeating(showId);

            var suggestionsMade = new SuggestionsMade(showId, partyRequested);

            var numberOfSuggestions = 3;

            suggestionsMade.Add(GiveMeSeveralSuggestionFor(partyRequested, auditoriumSeating, numberOfSuggestions,
                PricingCategory.First));
            suggestionsMade.Add(GiveMeSeveralSuggestionFor(partyRequested, auditoriumSeating, numberOfSuggestions,
                PricingCategory.Second));
            suggestionsMade.Add(GiveMeSeveralSuggestionFor(partyRequested, auditoriumSeating, numberOfSuggestions,
                PricingCategory.Third));
            suggestionsMade.Add(GiveMeSeveralSuggestionFor(partyRequested, auditoriumSeating, numberOfSuggestions,
                PricingCategory.Mixed));

            if (!suggestionsMade.MatchExpectations())
            {
                return new SuggestionNotAvailable(showId, partyRequested);
            }

            return suggestionsMade;
        }

        private static IReadOnlyCollection<SuggestionMade> GiveMeSeveralSuggestionFor(int partyRequested,
            AuditoriumSeating auditoriumSeating, int numberOfSuggestions, PricingCategory pricingCategory)
        {
            var foundedSuggestions = new List<SuggestionMade>();
            for (var i = 0; i < numberOfSuggestions; i++)
            {
                foundedSuggestions.Add(auditoriumSeating.MakeSuggestionFor(partyRequested, pricingCategory));
            }

            return foundedSuggestions;
        }
    }
}