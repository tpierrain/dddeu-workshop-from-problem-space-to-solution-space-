package com.baasie.SeatsSuggestionsDomain.DeepModel;

import java.util.ArrayList;
import java.util.List;

public class AdjacentSeats {
    public ArrayList<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistance = new ArrayList<>();

    public AdjacentSeats() {
    }

    public AdjacentSeats(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seats)
    {
        seatsWithDistance.addAll(seats);
    }

    public void addSeat(SeatWithTheDistanceFromTheMiddleOfTheRow seatWithTheDistance)
    {
        seatsWithDistance.add(seatWithTheDistance);
    }
}
