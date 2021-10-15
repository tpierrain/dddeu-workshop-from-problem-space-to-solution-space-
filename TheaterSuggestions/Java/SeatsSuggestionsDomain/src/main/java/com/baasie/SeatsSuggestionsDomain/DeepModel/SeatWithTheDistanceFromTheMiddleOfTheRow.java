package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.Seat;

public class SeatWithTheDistanceFromTheMiddleOfTheRow {
    private final Seat seat;
    private final int distanceFromTheMiddleOfTheRow;

    public SeatWithTheDistanceFromTheMiddleOfTheRow(Seat seat, int distanceFromTheMiddleOfTheRow) {
        this.seat = seat;
        this.distanceFromTheMiddleOfTheRow = distanceFromTheMiddleOfTheRow;
    }

    public Seat seat() {
        return this.seat;
    }

    public int distanceFromTheMiddleOfTheRow() {
        return distanceFromTheMiddleOfTheRow;
    }
}
