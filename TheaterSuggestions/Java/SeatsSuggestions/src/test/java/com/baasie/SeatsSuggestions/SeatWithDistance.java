package com.baasie.SeatsSuggestions;

import com.baasie.SeatsSuggestions.Seat;

/// <summary>
///     Our model uses a seat with a property DistanceFromTheMiddle
///     to manage these business rules:
///     * Offer seats nearer middle of the row.
///     * Offer adjacent seats to member of the same party.
/// </summary>
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
