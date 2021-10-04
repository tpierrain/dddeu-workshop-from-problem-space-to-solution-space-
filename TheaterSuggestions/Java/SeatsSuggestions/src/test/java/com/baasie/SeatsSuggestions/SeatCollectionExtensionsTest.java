package com.baasie.SeatsSuggestions;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.baasie.SeatsSuggestions.SeatCollectionExtensions.*;
import static com.google.common.truth.Truth.assertThat;

public class SeatCollectionExtensionsTest {
    private static List<Seat> computeDistances(List<Seat> seats, int rowSize) {
        return seats.stream().map(s -> new Seat(s.rowName(), s.number(), s.pricingCategory(), s.seatAvailability(), s.computeDistanceFromRowCentroid(rowSize))).collect(Collectors.toList());
    }

    @Test
    public void SelectAvailableSeatsCompliantWith_when_all_are_First_and_requesting_First() {
        List<Seat> seats = new ArrayList<>(Arrays.asList(
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 2, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 3, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 7, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.First, SeatAvailability.Available)
        ));

        List<Seat> seatsCompliant = selectAvailableSeatsCompliant(seats, PricingCategory.First);
        assertThat(seatsCompliant.stream().map(Seat::toString).collect(Collectors.toList()))
                .containsExactly("A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10");
    }


    @Test
    public void SelectAvailableSeatsCompliantWith_when_some_are_second_and_requesting_second() {
        List<Seat> seats = new ArrayList<>(Arrays.asList(
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 2, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 3, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 7, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.First, SeatAvailability.Available)
        ));

        List<Seat> seatsCompliant = selectAvailableSeatsCompliant(seats, PricingCategory.Second);
        assertThat(seatsCompliant.stream().map(Seat::toString).collect(Collectors.toList())).containsExactly("A3", "A6");
    }

    @Test
    public void Return_empty_when_SelectAdjacentSeats_is_called_and_no_adjacentSeats_are_available() {
        List<Seat> seats = new ArrayList<>(Arrays.asList(
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 3, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 7, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available)
        ));

        List<AdjacentSeats> adjacentSeats = selectAdjacentSeats(seats, 2);
        assertThat(adjacentSeats).isEmpty();
    }

    @Test
    public void Return_a_few_adjacent_seats_when_SelectAdjacentSeats_is_called() {
        List<Seat> seats = new ArrayList<>(Arrays.asList(
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),

                new Seat("A", 3, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.Second, SeatAvailability.Available),

                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.First, SeatAvailability.Available)
        ));

        seats = computeDistances(seats, 10);

        List<AdjacentSeats> adjacentSeats = selectAdjacentSeats(seats, 2);
        assertThat(adjacentSeats.stream().map(AdjacentSeats::toString).collect(Collectors.toList())).containsExactly("A5-A6");
    }

    @Test
    public void Order_by_middle_of_the_Row() {
        List<Seat> seats = new ArrayList<>(Arrays.asList(
                new Seat("A", 1, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 2, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 3, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 7, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.First, SeatAvailability.Available)
        ));

        seats = computeDistances(seats, seats.size());

        List<AdjacentSeats> adjacentSeats = selectAdjacentSeats(seats, 4);
        List<AdjacentSeats> orderByMiddleOfTheRow = orderByMiddleOfTheRow(adjacentSeats, 10);
        assertThat(orderByMiddleOfTheRow.stream().map(AdjacentSeats::toString).collect(Collectors.toList())).containsExactly("A4-A5-A6-A7");
    }

    @Test
    public void Order_by_middle_of_the_Row_when_every_seat_is_available() {
        List<Seat> seats = new ArrayList<>(Arrays.asList(
                new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 3, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 4, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 5, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 6, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 7, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 8, PricingCategory.First, SeatAvailability.Available),
                new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available),
                new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available)
        ));

        List<AdjacentSeats> adjacentSeats = selectAdjacentSeats(seats, 1);
        List<AdjacentSeats> orderByMiddleOfTheRow = orderByMiddleOfTheRow(adjacentSeats, 10);
        assertThat(orderByMiddleOfTheRow.stream().map(AdjacentSeats::toString).collect(Collectors.toList()))
                .containsExactly("A5", "A6", "A4", "A7", "A3", "A8", "A2", "A9", "A1", "A10");
    }

}