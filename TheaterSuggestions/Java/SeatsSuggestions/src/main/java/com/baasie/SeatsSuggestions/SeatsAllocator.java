package com.baasie.SeatsSuggestions;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class SeatsAllocator {
    private static final int NUMBER_OF_SUGGESTIONS = 3;
    private final AuditoriumSeatingAdapter auditoriumSeatingAdapter;

    public SeatsAllocator(AuditoriumSeatingAdapter auditoriumLayoutAdapter) {
        this.auditoriumSeatingAdapter = auditoriumLayoutAdapter;
    }

    public SuggestionsMade makeSuggestion(String showId, int partyRequested) {

        AuditoriumSeating auditoriumSeating = auditoriumSeatingAdapter.getAuditoriumSeating(showId);

        SuggestionsMade suggestionsMade = new SuggestionsMade(showId, partyRequested);
        
        suggestionsMade.add(giveMeSuggestionsFor(auditoriumSeating, partyRequested,
                PricingCategory.First));
        suggestionsMade.add(giveMeSuggestionsFor(auditoriumSeating,partyRequested,
                PricingCategory.Second));
        suggestionsMade.add(giveMeSuggestionsFor(auditoriumSeating,partyRequested,
                PricingCategory.Third));
        suggestionsMade.add(giveMeSuggestionsFor(auditoriumSeating,partyRequested,
                PricingCategory.Mixed));

        if (!suggestionsMade.matchExpectations()) {
            return new SuggestionNotAvailable(showId, partyRequested);
        }

        return suggestionsMade;
    }

    private static ImmutableList<SuggestionMade> giveMeSuggestionsFor(
            AuditoriumSeating auditoriumSeating, int partyRequested,  PricingCategory pricingCategory) {

        List<SuggestionMade> foundedSuggestions = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_SUGGESTIONS; i++) {
            SeatingOptionSuggested seatingOptionSuggested = auditoriumSeating.suggestSeatingOptionFor(partyRequested, pricingCategory);

            if (seatingOptionSuggested.matchExpectation()) {
                seatingOptionSuggested.seats().forEach(Seat::allocate);
                foundedSuggestions.add(new SuggestionMade(seatingOptionSuggested.seats(), partyRequested, pricingCategory));
            }
        }

        return ImmutableList.copyOf(foundedSuggestions);
    }
}
