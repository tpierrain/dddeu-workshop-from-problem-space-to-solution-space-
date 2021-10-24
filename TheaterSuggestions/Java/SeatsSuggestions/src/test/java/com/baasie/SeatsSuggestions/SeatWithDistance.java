package com.baasie.SeatsSuggestions;
import lombok.EqualsAndHashCode;
import com.baasie.SeatsSuggestions.Seat;

@EqualsAndHashCode
public class SeatWithDistance {
    private final Seat seat;
    private final int distanceFromTheMiddleOfTheRow;

    public SeatWithDistance(Seat seat, int distanceFromTheMiddleOfTheRow) {
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
