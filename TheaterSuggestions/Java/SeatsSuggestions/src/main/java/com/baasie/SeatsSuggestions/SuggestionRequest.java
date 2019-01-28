package com.baasie.SeatsSuggestions;

import lombok.Value;

@Value
public class SuggestionRequest {

    int partyRequested;
    PricingCategory pricingCategory;
}
