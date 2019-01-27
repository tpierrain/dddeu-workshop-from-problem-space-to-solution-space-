package com.baasie.SeatsSuggestionsAcceptanceTests;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class SuggestionMade {

    private ImmutableList<Seat> suggestedSeats;
    private int partyRequested;

    public SuggestionMade(List<Seat> suggestedSeats, int partyRequested) {
        this.suggestedSeats = ImmutableList.copyOf(suggestedSeats);
        this.partyRequested = partyRequested;
    }

    public ImmutableList<Seat> suggestedSeats() {
        return suggestedSeats;
    }
}
