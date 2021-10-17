package com.baasie.SeatsSuggestionsDomain;

import java.util.ArrayList;

public class NotSuggestionMatchedExpectation extends SuggestionMade {
    public NotSuggestionMatchedExpectation(PartyRequested partyRequested, PricingCategory pricingCategory) {
        super(new ArrayList<>(), partyRequested, pricingCategory);
    }
}
