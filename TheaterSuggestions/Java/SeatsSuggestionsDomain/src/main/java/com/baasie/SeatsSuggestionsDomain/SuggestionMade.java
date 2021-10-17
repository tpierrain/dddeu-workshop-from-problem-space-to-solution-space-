package com.baasie.SeatsSuggestionsDomain;

import com.google.common.collect.ImmutableList;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SuggestionMade {

    private final ImmutableList<Seat> suggestedSeats;
    private final PartyRequested partyRequested;
    private final PricingCategory pricingCategory;

    public SuggestionMade(List<Seat> suggestedSeats, PartyRequested partyRequested, PricingCategory pricingCategory) {
        this.suggestedSeats = ImmutableList.copyOf(suggestedSeats);
        this.partyRequested = partyRequested;
        this.pricingCategory = pricingCategory;
    }

    public List<String> seatNames() {
        return suggestedSeats.stream().sorted(Comparator.comparing(Seat::number)).map(Seat::toString).collect(Collectors.toList());
    }

    public boolean MatchExpectation() {
        return suggestedSeats.size() == partyRequested.partySize();
    }

    public PricingCategory pricingCategory() {
        return pricingCategory;
    }
}