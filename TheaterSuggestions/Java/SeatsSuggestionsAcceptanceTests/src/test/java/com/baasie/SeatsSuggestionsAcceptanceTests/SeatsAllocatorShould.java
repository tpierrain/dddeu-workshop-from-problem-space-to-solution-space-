package com.baasie.SeatsSuggestionsAcceptanceTests;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.SeatsSuggestions.*;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class SeatsAllocatorShould {

    @Test
    public void return_SeatsNotAvailable_when_Auditorium_has_all_its_seats_already_reserved() throws IOException {
        // Madison Auditorium-5
        //
        //      1   2   3   4   5   6   7   8   9  10
        // A : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
        // B : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
        final String showId = "5";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade).isInstanceOf(SuggestionNotAvailable.class);
    }

    @Test
    public void suggest_one_seat_when_Auditorium_contains_one_available_seat_only() throws IOException {
        // Ford Auditorium-1
        //
        //       1   2   3   4   5   6   7   8   9  10
        //  A : (2) (2)  1  (1) (1) (1) (1) (1) (2) (2)
        //  B : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
        final String showId = "1";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A3");
    }

    @Test
    public void offer_several_suggestions_ie_1_per_PricingCategory_and_other_one_without_category_affinity() throws IOException {
        // New Amsterdam-18
        //
        //     1   2   3   4   5   6   7   8   9  10
        //  A: 2   2   1   1   1   1   1   1   2   2
        //  B: 2   2   1   1   1   1   1   1   2   2
        //  C: 2   2   2   2   2   2   2   2   2   2
        //  D: 2   2   2   2   2   2   2   2   2   2
        //  E: 3   3   3   3   3   3   3   3   3   3
        //  F: 3   3   3   3   3   3   3   3   3   3
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
    public void offer_adjacent_seats_nearer_the_middle_of_a_row() throws IOException {
        // FIX ME

        // Mogador Auditorium-9
        //
        //    1   2   3   4   5   6   7   8   9  10
        // A: 2   2   1   1  (1) (1) (1) (1)  2   2
        // B: 2   2   1   1   1   1   1   1   2   2
        final String showId = "9";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A4", "A3", "B5");
    }
}
