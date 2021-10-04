package com.baasie.SeatsSuggestionsAcceptanceTests;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.SeatsSuggestions.*;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.common.truth.Truth.assertThat;

public class SeatsAllocatorShould {

    @Test
    public void Return_SeatsNotAvailable_when_Auditorium_has_all_its_seats_already_reserved() throws IOException {
        final String showId = "5";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade).isInstanceOf(SuggestionNotAvailable.class);
    }

    @Test
    public void Suggest_one_seat_when_Auditorium_contains_one_available_seat_only() throws IOException {
        final String showId = "1";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A3");
    }

    @Test
    public void Offer_several_suggestions_ie_1_per_PricingCategory_and_other_one_without_category_affinity() throws IOException {
        final String showId = "18";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A5", "A6", "A4");
        assertThat(suggestionsMade.seatNames(PricingCategory.Second)).containsExactly("A2", "A9", "A1");
        assertThat(suggestionsMade.seatNames(PricingCategory.Third)).containsExactly("E5", "E6", "E4");

        assertThat(suggestionsMade.seatNames(PricingCategory.Mixed)).containsExactly("A5", "A6", "A4");
    }

    @Test
    public void Offer_adjacent_seats_nearer_the_middle_of_a_row() throws IOException {
        final String showId = "9";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A4", "A3", "B5");
    }
    @Test
    public void Offer_4_adjacent_seats_nearer_the_middle_of_a_row_when_it_is_possible() throws IOException, URISyntaxException {
        final String showId = "3";
        final int partyRequested = 4;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).isEmpty();
        assertThat(suggestionsMade.seatNames(PricingCategory.Second))
                .containsExactly("C4-C5-C6-C7", "D4-D5-D6-D7");
        assertThat(suggestionsMade.seatNames(PricingCategory.Third))
                .containsExactly("E4-E5-E6-E7", "F4-F5-F6-F7");
        assertThat(suggestionsMade.seatNames(PricingCategory.Mixed))
                .containsExactly("A6-A7-A8-A9", "B1-B2-B3-B4", "C4-C5-C6-C7");
    }


    @Test
    public void Offer_3_adjacent_seats_nearer_the_middle_of_a_row_when_it_is_possible() throws IOException, URISyntaxException {
        final String showId = "3";
        final int partyRequested = 3;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).
                containsExactly("A6-A7-A8");
        assertThat(suggestionsMade.seatNames(PricingCategory.Second))
                .containsExactly("C4-C5-C6", "C7-C8-C9", "C1-C2-C3");
        assertThat(suggestionsMade.seatNames(PricingCategory.Third))
                .containsExactly("E4-E5-E6", "E7-E8-E9", "E1-E2-E3");
        assertThat(suggestionsMade.seatNames(PricingCategory.Mixed))
                .containsExactly("A6-A7-A8", "B2-B3-B4", "C4-C5-C6");
    }
}
