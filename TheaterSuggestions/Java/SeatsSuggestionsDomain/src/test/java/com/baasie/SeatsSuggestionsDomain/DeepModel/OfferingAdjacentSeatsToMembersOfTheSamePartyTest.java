package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class OfferingAdjacentSeatsToMembersOfTheSamePartyTest {

    @Test
    public void be_a_Value_Type() {
        // Two different instances with same values should be equals
        OfferingAdjacentSeatsToMembersOfTheSameParty rowFirstInstance = new OfferingAdjacentSeatsToMembersOfTheSameParty(new SuggestionRequest(new PartyRequested(3), PricingCategory.Mixed));
        OfferingAdjacentSeatsToMembersOfTheSameParty rowSecondInstance = new OfferingAdjacentSeatsToMembersOfTheSameParty(new SuggestionRequest(new PartyRequested(3), PricingCategory.Mixed));
        assertThat(rowSecondInstance).isEqualTo(rowFirstInstance);
    }

    @Test
    public void offer_adjacent_seats_nearer_the_middle_of_the_row_when_the_middle_is_not_reserved()
    {
        PartyRequested partyRequested = new PartyRequested(3);

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

        List<SeatWithTheDistanceFromTheMiddleOfTheRow>  seatsWithDistance =
                new OfferingSeatsNearerMiddleOfTheRow(new Row("A", new ArrayList<>(Arrays.asList( a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 ))))
                        .offerSeatsNearerTheMiddleOfTheRow(
                                new SuggestionRequest(partyRequested, PricingCategory.Mixed));

        List<Seat> seats = new OfferingAdjacentSeatsToMembersOfTheSameParty(new SuggestionRequest(partyRequested, PricingCategory.Mixed))
                .OfferAdjacentSeats(seatsWithDistance);

        assertThat(seats).containsExactly(a5, a6, a7);
    }

    @Test
    public void offer_adjacent_seats_nearer_the_middle_of_the_row_when_the_middle_is_already_reserved()
    {
        Seat a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        Seat a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
        Seat a3 = new Seat("A", 3, PricingCategory.First, SeatAvailability.Reserved);
        Seat a4 = new Seat("A", 4, PricingCategory.First, SeatAvailability.Reserved);
        Seat a5 = new Seat("A", 5, PricingCategory.First, SeatAvailability.Reserved);
        Seat a6 = new Seat("A", 6, PricingCategory.First, SeatAvailability.Reserved);
        Seat a7 = new Seat("A", 7, PricingCategory.First, SeatAvailability.Reserved);
        Seat a8 = new Seat("A", 8, PricingCategory.First, SeatAvailability.Reserved);
        Seat a9 = new Seat("A", 9, PricingCategory.Second, SeatAvailability.Available);
        Seat a10 = new Seat("A", 10, PricingCategory.Second, SeatAvailability.Available);

        Row row = new Row("A", new ArrayList<>(Arrays.asList( a1, a2, a3, a4, a5, a6, a7, a8, a9, a10 )));

        assertThat(row.offerAdjacentSeatsNearerTheMiddleOfRow(new SuggestionRequest(new PartyRequested(2)
                        , PricingCategory.Mixed)))
                .containsExactly(a2, a1);
    }
}
