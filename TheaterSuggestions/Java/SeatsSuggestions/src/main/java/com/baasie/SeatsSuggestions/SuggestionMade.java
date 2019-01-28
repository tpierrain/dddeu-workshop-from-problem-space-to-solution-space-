package com.baasie.SeatsSuggestions;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

public class SuggestionMade {

    private ImmutableList<Seat> suggestedSeats;
    private int partyRequested;
    private PricingCategory pricingCategory;

    public SuggestionMade(List<Seat> suggestedSeats, int partyRequested, PricingCategory pricingCategory) {
        this.suggestedSeats = ImmutableList.copyOf(suggestedSeats);
        this.partyRequested = partyRequested;
        this.pricingCategory = pricingCategory;
    }

    public List<String> seatNames() {
        return suggestedSeats.stream().map(Seat::toString).collect(Collectors.toList());
    }

    public boolean MatchExpectation() {
        return suggestedSeats.size() == partyRequested;
    }

    public PricingCategory pricingCategory() {
        return pricingCategory;
    }
}
