package com.baasie.SeatsSuggestionsDomain;

import java.util.ArrayList;

public class NotSuggestionMatchedExpectation extends SuggestionMade {
    public NotSuggestionMatchedExpectation(int partyRequested, PricingCategory pricingCategory) {
        super(new ArrayList<>(), partyRequested, pricingCategory);
    }
}
