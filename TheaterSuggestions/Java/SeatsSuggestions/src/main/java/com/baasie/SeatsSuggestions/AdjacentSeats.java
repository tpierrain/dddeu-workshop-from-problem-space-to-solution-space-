package com.baasie.SeatsSuggestions;

import lombok.EqualsAndHashCode;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class AdjacentSeats implements Iterable<Seat> {

    private List<Seat> seats;

    public AdjacentSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public Iterator<Seat> iterator() {
        return seats.iterator();
    }

    int size() {
        return seats.size();
    }


    public int computeDistanceFromRowCentroid() {
        List<Integer> allSeatsDistanceFromRowCenter = seats.stream().map(Seat::distanceFromCentroid).collect(Collectors.toList());
        return allSeatsDistanceFromRowCenter.stream().mapToInt(Integer::intValue).sum() / seats.size();
    }


    @Override
    public String toString() {
        return seats.stream().map(Seat::toString).collect(Collectors.joining("-"));
    }

}
