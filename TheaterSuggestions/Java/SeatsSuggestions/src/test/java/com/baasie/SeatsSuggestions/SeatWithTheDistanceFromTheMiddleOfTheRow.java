package com.baasie.SeatsSuggestions;

public class SeatWithTheDistanceFromTheMiddleOfTheRow {
    private Seat seat;
    private int distanceFromTheMiddleOfTheRow;

    public SeatWithTheDistanceFromTheMiddleOfTheRow(Seat seat, int distanceFromTheMiddleOfTheRow) {
        this.seat = seat;
        this.distanceFromTheMiddleOfTheRow = distanceFromTheMiddleOfTheRow;
    }

    public Seat seat()
    {
        return this.seat;
    }

    public Integer distanceFromTheMiddleOfTheRow()
    {
        return distanceFromTheMiddleOfTheRow;
    }
}


