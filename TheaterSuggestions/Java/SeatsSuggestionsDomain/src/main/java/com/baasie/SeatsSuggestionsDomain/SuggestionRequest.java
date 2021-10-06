package com.baasie.SeatsSuggestionsDomain;

import lombok.Value;

@Value
public class SuggestionRequest {

    int partyRequested;
    PricingCategory pricingCategory;

    SuggestionRequest(int partyRequested, PricingCategory pricingCategory) {
        this.partyRequested = partyRequested;
        this.pricingCategory = pricingCategory;
    }

    @Override
    public String toString() {
        return String.format("%s-%s", partyRequested, pricingCategory.toString());
    }

    public PricingCategory pricingCategory() {
        return pricingCategory;
    }

    public int partyRequested() {
        return partyRequested;
    }
}