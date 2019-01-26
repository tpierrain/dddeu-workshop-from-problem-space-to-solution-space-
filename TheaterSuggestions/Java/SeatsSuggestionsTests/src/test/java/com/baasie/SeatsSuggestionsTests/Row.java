package com.baasie.SeatsSuggestionsTests;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private String name;
    private List<Seat> seats = new ArrayList<>();

    public List<Seat> seats() {
        return seats;
    }
}
