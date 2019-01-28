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
        List<Seat> updatedList = seats.stream().map(s -> s.equals(seat) ? seat : s).collect(Collectors.toList());

        return new Row(name, updatedList);
    }

    public SeatingOptionSuggested suggestSeatingOption(SuggestionRequest suggestionRequest) {

        SeatingOptionSuggested seatingOptionSuggested = new SeatingOptionSuggested(suggestionRequest);

        for (Seat seat : selectAvailableSeatsCompliantWith(suggestionRequest.pricingCategory())) {
            seatingOptionSuggested.addSeat(seat);

            if (seatingOptionSuggested.matchExpectation())
            {
                return seatingOptionSuggested;
            }
        }

        return new SeatingOptionNotAvailable(suggestionRequest);
    }

    private Iterable<Seat> selectAvailableSeatsCompliantWith(PricingCategory pricingCategory) {
        return seats.stream().filter(seat -> seat.isAvailable() && seat.matchCategory(pricingCategory)).collect(Collectors.toList());
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
