package com.baasie.SeatsSuggestions.DeepModel;

import com.baasie.SeatsSuggestions.Seat;
import com.baasie.SeatsSuggestions.SuggestionRequest;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class OfferingAdjacentSeatsToMembersOfTheSameParty {
    private static final List<Seat> noSeatSuggested = new ArrayList<>();
    private final SuggestionRequest suggestionRequest;

    public OfferingAdjacentSeatsToMembersOfTheSameParty(SuggestionRequest suggestionRequest) {
        this.suggestionRequest = suggestionRequest;
    }

    private static List<Seat> selectTheBestGroup(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {

        return hasOnlyOneBestGroup(bestGroups) ?
                projectToSeats(bestGroups) :
                decideWhichGroupIsTheBestWhenDistancesAreEqual(bestGroups);
    }

    private static List<Seat> decideWhichGroupIsTheBestWhenDistancesAreEqual(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {

        TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores = new TreeMap<>();

        for (List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows : bestGroups.values())
            SelectTheBestScoreBetweenGroups(decideBetweenIdenticalScores, collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows);

        return decideBetweenIdenticalScores.lastEntry().getValue();
    }

    private static void SelectTheBestScoreBetweenGroups(TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows) {
        for (List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows :
                collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows) {

            if (decideBetweenIdenticalScores.containsKey(seatWithTheDistanceFromTheMiddleOfTheRows.size())) {
                selectTheBestScoreBetweenGroups(decideBetweenIdenticalScores, seatWithTheDistanceFromTheMiddleOfTheRows);
            } else {
                decideBetweenIdenticalScores.put(seatWithTheDistanceFromTheMiddleOfTheRows.size(),
                        projectToSeats(seatWithTheDistanceFromTheMiddleOfTheRows));
            }
        }
    }

    private static List<Integer> ExtractScores(TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores,
                                               List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows) {
        Integer bestGroupScore = seatWithTheDistanceFromTheMiddleOfTheRows
                .stream().map(s -> s.seat().number()).reduce(0, Integer::sum);
        List<Seat> seatWithTheDistanceFromTheMiddleOfTheRowsContained = decideBetweenIdenticalScores.get(seatWithTheDistanceFromTheMiddleOfTheRows.size());
        Integer bestGroupScoreForContained = seatWithTheDistanceFromTheMiddleOfTheRowsContained.stream().map(Seat::number).reduce(0, Integer::sum);
        return new ArrayList<>(Arrays.asList(bestGroupScore, bestGroupScoreForContained));

    }

    private static void selectTheBestScoreBetweenGroups(TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores,
                                                        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows) {

        List<Integer> scores = ExtractScores(decideBetweenIdenticalScores, seatWithTheDistanceFromTheMiddleOfTheRows);

        long bestGroupScore = scores.get(0);
        long bestGroupScoreForContained = scores.get(1);

        if (bestGroupScore < bestGroupScoreForContained)
            decideBetweenIdenticalScores.put(seatWithTheDistanceFromTheMiddleOfTheRows.size(),
                    projectToSeats(seatWithTheDistanceFromTheMiddleOfTheRows));
    }

    private static List<Seat> projectToSeats(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows) {
        return seatWithTheDistanceFromTheMiddleOfTheRows.stream()
                .map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat)
                .collect(Collectors.toList());
    }

    private static List<Seat> projectToSeats(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {
        return projectToSeats(bestGroups.get(bestGroups.firstKey()).get(0));
    }

    private static boolean hasOnlyOneBestGroup(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {
        return bestGroups.get(bestGroups.firstKey()).size() == 1;
    }

    private static List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> splitInGroupsOfAdjacentSeats(
            List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {

        ArrayList<SeatWithTheDistanceFromTheMiddleOfTheRow> groupOfSeatDistance = new ArrayList<>();
        ArrayList<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupsOfSeatDistance = new ArrayList<>();

        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistanceOrderBySeatNumber = orderSeatsByTheirNumberToGroupAdjacent(seatsWithDistances);
        SeatWithTheDistanceFromTheMiddleOfTheRow seatWithTheDistancePrevious = null;

        for (SeatWithTheDistanceFromTheMiddleOfTheRow seat : seatsWithDistanceOrderBySeatNumber) {
            if (seatWithTheDistancePrevious == null) {
                seatWithTheDistancePrevious = seat;
                groupOfSeatDistance.add(seatWithTheDistancePrevious);
            } else {
                if (seat.seat().number() == seatWithTheDistancePrevious.seat().number() + 1) {
                    groupOfSeatDistance.add(seat);
                    seatWithTheDistancePrevious = seat;
                } else {
                    groupsOfSeatDistance.add(groupOfSeatDistance.stream().sorted(Comparator.comparing(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow)).collect(Collectors.toList()));
                    groupOfSeatDistance = new ArrayList<>(Collections.singletonList(seat));
                    seatWithTheDistancePrevious = null;
                }
            }
        }

        groupsOfSeatDistance.add(groupOfSeatDistance.stream().sorted(Comparator.comparing(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow)).collect(Collectors.toList()));

        return groupsOfSeatDistance;
    }

    private static boolean any(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {
        return !bestGroups.isEmpty();
    }

    private static boolean isMatchingPartyRequested(SuggestionRequest suggestionRequest, List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithDistances) {
        return seatWithDistances.size() >= suggestionRequest.partyRequested();
    }

    private static List<SeatWithTheDistanceFromTheMiddleOfTheRow> orderSeatsByTheirNumberToGroupAdjacent(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {
        return seatsWithDistances.stream()
                .sorted(Comparator.comparing(s -> s.seat().number()))
                .collect(Collectors.toList());
    }

    public List<Seat> OfferAdjacentSeats(
            List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {

        List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupOfAdjacentSeats =
                splitInGroupsOfAdjacentSeats(seatsWithDistances);

        return selectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(groupOfAdjacentSeats);
    }

    private List<Seat> selectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
            List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupsOfAdjacentSeats) {

        TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>>
                theBestDistancesNearerToTheMiddleOfTheRowPerGroup = new TreeMap<>();

        for (List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistanceFromMiddleOfTheRow : groupsOfAdjacentSeats) {

            List<SeatWithTheDistanceFromTheMiddleOfTheRow> currentGroupOfAdjacentSeats =
                    sortSeatsByDistanceFromMiddleOfTheRow(seatsWithDistanceFromMiddleOfTheRow);

            if (!isMatchingPartyRequested(suggestionRequest, currentGroupOfAdjacentSeats)) continue;

            int sumOfDistances = sumOfDistancesNearerTheMiddleOfTheRowPerSeat(currentGroupOfAdjacentSeats);

            if (!theBestDistancesNearerToTheMiddleOfTheRowPerGroup.containsKey(sumOfDistances))
                theBestDistancesNearerToTheMiddleOfTheRowPerGroup.put(sumOfDistances, new ArrayList<>());

            theBestDistancesNearerToTheMiddleOfTheRowPerGroup.get(sumOfDistances).add(currentGroupOfAdjacentSeats);
        }

        return any(theBestDistancesNearerToTheMiddleOfTheRowPerGroup)
                ? selectTheBestGroup(theBestDistancesNearerToTheMiddleOfTheRowPerGroup) : noSeatSuggested();
    }

    private List<SeatWithTheDistanceFromTheMiddleOfTheRow> sortSeatsByDistanceFromMiddleOfTheRow(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistanceFromMiddleOfTheRow) {
        return seatsWithDistanceFromMiddleOfTheRow.stream()
                .sorted(Comparator.comparing(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow))
                .collect(Collectors.toList());
    }

    private Integer sumOfDistancesNearerTheMiddleOfTheRowPerSeat(
            List<SeatWithTheDistanceFromTheMiddleOfTheRow> currentGroup) {

        return currentGroup.stream()
                .map(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow)
                .reduce(0, Integer::sum);
    }

    private List<Seat> noSeatSuggested() {
        return noSeatSuggested;
    }
}
