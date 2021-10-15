package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.Seat;
import com.baasie.SeatsSuggestionsDomain.SuggestionRequest;

import java.util.*;
import java.util.stream.Collectors;

public class OfferingAdjacentSeatsToMembersOfTheSameParty {
    private final SuggestionRequest suggestionRequest;
    private static final List<Seat> noSeatSuggested = new ArrayList<>();

    public OfferingAdjacentSeatsToMembersOfTheSameParty(SuggestionRequest suggestionRequest) {
        this.suggestionRequest = suggestionRequest;
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
                bestDistanceNearerTheMiddleOfRowPerGroup = new TreeMap<>();

        for (List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistanceFromMiddleOfTheRow : groupsOfAdjacentSeats) {

            List<SeatWithTheDistanceFromTheMiddleOfTheRow> currentGroupOfAdjacentSeats =
                    SortSeatsByDistanceFromMiddleOfTheRow(seatsWithDistanceFromMiddleOfTheRow);

            if (!isMatchingPartyRequested(suggestionRequest, currentGroupOfAdjacentSeats)) continue;

            int sumOfDistances = sumOfDistancesPerSeat(currentGroupOfAdjacentSeats);

            if (!bestDistanceNearerTheMiddleOfRowPerGroup.containsKey(sumOfDistances))
                bestDistanceNearerTheMiddleOfRowPerGroup.put(sumOfDistances, new ArrayList<>());

            bestDistanceNearerTheMiddleOfRowPerGroup.get(sumOfDistances).add(currentGroupOfAdjacentSeats);
        }

        return any(bestDistanceNearerTheMiddleOfRowPerGroup)
                ? selectTheBestGroup(bestDistanceNearerTheMiddleOfRowPerGroup) : noSeatSuggested();
    }

    private List<SeatWithTheDistanceFromTheMiddleOfTheRow> SortSeatsByDistanceFromMiddleOfTheRow(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistanceFromMiddleOfTheRow) {
        return seatsWithDistanceFromMiddleOfTheRow.stream()
                .sorted(Comparator.comparing(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow))
                .collect(Collectors.toList());
    }

    private Integer sumOfDistancesPerSeat(List<SeatWithTheDistanceFromTheMiddleOfTheRow> currentGroup) {
        return currentGroup.stream()
                .map(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow)
                .reduce(0, Integer::sum);
    }

    private static List<Seat> selectTheBestGroup(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {

        if (any(bestGroups)) {

            if (hasTheBestGroupWithoutConflict(bestGroups))
            {
                return selectSeats(bestGroups);
            }

            return decideBetweenIdenticalScores(bestGroups);
        }
        return selectTheOnlyBestGroup(bestGroups);
    }

    private static List<Seat> decideBetweenIdenticalScores(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {

        TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores = populateTheBestGroups(bestGroups);

        return selectTheGroupWhoseSizeIsTheLargestWithEqualScore(decideBetweenIdenticalScores);
    }

    private static TreeMap<Integer, List<Seat>> populateTheBestGroups(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {

        TreeMap<Integer, List<Seat>> decideBetweenIdenticalScores = new TreeMap<>();

        for (List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> listList : bestGroups.values()) {

            for (List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRows : listList) {

                decideBetweenIdenticalScores.put(seatWithTheDistanceFromTheMiddleOfTheRows.size(),
                        seatWithTheDistanceFromTheMiddleOfTheRows.stream()
                                .map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat)
                                .collect(Collectors.toList()));
            }
        }
        return decideBetweenIdenticalScores;
    }

    private static List<Seat> selectSeats(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {
        return bestGroups.get(bestGroups.firstKey()).get(0).stream().map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat).collect(Collectors.toList());
    }

    private static boolean hasTheBestGroupWithoutConflict(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {
        return bestGroups.get(bestGroups.firstKey()).size() == 1;
    }

    private static List<Seat> selectTheGroupWhoseSizeIsTheLargestWithEqualScore(TreeMap<Integer, List<Seat>> groupsWithHighScore) {
        return groupsWithHighScore.firstEntry().getValue();
    }

    private static List<Seat> selectTheOnlyBestGroup(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestGroups) {
        if (any(bestGroups)) {
            NavigableMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> integerListNavigableMap = bestGroups.descendingMap();
            return integerListNavigableMap.firstEntry().getValue().get(0).stream().map(s -> s.seat()).collect(Collectors.toList());
        }
        return noSeatSuggested;
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

    private List<Seat> noSeatSuggested() {
        return noSeatSuggested;
    }

    private static List<SeatWithTheDistanceFromTheMiddleOfTheRow> orderSeatsByTheirNumberToGroupAdjacent(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances) {
        return seatsWithDistances.stream()
                .sorted(Comparator.comparing(s -> s.seat().number()))
                .collect(Collectors.toList());
    }
}
