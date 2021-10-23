package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.Seat;
import com.baasie.SeatsSuggestionsDomain.SuggestionRequest;

import java.util.*;
import java.util.stream.Collectors;

public class OfferingAdjacentSeatsToMembersOfTheSameParty {
    private static final AdjacentSeats noSeatFound = new AdjacentSeats();

    private static AdjacentSeats
    selectTheBestGroup(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {

        return hasOnlyOneBestGroup(bestGroups) ?
                projectToSeats(bestGroups) :
                decideWhichGroupIsTheBestWhenDistancesAreEqual(bestGroups);
    }

    private static AdjacentSeats
    decideWhichGroupIsTheBestWhenDistancesAreEqual(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {

        TreeMap<Integer,AdjacentSeats> decideBetweenIdenticalScores = new TreeMap<>();

        for (var adjacentSeatsGroups : bestGroups.values())
            SelectTheBestScoreBetweenGroups(decideBetweenIdenticalScores, adjacentSeatsGroups);

        return decideBetweenIdenticalScores.lastEntry().getValue();
    }

    private static void
    SelectTheBestScoreBetweenGroups(TreeMap<Integer, AdjacentSeats> decideBetweenIdenticalScores,
                                    AdjacentSeatsGroups adjacentSeatsGroups) {

        for (var adjacentSeats : adjacentSeatsGroups.groups) {
            if (!decideBetweenIdenticalScores.containsKey(adjacentSeats.seatsWithDistance.size())) {
                decideBetweenIdenticalScores.put(adjacentSeats.seatsWithDistance.size(), adjacentSeats);
            }
        }
    }

    private static AdjacentSeats
    projectToSeats(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {
        return bestGroups.get(bestGroups.firstKey()).groups.get(0);
    }

    private static boolean
    hasOnlyOneBestGroup(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {
        return bestGroups.get(bestGroups.firstKey()).groups.size() == 1;
    }

    private static AdjacentSeatsGroups
    splitInGroupsOfAdjacentSeats(SuggestionRequest suggestionRequest, List<SeatWithDistance> seatsWithDistances) {

        var adjacentSeats = new AdjacentSeats();
        var  adjacentSeatsGroups = new AdjacentSeatsGroups();
        SeatWithDistance seatWithTheDistancePrevious = null;

        for (SeatWithDistance seat : orderSeatsByTheirNumberToGroupAdjacent(seatsWithDistances)) {
            if (seatWithTheDistancePrevious == null) {
                seatWithTheDistancePrevious = seat;
                adjacentSeats.addSeat(seatWithTheDistancePrevious);
            } else {
                if (seat.seat().number() == seatWithTheDistancePrevious.seat().number() + 1) {
                    adjacentSeats.addSeat(seat);
                    seatWithTheDistancePrevious = seat;
                } else {
                    adjacentSeatsGroups.groups.add(buildAdjacentSeatsSortedByDistanceFromMiddleOfTheRow(adjacentSeats));
                    adjacentSeats = new AdjacentSeats();
                    adjacentSeats.addSeat(seat);
                    seatWithTheDistancePrevious = null;
                }
            }
        }
        adjacentSeatsGroups.groups.add(buildAdjacentSeatsSortedByDistanceFromMiddleOfTheRow(adjacentSeats));

        return sortGroupsByDistanceFromMiddleOfTheRow(suggestionRequest, adjacentSeatsGroups);
    }

    private static AdjacentSeatsGroups
    sortGroupsByDistanceFromMiddleOfTheRow(SuggestionRequest suggestionRequest,
                                           AdjacentSeatsGroups groupOfAdjacentSeats)
    {
        var adjacentSeatsGroups = new AdjacentSeatsGroups();

        for (var adjacentSeats : new ArrayList<>(groupOfAdjacentSeats.groups)) {
            List<SeatWithDistance> seatWithDistances = adjacentSeats.seatsWithDistance.stream().limit(suggestionRequest.partyRequested().partySize()).collect(Collectors.toList());
            adjacentSeatsGroups.groups.add(new AdjacentSeats(seatWithDistances.stream().sorted(Comparator.comparing(SeatWithDistance::distanceFromTheMiddleOfTheRow)).collect(Collectors.toList())));
        }
        return adjacentSeatsGroups;
    }
    private static AdjacentSeats
    buildAdjacentSeatsSortedByDistanceFromMiddleOfTheRow(AdjacentSeats adjacentSeats) {
        return new AdjacentSeats(adjacentSeats.seatsWithDistance.stream()
                .sorted(Comparator.comparing(SeatWithDistance::distanceFromTheMiddleOfTheRow)).collect(Collectors.toList()));
    }

    private static boolean
    any(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {
        return !bestGroups.isEmpty();
    }

    private static boolean
    isMatchingPartyRequested(SuggestionRequest suggestionRequest, AdjacentSeats adjacentSeats) {
        return adjacentSeats.seatsWithDistance.size() >= suggestionRequest.partyRequested().partySize();
    }

    private static List<SeatWithDistance>
    orderSeatsByTheirNumberToGroupAdjacent(List<SeatWithDistance> seatsWithDistances) {
        return seatsWithDistances.stream()
                .sorted(Comparator.comparing(s -> s.seat().number()))
                .collect(Collectors.toList());
    }

    public static List<Seat>
    OfferAdjacentSeats(SuggestionRequest suggestionRequest, List<SeatWithDistance> seatsWithDistances) {

       var groupOfAdjacentSeats = splitInGroupsOfAdjacentSeats(suggestionRequest, seatsWithDistances);

        return adaptAdjacentSeats(selectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(suggestionRequest, groupOfAdjacentSeats));
    }

    private static List<Seat>
    adaptAdjacentSeats(AdjacentSeats adjacentSeats)
    {
        return adjacentSeats.seatsWithDistance.stream().map(SeatWithDistance::seat).collect(Collectors.toList());
    }

    private static AdjacentSeats
    selectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(SuggestionRequest suggestionRequest, AdjacentSeatsGroups adjacentSeatsGroups) {

        TreeMap<Integer, AdjacentSeatsGroups> theBestDistancesNearerToTheMiddleOfTheRowPerGroup = new TreeMap<>();

        // To select the best group of adjacent seats, we sort them by their distances
        for (var adjacentSeats : adjacentSeatsGroups.groups) {

            if (!isMatchingPartyRequested(suggestionRequest, adjacentSeats)) continue;

            var sumOfDistances = sumOfDistancesNearerTheMiddleOfTheRowPerSeat(adjacentSeats);

            if (!theBestDistancesNearerToTheMiddleOfTheRowPerGroup.containsKey(sumOfDistances))
                theBestDistancesNearerToTheMiddleOfTheRowPerGroup.put(sumOfDistances, new AdjacentSeatsGroups());

            theBestDistancesNearerToTheMiddleOfTheRowPerGroup.get(sumOfDistances).groups.add(adjacentSeats);
        }

        return any(theBestDistancesNearerToTheMiddleOfTheRowPerGroup)
                ? selectTheBestGroup(theBestDistancesNearerToTheMiddleOfTheRowPerGroup) : noSeatFound;
    }

    private static Integer
    sumOfDistancesNearerTheMiddleOfTheRowPerSeat(AdjacentSeats adjacentSeats) {

        return adjacentSeats.seatsWithDistance.stream()
                .map(SeatWithDistance::distanceFromTheMiddleOfTheRow)
                .reduce(0, Integer::sum);
    }

}
