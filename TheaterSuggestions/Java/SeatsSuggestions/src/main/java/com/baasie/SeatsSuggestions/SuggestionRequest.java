package com.baasie.SeatsSuggestions;

import lombok.Value;

@Value
public class SuggestionRequest {

    int partyRequested;
    PricingCategory pricingCategory;

    @Override
    public String toString() {
        return String.format("%s-%s", partyRequested, pricingCategory.toString());
    }
}
