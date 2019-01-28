package com.baasie.SeatsSuggestions;

import java.util.List;

public class Row {
    private String name;
    private List<Seat> seats;

    public Row(String name, List<Seat> seats) {
        this.name = name;
        this.seats = seats;
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public SeatingOptionSuggested suggestSeatingOption(int partyRequested, PricingCategory pricingCategory) {
        for (Seat seat : seats) {
            if (seat.isAvailable() && seat.matchCategory(pricingCategory)) {
                SeatingOptionSuggested seatingOptionSuggested = new SeatingOptionSuggested(partyRequested, pricingCategory);
                seatingOptionSuggested.addSeat(seat);

                if (seatingOptionSuggested.matchExpectation()) {
                    return seatingOptionSuggested;
                }
            }
        }

        return new SeatingOptionNotAvailable(partyRequested, pricingCategory);
    }

    public List<Seat> seats() {
        return seats;
    }

}
