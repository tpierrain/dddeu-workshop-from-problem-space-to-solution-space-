package com.baasie.SeatsSuggestions.DeepModel;

import com.baasie.SeatsSuggestions.Seat;
import com.baasie.SeatsSuggestions.SuggestionRequest;

import java.util.*;
import java.util.stream.Collectors;

public class OfferingAdjacentSeatsToMembersOfTheSameParty {
    private final SuggestionRequest suggestionRequest;
    private static final List<Seat> noSeatSuggested = new ArrayList<>();

    public OfferingAdjacentSeatsToMembersOfTheSameParty(SuggestionRequest suggestionRequest) {
        this.suggestionRequest = suggestionRequest;
    }
    private List<Seat> NoSeatSuggested() {
        return noSeatSuggested;
    }

    public List<Seat> OfferAdjacentSeats(
            List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances)
    {
        return SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
                SplitInGroupsOfAdjacentSeats(seatsWithDistances));
    }

    private List<Seat> SelectAdjacentSeatsWithShorterDistanceFromTheMiddleOfTheRow(
            List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupOfAdjacentSeats)
    {
        TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestDistances = new TreeMap<>();

        List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> seatsWithTheDistance = groupOfAdjacentSeats.stream().sorted(Comparator.comparingInt(s -> s.get(0).seat().number())).collect(Collectors.toList());

        for (List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistance : seatsWithTheDistance)
        {
            if (!IsMatchingPartyRequested(suggestionRequest, seatsWithDistance)) continue;

            Integer sumOfDistances = seatsWithDistance.stream().map(SeatWithTheDistanceFromTheMiddleOfTheRow::distanceFromTheMiddleOfTheRow).reduce(0, Integer::sum);

            if (!bestDistances.containsKey(sumOfDistances))
                bestDistances.put(sumOfDistances, new ArrayList<>());

            bestDistances.get(sumOfDistances).add(seatsWithDistance);
        }

        return bestDistances.size() > 0
                ?  SelectTheBestGroup(bestDistances)
                : NoSeatSuggested();
    }


    private static List<Seat> SelectTheBestGroup(
            TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestDistances)
    {
        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithTheDistanceFromTheMiddleOfTheRows = First(bestDistances.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));

        if (HaveMultipleBestHighScores(seatsWithTheDistanceFromTheMiddleOfTheRows))
        {
            TreeMap<Integer, List<SeatWithTheDistanceFromTheMiddleOfTheRow>> decideBetweenIdenticalScores = new TreeMap<>();

            for (List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithTheDistanceFromTheMiddleOfTheRow : bestDistances.values().stream().flatMap(Collection::stream).collect(Collectors.toList())) {
                decideBetweenIdenticalScores.put(seatWithTheDistanceFromTheMiddleOfTheRow.size(),
                        seatWithTheDistanceFromTheMiddleOfTheRow);
            }

            decideBetweenIdenticalScores.size();

            return SelectTheGroupWhoseSizeIsTheLargestWithEqualScore(decideBetweenIdenticalScores);
        }

        return SelectTheOnlyBestGroup(bestDistances);
    }

    private static List<Seat> SelectTheGroupWhoseSizeIsTheLargestWithEqualScore(TreeMap<Integer, List<SeatWithTheDistanceFromTheMiddleOfTheRow>> decideBetweenIdenticalScores) {
        for (List<SeatWithTheDistanceFromTheMiddleOfTheRow> value : decideBetweenIdenticalScores.values())
        {
            return value.stream().map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat).collect(Collectors.toList());
        }
        return noSeatSuggested;
    }

    private static List<Seat> SelectTheOnlyBestGroup(TreeMap<Integer, List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>>> bestDistances) {
        return bestDistances.values().stream().flatMap(Collection::stream).flatMap(Collection::stream).map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat).collect(Collectors.toList());
    }

    static List<SeatWithTheDistanceFromTheMiddleOfTheRow> First(List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> items)
    {
        Iterator<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> iterator = items.iterator();
        return iterator.next();
    }
    private static boolean HaveMultipleBestHighScores(
            List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithTheDistanceFromTheMiddleOfTheRows)
    {
        return seatsWithTheDistanceFromTheMiddleOfTheRows.size() > 1;
    }

    private static boolean IsMatchingPartyRequested(SuggestionRequest suggestionRequest, List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatWithDistances)
    {
        return seatWithDistances.size() >= suggestionRequest.partyRequested();
    }

    private static List<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> SplitInGroupsOfAdjacentSeats(
            List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances)
    {
        ArrayList<SeatWithTheDistanceFromTheMiddleOfTheRow> groupOfSeatDistance = new ArrayList<>();
        ArrayList<List<SeatWithTheDistanceFromTheMiddleOfTheRow>> groupsOfSeatDistance = new ArrayList<>();

        Iterable<SeatWithTheDistanceFromTheMiddleOfTheRow> enumerator = orderSeatsByTheirNumberToGroupAdjacent(seatsWithDistances);
        SeatWithTheDistanceFromTheMiddleOfTheRow seatWithTheDistancePrevious = null;

        for (SeatWithTheDistanceFromTheMiddleOfTheRow seatWithDistance : enumerator) {
            if (seatWithTheDistancePrevious == null) {
                seatWithTheDistancePrevious = seatWithDistance;
                groupOfSeatDistance.add(seatWithTheDistancePrevious);
            } else {
                if (seatWithDistance.seat().number() == seatWithTheDistancePrevious.seat().number() + 1) {
                    groupOfSeatDistance.add(seatWithDistance);
                    seatWithTheDistancePrevious = seatWithDistance;
                } else {
                    groupsOfSeatDistance.add(groupOfSeatDistance);
                    groupOfSeatDistance = new ArrayList<>(Collections.singletonList(seatWithDistance));
                    seatWithTheDistancePrevious = null;
                }
            }
        }
        if (!(groupOfSeatDistance.size() <= 0))
        {
            groupsOfSeatDistance.add(groupOfSeatDistance);
        }

        return groupsOfSeatDistance;
    }

    private static List<SeatWithTheDistanceFromTheMiddleOfTheRow> orderSeatsByTheirNumberToGroupAdjacent(List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistances)
    {
        return seatsWithDistances.stream().sorted(Comparator.comparing(s -> s.seat().number())).collect(Collectors.toList());
    }
}
