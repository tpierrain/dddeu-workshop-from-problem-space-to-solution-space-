package com.baasie.SeatsSuggestionsDomain;

import lombok.Value;

@Value
public class SuggestionRequest {

    PartyRequested partyRequested;
    PricingCategory pricingCategory;

    public SuggestionRequest(PartyRequested partyRequested, PricingCategory pricingCategory) {
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

    public PartyRequested partyRequested() {
        return partyRequested;
    }
}