package com.baasie.SeatsSuggestionsTests;

import java.util.ArrayList;
import java.util.List;

public class Suggestion {
    private int partyRequested;

    private List<Seat> seats = new ArrayList<>();

    public Suggestion(int partyRequested) {
        this.partyRequested = partyRequested;
    }

    public boolean isFulFilled() {
        return this.seats.size() == partyRequested;
    }

    public void addSeat(Seat seat) {
        seat.updateCategory(SeatAvailability.Suggested);
        this.seats.add(seat);
    }

    public int partyRequested() {
        return partyRequested;
    }

    public List<Seat> seats() {
        return seats;
    }
}
