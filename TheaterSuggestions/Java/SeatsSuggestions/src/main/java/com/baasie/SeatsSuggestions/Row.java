package com.baasie.SeatsSuggestions;

import com.baasie.SeatsSuggestions.DeepModel.OfferingAdjacentSeatsToMembersOfTheSameParty;
import com.baasie.SeatsSuggestions.DeepModel.OfferingSeatsNearerMiddleOfTheRow;
import com.baasie.SeatsSuggestions.DeepModel.SeatWithTheDistanceFromTheMiddleOfTheRow;
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

        for (Seat seat : offerAdjacentSeatsNearerTheMiddleOfRow(suggestionRequest)) {
            seatingOptionSuggested.addSeat(seat);

            if (seatingOptionSuggested.matchExpectation())
            {
                return seatingOptionSuggested;
            }
        }

        return new SeatingOptionNotAvailable(suggestionRequest);
    }

    public List<Seat> offerAdjacentSeatsNearerTheMiddleOfRow(SuggestionRequest suggestionRequest)
    {
        // 1. offer seats from the middle of the row
        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows = new OfferingSeatsNearerMiddleOfTheRow(this).offerSeatsNearerTheMiddleOfTheRow(suggestionRequest);

        if (doNotLookForAdjacentSeatsWhenThePartyContainsOnlyOnePerson(suggestionRequest)) {
            return seatWithTheDistanceFromTheMiddleOfTheRows.stream().map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat).collect(Collectors.toList());
        }
        // 2. based on seats with distance from the middle of row,
        //    we offer the best group (close to the middle) of adjacent seats
        List<Seat> seats = new OfferingAdjacentSeatsToMembersOfTheSameParty(suggestionRequest).OfferAdjacentSeats(
                seatWithTheDistanceFromTheMiddleOfTheRows);
        return seats;
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
}
