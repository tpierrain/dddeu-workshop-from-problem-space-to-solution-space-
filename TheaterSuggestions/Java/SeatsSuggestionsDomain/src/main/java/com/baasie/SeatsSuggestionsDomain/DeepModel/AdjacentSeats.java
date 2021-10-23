package com.baasie.SeatsSuggestionsDomain.DeepModel;

import java.util.ArrayList;
import java.util.List;

public class AdjacentSeats {
    public ArrayList<SeatWithDistance> seatsWithDistance = new ArrayList<>();

    public AdjacentSeats() {
    }

    public AdjacentSeats(List<SeatWithDistance> seats)
    {
        seatsWithDistance.addAll(seats);
    }

    public void addSeat(SeatWithDistance seatWithTheDistance)
    {
        seatsWithDistance.add(seatWithTheDistance);
    }
}
