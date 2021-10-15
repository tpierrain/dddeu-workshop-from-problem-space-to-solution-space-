package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.Row;
import com.baasie.SeatsSuggestionsDomain.Seat;
import com.baasie.SeatsSuggestionsDomain.SuggestionRequest;

import java.util.*;
import java.util.stream.Collectors;

public class OfferingSeatsNearerMiddleOfTheRow {
    private final Row row;

    public OfferingSeatsNearerMiddleOfTheRow(Row row) {
        this.row = row;
    }

    public List<SeatWithTheDistanceFromTheMiddleOfTheRow> offerSeatsNearerTheMiddleOfTheRow(SuggestionRequest suggestionRequest) {

        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows =
                computeDistancesNearerTheMiddleOfTheRow();

        return seatWithTheDistanceFromTheMiddleOfTheRows
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

        return seatsInTheMiddleOfTheRow(row.seats(), theMiddleOfRow())
                .stream()
                .map(s -> new SeatWithTheDistanceFromTheMiddleOfTheRow(s, 0))
                .collect(Collectors.toList());
    }

    private List<Seat> seatsInTheMiddleOfTheRow(List<Seat> seats, int middle) {

        return seats.size() % 2 == 0
                ? new ArrayList<>(Arrays.asList(seats.get(middle - 1), seats.get(middle)))
                : new ArrayList<>(Collections.singletonList(seats.get(middle - 1)));
    }

    private int theMiddleOfRow() {

        return rowSizeIsEven() ? row.seats().size() / 2 : Math.abs(row.seats().size() / 2) + 1;
    }

    private boolean rowSizeIsEven() {

        return row.seats().size() % 2 == 0;
    }

    private boolean isMiddle(Seat seat) {

        int theMiddleOfRow = theMiddleOfRow();

        if (rowSizeIsEven()) {
            if (Math.abs(seat.number() - theMiddleOfRow) == 0) {
                return true;
            }
            if (seat.number() - (theMiddleOfRow + 1) == 0) {
                return true;
            }
        }
        return Math.abs(seat.number() - theMiddleOfRow) == 0;
    }

    private List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> splitSeatsByDistanceNearerTheMiddleOfTheRow() {

        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithDistances = new ArrayList<>();
        List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupsSeatsWithDistance = new ArrayList<>();

        for (Seat seat : row.seats()) {
            if (!isMiddle(seat)) {
                seatWithDistances.add(new SeatWithTheDistanceFromTheMiddleOfTheRow(seat, distance(theMiddleOfRow(), seat)));
            } else {
                if (!seatWithDistances.isEmpty())
                    groupsSeatsWithDistance.add(seatWithDistances);
                seatWithDistances = new ArrayList<>();
            }
        }
        if (!seatWithDistances.isEmpty())
            groupsSeatsWithDistance.add(seatWithDistances);

        return groupsSeatsWithDistance;
    }

    private int distance(int middle, Seat seat) {
        int distance;
        if (rowSizeIsEven())
            distance = seat.number() - middle > 0
                    ? Math.abs(seat.number() - middle)
                    : Math.abs(seat.number() - (middle + 1));
        else
            distance = Math.abs(seat.number() - middle);
        return distance;
    }
}
