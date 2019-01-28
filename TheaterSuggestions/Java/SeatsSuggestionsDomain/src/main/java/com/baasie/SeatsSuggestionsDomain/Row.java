package com.baasie.SeatsSuggestionsDomain;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.baasie.SeatsSuggestionsDomain.SeatCollectionExtensions.*;

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

        List<Seat> availableSeatsCompliant = selectAvailableSeatsCompliant(seats, suggestionRequest.pricingCategory());

        List<AdjacentSeats> adjacentSeatsOfExpectedSize =
                selectAdjacentSeats(availableSeatsCompliant, suggestionRequest.partyRequested());

        List<AdjacentSeats> adjacentSeatsOrdered = orderByMiddleOfTheRow(adjacentSeatsOfExpectedSize, seats.size());

        for (AdjacentSeats adjacentSeats : adjacentSeatsOrdered) {
            seatingOptionSuggested.addSeats(adjacentSeats);

            if (seatingOptionSuggested.matchExpectation())
            {
                return seatingOptionSuggested;
            }
        }

        return new SeatingOptionNotAvailable(suggestionRequest);
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
