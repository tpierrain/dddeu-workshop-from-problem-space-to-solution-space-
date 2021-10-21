package com.baasie.SeatsSuggestionsDomain;

import com.baasie.SeatsSuggestionsDomain.port.IAdaptAuditoriumSeating;
import com.baasie.SeatsSuggestionsDomain.port.IProvideAuditoriumSeating;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class SeatAllocator implements IProvideAuditoriumSeating {
    private static final int NUMBER_OF_SUGGESTIONS = 3;
    private final IAdaptAuditoriumSeating iAdaptAuditoriumSeating;

    public SeatAllocator(IAdaptAuditoriumSeating iAdaptAuditoriumSeating) {
        this.iAdaptAuditoriumSeating = iAdaptAuditoriumSeating;
    }

    private static List<SuggestionMade> giveMeSuggestionsFor(
            AuditoriumSeating auditoriumSeating, PartyRequested partyRequested, PricingCategory pricingCategory) {

        var suggestionRequest = new SuggestionRequest(partyRequested, pricingCategory);
        List<SuggestionMade> foundedSuggestions = new ArrayList<>();
        for (var i = 0; i < NUMBER_OF_SUGGESTIONS; i++) {
            SeatingOptionSuggested seatingOptionSuggested = auditoriumSeating.suggestSeatingOptionFor(suggestionRequest);

            if (seatingOptionSuggested.matchExpectation()) {
                // We get the new version of the Auditorium after the allocation
                auditoriumSeating = auditoriumSeating.allocate(seatingOptionSuggested);
                foundedSuggestions.add(new SuggestionMade(seatingOptionSuggested.seats(), partyRequested, pricingCategory));
            }
        }

        return ImmutableList.copyOf(foundedSuggestions);
    }

    public SuggestionsMade makeSuggestions(ShowId showId, PartyRequested partyRequested) {

        var auditoriumSeating = iAdaptAuditoriumSeating.getAuditoriumSeating(showId);

        var suggestionsMade = new SuggestionsMade(showId, partyRequested);

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
}