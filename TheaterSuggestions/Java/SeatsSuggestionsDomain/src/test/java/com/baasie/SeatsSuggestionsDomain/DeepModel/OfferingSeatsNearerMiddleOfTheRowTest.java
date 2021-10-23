package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class OfferingSeatsNearerMiddleOfTheRowTest {

    @Test
    public void be_a_Value_Type() {
        var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
        var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
        var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
        var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
        var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
        var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
        var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
        var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
        var a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

        var row = new Row("A", new ArrayList<>(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)));

        // Two different instances with same values should be equals
        assertThat(new OfferingSeatsNearerMiddleOfTheRow(row))
                .isEqualTo(new OfferingSeatsNearerMiddleOfTheRow(row));
    }

    @Test
    public void offer_seats_from_the_middle_of_the_row_when_the_row_size_is_even_and_party_size_is_greater_than_one() {
        PartyRequested partyRequested = new PartyRequested(2);

        var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
        var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
        var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
        var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
        var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
        var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
        var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
        var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
        var a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

        var row = new Row("A", new ArrayList<>(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)));

        var seatsWithDistance = new ArrayList<>(new OfferingSeatsNearerMiddleOfTheRow(row)
                .offerSeatsNearerTheMiddleOfTheRow(new SuggestionRequest(partyRequested, PricingCategory.Mixed)));

        var seats = seatsWithDistance.stream().map(SeatWithDistance::seat).limit(partyRequested.partySize()).collect(Collectors.toList());

        assertThat(seats).containsExactly(a5, a6);
    }

    @Test
    public void offer_seats_from_the_middle_of_the_row_when_with_the_row_size_is_odd_and_party_size_is_greater_than_one() {
        PartyRequested partyRequested = new PartyRequested(5);

        var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
        var a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
        var a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
        var a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
        var a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
        var a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
        var a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
        var a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);

        var row = new Row("A", new ArrayList<>(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9)));

        var seatsWithDistance = new OfferingSeatsNearerMiddleOfTheRow(row)
                .offerSeatsNearerTheMiddleOfTheRow(new SuggestionRequest(partyRequested, PricingCategory.Mixed)).stream().limit(partyRequested.partySize()).collect(Collectors.toList());

        var seats = seatsWithDistance.stream().map(SeatWithDistance::seat).sorted(Comparator.comparingInt(Seat::number)).collect(Collectors.toList());

        assertThat(seats).containsExactly(a2, a3, a5, a6, a7);
    }
}
