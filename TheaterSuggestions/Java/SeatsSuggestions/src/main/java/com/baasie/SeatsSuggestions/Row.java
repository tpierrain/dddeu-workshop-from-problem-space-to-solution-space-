package com.baasie.SeatsSuggestions;
import  com.baasie.SeatsSuggestions.DeepModel.*;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class Row {
    private final String name;
    private final List<Seat> seats;

    public Row(String name, List<Seat> seats) {
        this.name = name;
        this.seats = seats
                .stream()
                .map(s -> new Seat(
                        s.rowName(),
                        s.number(),
                        s.pricingCategory(),
                        s.seatAvailability()))
                        .collect(Collectors.toList());
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

        for (Seat seat : offerAdjacentSeatsNearerTheMiddleOfRow(suggestionRequest)) {
            seatingOptionSuggested.addSeat(seat);

            if (seatingOptionSuggested.matchExpectation()) {
                return seatingOptionSuggested;
            }
        }

        return new SeatingOptionNotAvailable(suggestionRequest);
    }
    public List<Seat> offerAdjacentSeatsNearerTheMiddleOfRow(SuggestionRequest suggestionRequest)
    {
        // 1. offer seats from the middle of the row
        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows = new OfferingSeatsNearerMiddleOfTheRow(this).offerSeatsNearerTheMiddleOfTheRow(suggestionRequest);

        return seatWithTheDistanceFromTheMiddleOfTheRows.stream().map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat).collect(Collectors.toList());
    }

    private boolean doNotLookForAdjacentSeatsWhenThePartyContainsOnlyOnePerson(SuggestionRequest suggestionRequest) {
        return suggestionRequest.partyRequested() == 1;
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

    public boolean rowSizeIsEven() {

        return seats().size() % 2 == 0;
    }

    public int theMiddleOfRow() {

        return rowSizeIsEven() ? seats().size() / 2 : Math.abs(seats().size() / 2) + 1;
    }

    public boolean isTheMiddleOfRow(Seat seat) {

        int theMiddleOfRow = theMiddleOfRow();

        if (rowSizeIsEven()) {
            if (Math.abs(seat.number() - theMiddleOfRow) == 0 || seat.number() - (theMiddleOfRow + 1) == 0) {
                return true;
            }
        }
        return Math.abs(seat.number() - theMiddleOfRow) == 0;
    }

    public int distanceFromTheMiddleOfRow(Seat seat) {

        if (rowSizeIsEven())
            return seat.number() - theMiddleOfRow() > 0
                    ? Math.abs(seat.number() - theMiddleOfRow())
                    : Math.abs(seat.number() - (theMiddleOfRow() + 1));

        return Math.abs(seat.number() - theMiddleOfRow());
    }
}