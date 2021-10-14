package com.baasie.SeatsSuggestions;

public class SeatWithTheDistanceFromTheMiddleOfTheRow {
    Seat seat;
    int distanceFromTheMiddleOfTheRow;

    public SeatWithTheDistanceFromTheMiddleOfTheRow(Seat seat, int distanceFromTheMiddleOfTheRow) {
        this.seat = seat;
        this.distanceFromTheMiddleOfTheRow = distanceFromTheMiddleOfTheRow;
    }

    public Seat getSeat() {
        return this.seat;
    }

    public int getDistanceFromTheMiddleOfTheRow() {
        return this.distanceFromTheMiddleOfTheRow;
    }

    @Override
    public String toString() {
        return this.seat.rowName() + this.seat.number() + " " + this.distanceFromTheMiddleOfTheRow;
    }
}
