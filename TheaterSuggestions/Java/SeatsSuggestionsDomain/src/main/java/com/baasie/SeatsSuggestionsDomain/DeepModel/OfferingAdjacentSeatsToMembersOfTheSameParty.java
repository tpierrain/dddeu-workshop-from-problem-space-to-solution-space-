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

    public OfferingAdjacentSeatsToMembersOfTheSameParty(SuggestionRequest suggestionRequest) {
        this.suggestionRequest = suggestionRequest;
    }

    private static List<Seat> selectTheBestGroup(TreeMap<Integer, List<AdjacentSeats>> bestGroups) {

        return hasOnlyOneBestGroup(bestGroups) ?
                projectToSeats(bestGroups) :
                decideWhichGroupIsTheBestWhenDistancesAreEqual(bestGroups);
    }

    private static List<Seat> decideWhichGroupIsTheBestWhenDistancesAreEqual(TreeMap<Integer, List<AdjacentSeats>> bestGroups) {

        TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores = new TreeMap<>();

        for (var collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows : bestGroups.values())
            SelectTheBestScoreBetweenGroups(decideBetweenIdenticalScores, collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows);

        return decideBetweenIdenticalScores.lastEntry().getValue();
    }

    private static void SelectTheBestScoreBetweenGroups(TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores, List<AdjacentSeats> collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows) {
        for (var seatWithTheDistanceFromTheMiddleOfTheRows :
                collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows) {

            if (decideBetweenIdenticalScores.containsKey(seatWithTheDistanceFromTheMiddleOfTheRows.seatsWithDistance.size())) {
                selectTheBestScoreBetweenGroups(decideBetweenIdenticalScores, seatWithTheDistanceFromTheMiddleOfTheRows);
            } else {
                decideBetweenIdenticalScores.put(seatWithTheDistanceFromTheMiddleOfTheRows.seatsWithDistance.size(),
                        projectToSeats(seatWithTheDistanceFromTheMiddleOfTheRows));
            }
        }
    }

    private static List<Integer> ExtractScores(TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores,
                                               AdjacentSeats seatWithTheDistanceFromTheMiddleOfTheRows) {
        var bestGroupScore = seatWithTheDistanceFromTheMiddleOfTheRows.seatsWithDistance
                .stream().map(s -> s.seat().number()).reduce(0, Integer::sum);
        var seatWithTheDistanceFromTheMiddleOfTheRowsContained = decideBetweenIdenticalScores
                .get(seatWithTheDistanceFromTheMiddleOfTheRows.seatsWithDistance.size());
        Integer bestGroupScoreForContained = seatWithTheDistanceFromTheMiddleOfTheRowsContained
                .stream().map(Seat::number).reduce(0, Integer::sum);
        return new ArrayList<>(Arrays.asList(bestGroupScore, bestGroupScoreForContained));

    }

    private static void selectTheBestScoreBetweenGroups(TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores,
                                                        AdjacentSeats seatWithTheDistanceFromTheMiddleOfTheRows) {

        var scores = ExtractScores(decideBetweenIdenticalScores, seatWithTheDistanceFromTheMiddleOfTheRows);

        long bestGroupScore = scores.get(0);
        long bestGroupScoreForContained = scores.get(1);

        if (bestGroupScore < bestGroupScoreForContained)
            decideBetweenIdenticalScores.put(seatWithTheDistanceFromTheMiddleOfTheRows.seatsWithDistance.size(),
                    projectToSeats(seatWithTheDistanceFromTheMiddleOfTheRows));
    }

    private static List<Seat> projectToSeats(AdjacentSeats seatWithTheDistanceFromTheMiddleOfTheRows) {
        return seatWithTheDistanceFromTheMiddleOfTheRows.seatsWithDistance.stream()
                .map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat)
                .collect(Collectors.toList());
    }

    private static List<Seat> projectToSeats(TreeMap<Integer, List<AdjacentSeats>> bestGroups) {
        List<AdjacentSeats> adjacentSeats = bestGroups.get(bestGroups.firstKey());
        List<SeatWithTheDistanceFromTheMiddleOfTheRow> collect = adjacentSeats.stream().map(s -> s.seatsWithDistance).flatMap(List::stream).collect(Collectors.toList());
        return collect.stream().map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat).collect(Collectors.toList());
    }

    private static boolean hasOnlyOneBestGroup(TreeMap<Integer, List<AdjacentSeats>> bestGroups) {
        return bestGroups.get(bestGroups.firstKey()).size() == 1;
    }

    private static AdjacentSeatsGroups splitInGroupsOfAdjacentSeats(
            List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {

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
                    seatWithTheDistancePrevious = null;
                }
            }
        }
        AdjacentSeats seats = buildAdjacentSeatsSortedByDistanceFromMiddleOfTheRow(adjacentSeats);
        adjacentSeatsGroups.groups.add(seats);

        return adjacentSeatsGroups;
    }

    private static AdjacentSeats buildAdjacentSeatsSortedByDistanceFromMiddleOfTheRow(AdjacentSeats adjacentSeats) {
        return new AdjacentSeats(adjacentSeats.seatsWithDistance.stream().sorted(Comparator.comparing(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow)).collect(Collectors.toList()));
    }

    private static boolean any(TreeMap<Integer, List<AdjacentSeats>> bestGroups) {
        return !bestGroups.isEmpty();
    }

    private static boolean isMatchingPartyRequested(SuggestionRequest suggestionRequest, List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithDistances) {
        return seatWithDistances.size() >= suggestionRequest.partyRequested().partySize();
    }

    private static List<SeatWithTheDistanceFromTheMiddleOfTheRow> orderSeatsByTheirNumberToGroupAdjacent(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {
        return seatsWithDistances.stream()
                .sorted(Comparator.comparing(s -> s.seat().number()))
                .collect(Collectors.toList());
    }

    public List<Seat> OfferAdjacentSeats(
            List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {

       var groupOfAdjacentSeats = splitInGroupsOfAdjacentSeats(seatsWithDistances);

        return selectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(groupOfAdjacentSeats);
    }

    private List<Seat> selectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
           AdjacentSeatsGroups groupsOfAdjacentSeats) {

        TreeMap<Integer, List<AdjacentSeats>>
                theBestDistancesNearerToTheMiddleOfTheRowPerGroup = new TreeMap<>();

        for (var currentGroupOfAdjacentSeats : groupsOfAdjacentSeats.groups.stream().map(s -> s.seatsWithDistance).collect(Collectors.toList())) {

            if (!isMatchingPartyRequested(suggestionRequest, currentGroupOfAdjacentSeats)) continue;

            var sumOfDistances = sumOfDistancesNearerTheMiddleOfTheRowPerSeat(currentGroupOfAdjacentSeats);

            if (!theBestDistancesNearerToTheMiddleOfTheRowPerGroup.containsKey(sumOfDistances))
                theBestDistancesNearerToTheMiddleOfTheRowPerGroup.put(sumOfDistances, new ArrayList<>());

            theBestDistancesNearerToTheMiddleOfTheRowPerGroup.get(sumOfDistances).add(new AdjacentSeats(currentGroupOfAdjacentSeats));
        }

        return any(theBestDistancesNearerToTheMiddleOfTheRowPerGroup)
                ? selectTheBestGroup(theBestDistancesNearerToTheMiddleOfTheRowPerGroup) : noSeatFound;
    }

    private Integer sumOfDistancesNearerTheMiddleOfTheRowPerSeat(
            List<SeatWithTheDistanceFromTheMiddleOfTheRow> currentGroup) {

        return currentGroup.stream()
                .map(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow)
                .reduce(0, Integer::sum);
    }

}
