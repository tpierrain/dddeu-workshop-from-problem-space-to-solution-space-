package com.baasie.SeatsSuggestions;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class Row {
    private String name;
    private List<Seat> seats;

    public Row(String name, List<Seat> seats) {
        this.name = name;
        this.seats = seats;
    }

    public List<Seat> seats() {
        return seats;
    }

    public Row addSeat(Seat seat) {
        ArrayList<Seat> newSeats = new ArrayList<>(this.seats);
        newSeats.add(seat);
        return new Row(name, newSeats);
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

    public Row allocate(Seat seat) {
        List<Seat> newVersionOfSeats = new ArrayList<>();

        seats.forEach(currentSeat -> {
            if (currentSeat.sameSeatLocation(seat)) {
                newVersionOfSeats.add(new Seat(seat.rowName(), seat.number(), seat.pricingCategory(),
                        SeatAvailability.Allocated));
            } else {
                newVersionOfSeats.add(currentSeat);
            }
        });

        return new Row(seat.rowName(), newVersionOfSeats);
    }
}
