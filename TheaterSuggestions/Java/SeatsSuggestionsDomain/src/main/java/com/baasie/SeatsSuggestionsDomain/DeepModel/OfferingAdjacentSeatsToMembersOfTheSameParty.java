package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.Seat;
import com.baasie.SeatsSuggestionsDomain.SuggestionRequest;
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

        return hasTheBestGroupWithoutConflict(bestGroups) ?
                selectSeatsFrom(bestGroups) :
                decideBetweenIdenticalScores(bestGroups);
    }

    private static List<Seat> decideBetweenIdenticalScores(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {

        return decideTheBestGroup(bestGroups);
    }

    private static List<Seat> decideTheBestGroup(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {

        TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores = new TreeMap<>();

        for (List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows :
                bestGroups.values()) {

            for (List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows :
                    collectionOfSeatWithTheDistanceFromTheMiddleOfTheRows) {

                if (decideBetweenIdenticalScores.containsKey(seatWithTheDistanceFromTheMiddleOfTheRows.size())) {

                    // Groups are equivalents, the domain expert have decided to select the group with the smallest seat numbers
                    // =========================================================================================================
                    Integer bestGroupScore = seatWithTheDistanceFromTheMiddleOfTheRows.stream().map(s -> s.seat().number())
                            .reduce(0, Integer::sum);

                    List<Seat> seatWithTheDistanceFromTheMiddleOfTheRowsContained = decideBetweenIdenticalScores.get(seatWithTheDistanceFromTheMiddleOfTheRows.size());
                    Integer bestGroupScoreForContained = seatWithTheDistanceFromTheMiddleOfTheRowsContained.stream().map(s -> s.number()).reduce(0, Integer::sum);
                    if (bestGroupScore < bestGroupScoreForContained)
                        decideBetweenIdenticalScores.put(seatWithTheDistanceFromTheMiddleOfTheRows.size(),
                            projectToSeats(seatWithTheDistanceFromTheMiddleOfTheRows));
                }
                else {
                    decideBetweenIdenticalScores.put(seatWithTheDistanceFromTheMiddleOfTheRows.size(),
                            projectToSeats(seatWithTheDistanceFromTheMiddleOfTheRows));
                }
            }
        }

        return decideBetweenIdenticalScores.lastEntry().getValue();
    }

    private static List<Seat> projectToSeats(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows) {
        return seatWithTheDistanceFromTheMiddleOfTheRows.stream()
                .map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat)
                .collect(Collectors.toList());
    }

    private static List<Seat> selectSeatsFrom(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {
        return projectToSeats(bestGroups.get(bestGroups.firstKey()).get(0));
    }

    private static boolean hasTheBestGroupWithoutConflict(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {
        // if the first entry is alone, there is no conflict
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
        return seatWithDistances.size() >= suggestionRequest.partyRequested().partySize();
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
