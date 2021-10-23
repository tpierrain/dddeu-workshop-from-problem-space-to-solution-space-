package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.Row;
import com.baasie.SeatsSuggestionsDomain.Seat;
import com.baasie.SeatsSuggestionsDomain.SuggestionRequest;

import lombok.EqualsAndHashCode;
import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class OfferingSeatsNearerMiddleOfTheRow {
    private final Row row;

    public OfferingSeatsNearerMiddleOfTheRow(Row row) {
        this.row = row;
    }

    public List<SeatWithDistance> offerSeatsNearerTheMiddleOfTheRow(SuggestionRequest suggestionRequest) {

        return computeDistancesNearerTheMiddleOfTheRow()
                .stream()
                .filter(seatWithTheDistanceFromTheMiddleOfTheRow ->
                        seatWithTheDistanceFromTheMiddleOfTheRow.seat().matchCategory(suggestionRequest.pricingCategory()))
                .filter(seatWithTheDistanceFromTheMiddleOfTheRow ->
                        seatWithTheDistanceFromTheMiddleOfTheRow.seat().isAvailable())
                .collect(Collectors.toList());
    }


    private List<SeatWithDistance> computeDistancesNearerTheMiddleOfTheRow() {

        List<List<SeatWithDistance>> seatWithTheDistanceFromTheMiddleOfTheRows = splitSeatsByDistanceNearerTheMiddleOfTheRow();

        List<SeatWithDistance> seatsInTheMiddleOfTheRow = seatsInTheMiddleOfTheRow();

        seatWithTheDistanceFromTheMiddleOfTheRows.add(seatsInTheMiddleOfTheRow);

        return seatWithTheDistanceFromTheMiddleOfTheRows.stream()
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(SeatWithDistance::distanceFromTheMiddleOfTheRow))
                .collect(Collectors.toList());
    }

    private List<SeatWithDistance> seatsInTheMiddleOfTheRow() {

        return seatsInTheMiddleOfTheRow(row.seats(), row.theMiddleOfRow())
                .stream()
                .map(s -> new SeatWithDistance(s, 0))
                .collect(Collectors.toList());
    }

    private List<Seat> seatsInTheMiddleOfTheRow(List<Seat> seats, int middle) {

        return seats.size() % 2 == 0
                ? new ArrayList<>(Arrays.asList(seats.get(middle - 1), seats.get(middle)))
                : new ArrayList<>(Collections.singletonList(seats.get(middle - 1)));
    }

    private List<List<SeatWithDistance>> splitSeatsByDistanceNearerTheMiddleOfTheRow() {

        List<SeatWithDistance> seatsWithDistance = new ArrayList<>();
        List<List<SeatWithDistance>> groupsOfSeatsWithDistance = new ArrayList<>();

        for (Seat seat : row.seats()) {
            if (!row.isTheMiddleOfRow(seat)) {
                seatsWithDistance
                        .add(new SeatWithDistance(seat, row.distanceFromTheMiddleOfRow(seat)));
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
