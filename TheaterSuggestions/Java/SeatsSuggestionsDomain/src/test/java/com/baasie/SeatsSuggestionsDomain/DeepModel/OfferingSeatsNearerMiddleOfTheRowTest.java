package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class OfferingSeatsNearerMiddleOfTheRowTest {

    @Test
    public void be_a_Value_Type() {
        Seat a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        Seat a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
        Seat a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
        Seat a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
        Seat a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
        Seat a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
        Seat a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
        Seat a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
        Seat a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
        Seat a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

        Row row = new Row("A", new ArrayList<>(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)));
        // Two different instances with same values should be equals
        OfferingSeatsNearerMiddleOfTheRow firstInstance = new OfferingSeatsNearerMiddleOfTheRow(row);
        OfferingSeatsNearerMiddleOfTheRow secondInstance = new OfferingSeatsNearerMiddleOfTheRow(row);
        assertThat(secondInstance).isEqualTo(firstInstance);
    }

    @Test
    public void offer_seats_from_the_middle_of_the_row_when_the_row_size_is_even_and_party_size_is_greater_than_one() {
        int partySize = 2;

        Seat a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        Seat a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
        Seat a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
        Seat a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
        Seat a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
        Seat a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
        Seat a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
        Seat a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
        Seat a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
        Seat a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

        Row row = new Row("A", new ArrayList<>(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)));

        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistance = new ArrayList<>(new OfferingSeatsNearerMiddleOfTheRow(row)
                .offerSeatsNearerTheMiddleOfTheRow(new SuggestionRequest(partySize, PricingCategory.Mixed)));

        List<Seat> seats = seatsWithDistance.stream().map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat).limit(partySize).collect(Collectors.toList());

        assertThat(seats).containsExactly(a5, a6);
    }

    @Test
    public void offer_seats_from_the_middle_of_the_row_when_with_the_row_size_is_odd_and_party_size_is_greater_than_one() {
        int partySize = 5;

        Seat a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        Seat a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
        Seat a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Available);
        Seat a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
        Seat a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Available);
        Seat a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Available);
        Seat a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Available);
        Seat a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
        Seat a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);

        Row row = new Row("A", new ArrayList<>(Arrays.asList(a1, a2, a3, a4, a5, a6, a7, a8, a9)));

        List<SeatWithTheDistanceFromTheMiddleOfTheRow> seatsWithDistance = new OfferingSeatsNearerMiddleOfTheRow(row)
                .offerSeatsNearerTheMiddleOfTheRow(new SuggestionRequest(partySize, PricingCategory.Mixed)).stream().limit(partySize).collect(Collectors.toList());

        List<Seat> seats = seatsWithDistance.stream().map(SeatWithTheDistanceFromTheMiddleOfTheRow::seat).sorted(Comparator.comparingInt(Seat::number)).collect(Collectors.toList());

        assertThat(seats).containsExactly(a2, a3, a5, a6, a7);
    }
}
