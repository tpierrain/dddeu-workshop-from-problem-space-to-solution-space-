package com.baasie.SeatsSuggestions;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class SeatingOptionSuggested {

    private PricingCategory pricingCategory;
    private List<Seat> seats = new ArrayList<>();
    private int partyRequested;

    public SeatingOptionSuggested(SuggestionRequest suggestionRequest) {
        this.pricingCategory = suggestionRequest.pricingCategory();
        this.partyRequested = suggestionRequest.partyRequested();
    }

    public void addSeats(AdjacentSeats adjacentSeats) {
        seats.addAll(Lists.newArrayList(adjacentSeats));
    }

    public boolean matchExpectation() {
        return seats.size() == partyRequested;
    }

    public List<Seat> seats() {
        return seats;
    }
}
