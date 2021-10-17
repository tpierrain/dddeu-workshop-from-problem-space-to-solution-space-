package com.baasie.SeatsSuggestions.DeepModel;

import com.baasie.SeatsSuggestions.Row;
import com.baasie.SeatsSuggestions.Seat;
import com.baasie.SeatsSuggestions.SuggestionRequest;

import lombok.EqualsAndHashCode;
import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class OfferingSeatsNearerMiddleOfTheRow {
    private final Row row;

    public OfferingSeatsNearerMiddleOfTheRow(Row row) {
        this.row = row;
    }

    public List<SeatWithTheDistanceFromTheMiddleOfTheRow> offerSeatsNearerTheMiddleOfTheRow(SuggestionRequest suggestionRequest) {

        return computeDistancesNearerTheMiddleOfTheRow()
                .stream()
                .filter(seatWithTheDistanceFromTheMiddleOfTheRow ->
                        seatWithTheDistanceFromTheMiddleOfTheRow.seat().matchCategory(suggestionRequest.pricingCategory()))
                .filter(seatWithTheDistanceFromTheMiddleOfTheRow ->
                        seatWithTheDistanceFromTheMiddleOfTheRow.seat().isAvailable())
                .collect(Collectors.toList());
    }


    private List<SeatWithTheDistanceFromTheMiddleOfTheRow> computeDistancesNearerTheMiddleOfTheRow() {

        List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> seatWithTheDistanceFromTheMiddleOfTheRows = splitSeatsByDistanceNearerTheMiddleOfTheRow();

        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsInTheMiddleOfTheRow = seatsInTheMiddleOfTheRow();

        seatWithTheDistanceFromTheMiddleOfTheRows.add(seatsInTheMiddleOfTheRow);

        return seatWithTheDistanceFromTheMiddleOfTheRows.stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow))
                .collect(Collectors.toList());
    }

    private List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsInTheMiddleOfTheRow() {

        return seatsInTheMiddleOfTheRow(row.seats(), row.theMiddleOfRow())
                .stream()
                .map(s -> new SeatWithTheDistanceFromTheMiddleOfTheRow(s, 0))
                .collect(Collectors.toList());
    }

    private List<Seat> seatsInTheMiddleOfTheRow(List<Seat> seats, int middle) {

        return seats.size() % 2 == 0
                ? new ArrayList<>(Arrays.asList(seats.get(middle - 1), seats.get(middle)))
                : new ArrayList<>(Collections.singletonList(seats.get(middle - 1)));
    }

    private List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> splitSeatsByDistanceNearerTheMiddleOfTheRow() {

        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistance = new ArrayList<>();
        List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupsOfSeatsWithDistance = new ArrayList<>();

        for (Seat seat : row.seats()) {
            if (!row.isTheMiddleOfRow(seat)) {
                seatsWithDistance
                        .add(new SeatWithTheDistanceFromTheMiddleOfTheRow(seat, row.distanceFromTheMiddleOfRow(seat)));
            } else {
                if (!seatsWithDistance.isEmpty())
                    groupsOfSeatsWithDistance.add(seatsWithDistance);
                seatsWithDistance = new ArrayList<>();
            }
        }
        if (!seatsWithDistance.isEmpty())
            groupsOfSeatsWithDistance.add(seatsWithDistance);

        return groupsOfSeatsWithDistance;
    }

}
