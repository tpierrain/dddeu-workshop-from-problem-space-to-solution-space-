package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.Seat;
import com.baasie.SeatsSuggestionsDomain.SuggestionRequest;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class OfferingAdjacentSeatsToMembersOfTheSameParty {
    private static final List<Seat> noSeatFound = new ArrayList<>();
    private final SuggestionRequest suggestionRequest;

    public
    OfferingAdjacentSeatsToMembersOfTheSameParty(SuggestionRequest suggestionRequest) {
        this.suggestionRequest = suggestionRequest;
    }

    private static List<Seat>
    selectTheBestGroup(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {

        return hasOnlyOneBestGroup(bestGroups) ?
                projectToSeats(bestGroups) :
                decideWhichGroupIsTheBestWhenDistancesAreEqual(bestGroups);
    }

    private static List<Seat>
    decideWhichGroupIsTheBestWhenDistancesAreEqual(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {

        TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores = new TreeMap<>();

        for (var adjacentSeatsGroups : bestGroups.values())
            SelectTheBestScoreBetweenGroups(decideBetweenIdenticalScores, adjacentSeatsGroups);

        return decideBetweenIdenticalScores.lastEntry().getValue();
    }

    private static void
    SelectTheBestScoreBetweenGroups(TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores, AdjacentSeatsGroups adjacentSeatsList) {

        for (var adjacentSeats : adjacentSeatsList.groups) {

            if (decideBetweenIdenticalScores.containsKey(adjacentSeats.seatsWithDistance.size())) {
                selectTheBestScoreBetweenGroups(decideBetweenIdenticalScores, adjacentSeats);
            } else {
                decideBetweenIdenticalScores.put(adjacentSeats.seatsWithDistance.size(),
                        projectToSeats(adjacentSeats));
            }
        }
    }

    private static
    List<Integer> ExtractScores(TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores, AdjacentSeats adjacentSeats) {

        var bestGroupScore = adjacentSeats.seatsWithDistance
                .stream().map(s -> s.seat().number()).reduce(0, Integer::sum);

        var seatWithTheDistanceFromTheMiddleOfTheRowsContained = decideBetweenIdenticalScores
                .get(adjacentSeats.seatsWithDistance.size());

        Integer bestGroupScoreForContained = seatWithTheDistanceFromTheMiddleOfTheRowsContained
                .stream().map(Seat::number).reduce(0, Integer::sum);

        return new ArrayList<>(Arrays.asList(bestGroupScore, bestGroupScoreForContained));
    }

    private static void
    selectTheBestScoreBetweenGroups(TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores, AdjacentSeats adjacentSeats) {

        var scores = ExtractScores(decideBetweenIdenticalScores, adjacentSeats);

        long bestGroupScore = scores.get(0);
        long bestGroupScoreForContained = scores.get(1);

        if (bestGroupScore < bestGroupScoreForContained)
            decideBetweenIdenticalScores.put(adjacentSeats.seatsWithDistance.size(),
                    projectToSeats(adjacentSeats));
    }

    private static List<Seat>
    projectToSeats(AdjacentSeats adjacentSeats) {
        return adjacentSeats.seatsWithDistance.stream()
                .map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat)
                .collect(Collectors.toList());
    }

    private static List<Seat>
    projectToSeats(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {
        AdjacentSeatsGroups adjacentSeatsGroups = bestGroups.get(bestGroups.firstKey());
        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWitDistance = adjacentSeatsGroups.groups.stream().map(s -> s.seatsWithDistance).flatMap(List::stream).collect(Collectors.toList());
        return seatsWitDistance.stream().map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat).collect(Collectors.toList());
    }

    private static boolean
    hasOnlyOneBestGroup(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {
        return bestGroups.get(bestGroups.firstKey()).groups.size() == 1;
    }

    private static AdjacentSeatsGroups
    splitInGroupsOfAdjacentSeats(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {

        var adjacentSeats = new AdjacentSeats();
        var  adjacentSeatsGroups = new AdjacentSeatsGroups();
        SeatWithTheDistanceFromTheMiddleOfTheRow seatWithTheDistancePrevious = null;

        for (SeatWithTheDistanceFromTheMiddleOfTheRow seat : orderSeatsByTheirNumberToGroupAdjacent(seatsWithDistances)) {
            if (seatWithTheDistancePrevious == null) {
                seatWithTheDistancePrevious = seat;
                adjacentSeats.addSeat(seatWithTheDistancePrevious);
            } else {
                if (seat.seat().number() == seatWithTheDistancePrevious.seat().number() + 1) {
                    adjacentSeats.addSeat(seat);
                    seatWithTheDistancePrevious = seat;
                } else {
                    AdjacentSeats seats = buildAdjacentSeatsSortedByDistanceFromMiddleOfTheRow(adjacentSeats);
                    adjacentSeatsGroups.groups.add(seats);
                    adjacentSeats = new AdjacentSeats();
                    adjacentSeats.addSeat(seat);
                    seatWithTheDistancePrevious = null;
                }
            }
        }
        AdjacentSeats seats = buildAdjacentSeatsSortedByDistanceFromMiddleOfTheRow(adjacentSeats);
        adjacentSeatsGroups.groups.add(seats);

        return adjacentSeatsGroups;
    }

    private static AdjacentSeats
    buildAdjacentSeatsSortedByDistanceFromMiddleOfTheRow(AdjacentSeats adjacentSeats) {
        return new AdjacentSeats(adjacentSeats.seatsWithDistance.stream().sorted(Comparator.comparing(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow)).collect(Collectors.toList()));
    }

    private static boolean
    any(TreeMap<Integer, AdjacentSeatsGroups> bestGroups) {
        return !bestGroups.isEmpty();
    }

    private static boolean
    isMatchingPartyRequested(SuggestionRequest suggestionRequest, AdjacentSeats adjacentSeats) {
        return adjacentSeats.seatsWithDistance.size() >= suggestionRequest.partyRequested().partySize();
    }

    private static List<SeatWithTheDistanceFromTheMiddleOfTheRow>
    orderSeatsByTheirNumberToGroupAdjacent(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {
        return seatsWithDistances.stream()
                .sorted(Comparator.comparing(s -> s.seat().number()))
                .collect(Collectors.toList());
    }

    public List<Seat>
    OfferAdjacentSeats(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {

       var groupOfAdjacentSeats = splitInGroupsOfAdjacentSeats(seatsWithDistances);

        return selectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(groupOfAdjacentSeats);
    }

    private List<Seat>
    selectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(AdjacentSeatsGroups adjacentSeatsGroups) {

        TreeMap<Integer, AdjacentSeatsGroups>
                theBestDistancesNearerToTheMiddleOfTheRowPerGroup = new TreeMap<>();

        for (var currentAdjacentSeats : adjacentSeatsGroups.groups
                .stream().map(s -> new ArrayList<>(s.seatsWithDistance)).map(AdjacentSeats::new).collect(Collectors.toList())) {

            if (!isMatchingPartyRequested(suggestionRequest, currentAdjacentSeats)) continue;

            var sumOfDistances = sumOfDistancesNearerTheMiddleOfTheRowPerSeat(currentAdjacentSeats);

            if (!theBestDistancesNearerToTheMiddleOfTheRowPerGroup.containsKey(sumOfDistances))
                theBestDistancesNearerToTheMiddleOfTheRowPerGroup.put(sumOfDistances, new AdjacentSeatsGroups());

            theBestDistancesNearerToTheMiddleOfTheRowPerGroup.get(sumOfDistances).groups.add(currentAdjacentSeats);
        }

        return any(theBestDistancesNearerToTheMiddleOfTheRowPerGroup)
                ? selectTheBestGroup(theBestDistancesNearerToTheMiddleOfTheRowPerGroup) : noSeatFound;
    }

    private Integer
    sumOfDistancesNearerTheMiddleOfTheRowPerSeat(AdjacentSeats adjacentSeats) {

        return adjacentSeats.seatsWithDistance.stream()
                .map(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow)
                .reduce(0, Integer::sum);
    }

}
