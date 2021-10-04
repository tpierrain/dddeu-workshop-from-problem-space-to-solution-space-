package com.baasie.SeatsSuggestions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SeatCollectionExtensions {
    private SeatCollectionExtensions() {
    }

    public static List<Seat> selectAvailableSeatsCompliant(List<Seat> seats, PricingCategory pricingCategory) {
        return seats.stream().filter(seat -> seat.isAvailable() && seat.matchCategory(pricingCategory)).collect(Collectors.toList());
    }

    public static List<AdjacentSeats> selectAdjacentSeats(List<Seat> seats, int partySize) {
        List<AdjacentSeats> adjacentSeatsGroups = new ArrayList<>();
        List<Seat> adjacentSeats = new ArrayList<>();
        List<Seat> currentSeats = new ArrayList<>(seats);

        if (partySize == 1) {
            return currentSeats.stream().map(seat -> new AdjacentSeats(Stream.of(seat).collect(Collectors.toCollection(ArrayList::new)))).collect(Collectors.toList());
        }

        for (Seat candidateSeat : currentSeats) {
            if (adjacentSeats.isEmpty()) {
                adjacentSeats.add(candidateSeat);
                continue;
            }

            adjacentSeats = adjacentSeats.stream()
                    .sorted(Comparator.comparing(Seat::number))
                    .collect(Collectors.toCollection(ArrayList::new));

            if (candidateSeat.isAdjacentWith(adjacentSeats)) {
                adjacentSeats.add(candidateSeat);

                if (noMoreSeats(adjacentSeats, currentSeats)) {
                    adjacentSeatsGroups.add(reduceAdjacentSeats(partySize, adjacentSeats));
                }

            } else {
                if (!adjacentSeats.isEmpty()) {
                    adjacentSeatsGroups.add(reduceAdjacentSeats(partySize, adjacentSeats));
                }

                adjacentSeats = new ArrayList<>();
                adjacentSeats.add(candidateSeat);

            }
        }
        return adjacentSeatsGroups.stream().filter(a -> a.size() >= partySize).collect(Collectors.toList());
    }

    private static AdjacentSeats reduceAdjacentSeats(int partySize, List<Seat> adjacentSeats) {

        List<Seat> orderedAdjacentSeats = adjacentSeats.stream()
                .sorted(Comparator.comparing(Seat::distanceFromCentroid))
                .collect(Collectors.toCollection(ArrayList::new));

        List<Seat> sortedSeats = orderedAdjacentSeats.stream()
                .limit(partySize)
                .sorted(Comparator.comparing(Seat::number))
                .collect(Collectors.toCollection(ArrayList::new));
        return new AdjacentSeats(sortedSeats);
    }

    public static List<AdjacentSeats> orderByMiddleOfTheRow(List<AdjacentSeats> adjacentSeats,
                                                            int rowSize) {
        SortedMap<Integer, List<AdjacentSeats>> sortedAdjacentSeatsGroups = new TreeMap<>();

        for (AdjacentSeats adjacentSeat : adjacentSeats) {
            int distanceFromRowCentroid = adjacentSeat.computeDistanceFromRowCentroid();

            if (!sortedAdjacentSeatsGroups.containsKey(distanceFromRowCentroid)) {
                sortedAdjacentSeatsGroups.put(distanceFromRowCentroid, new ArrayList<>());
            }

            sortedAdjacentSeatsGroups.get(distanceFromRowCentroid).add(adjacentSeat);
        }

        return sortedAdjacentSeatsGroups.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static int centroidIndex(int rowSize) {
        return Math.abs(rowSize / 2);
    }

    private static boolean noMoreSeats(Collection adjacentSeats, Collection currentSeats)
    {
        return adjacentSeats.size() == currentSeats.size();
    }

    public static int computeDistanceFromCentroid(int seatLocation, int rowSize) {
        return Math.abs(seatLocation - centroidIndex(rowSize));
    }

    public static boolean isCentroid(int seatLocation, int rowSize) {
        int centroidIndex = centroidIndex(rowSize);

        return seatLocation == centroidIndex || seatLocation == centroidIndex + 1;
    }

    public static boolean isOdd(int rowSize) {
        return rowSize % 2 != 0;
    }
}