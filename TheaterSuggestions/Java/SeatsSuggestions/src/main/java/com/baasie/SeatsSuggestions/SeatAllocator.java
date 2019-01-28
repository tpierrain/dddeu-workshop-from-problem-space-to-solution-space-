package com.baasie.SeatsSuggestions;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class SeatAllocator {
    private static final int NUMBER_OF_SUGGESTIONS = 3;
    private final AuditoriumSeatingAdapter auditoriumSeatingAdapter;

    public SeatAllocator(AuditoriumSeatingAdapter auditoriumLayoutAdapter) {
        this.auditoriumSeatingAdapter = auditoriumLayoutAdapter;
    }

    public SuggestionsMade makeSuggestions(String showId, int partyRequested) {

        AuditoriumSeating auditoriumSeating = auditoriumSeatingAdapter.getAuditoriumSeating(showId);

        SuggestionsMade suggestionsMade = new SuggestionsMade(showId, partyRequested);

        suggestionsMade.add(giveMeSuggestionsFor(auditoriumSeating, partyRequested,
                PricingCategory.First));
        suggestionsMade.add(giveMeSuggestionsFor(auditoriumSeating, partyRequested,
                PricingCategory.Second));
        suggestionsMade.add(giveMeSuggestionsFor(auditoriumSeating, partyRequested,
                PricingCategory.Third));
        suggestionsMade.add(giveMeSuggestionsFor(auditoriumSeating, partyRequested,
                PricingCategory.Mixed));

        if (suggestionsMade.matchExpectations()) {
            return suggestionsMade;
        }

        return new SuggestionNotAvailable(showId, partyRequested);
    }

    private static List<SuggestionMade> giveMeSuggestionsFor(
            AuditoriumSeating auditoriumSeating, int partyRequested, PricingCategory pricingCategory) {

        SuggestionRequest suggestionRequest = new SuggestionRequest(partyRequested, pricingCategory);
        List<SuggestionMade> foundedSuggestions = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_SUGGESTIONS; i++) {
            SeatingOptionSuggested seatingOptionSuggested = auditoriumSeating.suggestSeatingOptionFor(suggestionRequest);

            if (seatingOptionSuggested.matchExpectation()) {
                // We get the new version of the Auditorium after the allocation
                auditoriumSeating = auditoriumSeating.allocate(seatingOptionSuggested);
                foundedSuggestions.add(new SuggestionMade(seatingOptionSuggested.seats(), partyRequested, pricingCategory));
            }
        }

        return ImmutableList.copyOf(foundedSuggestions);
    }
}
