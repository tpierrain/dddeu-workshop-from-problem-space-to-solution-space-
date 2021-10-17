package com.baasie.SeatsSuggestionsDomain;

import java.util.ArrayList;
import java.util.List;

public class SeatingOptionSuggested {

    private final PricingCategory pricingCategory;
    private final List<Seat> seats = new ArrayList<>();
    private final PartyRequested partyRequested;

    public SeatingOptionSuggested(SuggestionRequest suggestionRequest) {
        this.pricingCategory = suggestionRequest.pricingCategory();
        this.partyRequested = suggestionRequest.partyRequested();
    }

    PartyRequested partyRequested() { return partyRequested;  }

    PricingCategory pricingCategory() { return pricingCategory; }

    public boolean matchExpectation() {
        return seats.size() == partyRequested.partySize();
    }

    public List<Seat> seats() {
        return seats;
    }

    public void addSeat(Seat seat) {
        this.seats.add(seat);
    }
}
