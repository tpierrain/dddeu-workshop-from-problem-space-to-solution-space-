package com.baasie.SeatsSuggestionsTests;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class SeatAllocator {

    private final AuditoriumSeatingAdapter auditoriumSeatingAdapter;

    public SeatAllocator(AuditoriumSeatingAdapter auditoriumLayoutAdapter) {
        this.auditoriumSeatingAdapter = auditoriumLayoutAdapter;
    }

    public SuggestionsMade makeSuggestion(String showId, int partyRequested) {

        AuditoriumSeating auditoriumSeating = auditoriumSeatingAdapter.getAuditoriumSeating(showId);

        SuggestionsMade suggestionsMade = new SuggestionsMade(showId, partyRequested);

        int numberOfSuggestions = 3;

        suggestionsMade.add(giveMeSeveralSuggestionFor(partyRequested, auditoriumSeating, numberOfSuggestions,
                PricingCategory.First));
        suggestionsMade.add(giveMeSeveralSuggestionFor(partyRequested, auditoriumSeating, numberOfSuggestions,
                PricingCategory.Second));
        suggestionsMade.add(giveMeSeveralSuggestionFor(partyRequested, auditoriumSeating, numberOfSuggestions,
                PricingCategory.Third));
        suggestionsMade.add(giveMeSeveralSuggestionFor(partyRequested, auditoriumSeating, numberOfSuggestions,
                PricingCategory.Mixed));

        if (!suggestionsMade.matchExpectations()) {
            return new SuggestionNotAvailable(showId, partyRequested);
        }

        return suggestionsMade;
    }

    private static ImmutableList<SuggestionMade> giveMeSeveralSuggestionFor(int partyRequested,
                                                                            AuditoriumSeating auditoriumSeating, int numberOfSuggestions, PricingCategory pricingCategory) {
        List<SuggestionMade> foundedSuggestions = new ArrayList<>();
        for (int i = 0; i < numberOfSuggestions; i++) {
            foundedSuggestions.add(auditoriumSeating.makeSuggestionFor(partyRequested, pricingCategory));
        }

        return ImmutableList.copyOf(foundedSuggestions);
    }
}
