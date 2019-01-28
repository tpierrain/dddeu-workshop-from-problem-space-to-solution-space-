package com.baasie.SeatsSuggestions;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public int size() {
        return seats.size();
    }

    public List<AdjacentSeats> splitInto(int size) {
        List<AdjacentSeats> result = new ArrayList<>();
        AdjacentSeats lastGroup = new AdjacentSeats(new ArrayList<>());
        for (Seat seat : seats) {
            if (lastGroup.size() == size) {
                result.add(lastGroup);
                lastGroup = new AdjacentSeats(Stream.of(seat).collect(Collectors.toList()));
            } else {
                lastGroup.add(seat);
            }
        }

        if (lastGroup.size() == size) {
            result.add(lastGroup);
        }

        return result;
    }

    private void add(Seat seat) {
        seats.add(seat);
    }

    public int computeDistanceFromRowCentroid(int rowSize) {
        List<Integer> allSeatsDistanceFromRowCenter = seats.stream().map(seat -> seat.computeDistanceFromRowCentroid(rowSize)).collect(Collectors.toList());
        return allSeatsDistanceFromRowCenter.stream().mapToInt(Integer::intValue).sum() / seats.size();
    }


    @Override
    public String toString() {
        return seats.stream().map(Seat::toString).collect(Collectors.joining("-"));
    }

}
