package com.baasie.SeatsSuggestionsAcceptanceTests;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.SeatsSuggestions.*;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class SeatsAllocatorTest {


    @Test
    public void should_return_SeatsNotAvailable_when_Auditorium_has_all_its_seats_already_reserved() throws IOException {
        // Madison Auditorium-5
        //
        //      1   2   3   4   5   6   7   8   9  10
        // A : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
        // B : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
        final String showId = "5";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatsAllocator seatsAllocator = new SeatsAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatsAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade).isInstanceOf(SuggestionNotAvailable.class);
    }

    @Test
    public void should_suggest_one_seat_when_Auditorium_contains_one_available_seat_only() throws IOException {
        // Ford Auditorium-1
        //
        //       1   2   3   4   5   6   7   8   9  10
        //  A : (2) (2)  1  (1) (1) (1) (1) (1) (2) (2)
        //  B : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
        final String showId = "1";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatsAllocator seatsAllocator = new SeatsAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatsAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A3");
    }

    @Test
    public void should_offer_several_suggestions_ie_1_per_PricingCategory_and_other_one_without_category_affinity() throws IOException {
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

        SeatsAllocator seatsAllocator = new SeatsAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatsAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A5", "A6", "A4");
        assertThat(suggestionsMade.seatNames(PricingCategory.Second)).containsExactly("A2", "A9", "A1");
        assertThat(suggestionsMade.seatNames(PricingCategory.Third)).containsExactly("E5", "E6", "E4");

        assertThat(suggestionsMade.seatNames(PricingCategory.Mixed)).containsExactly("A5", "A6", "A4");
    }

    @Test
    public void should_offer_seats_nearer_the_middle_of_a_row() throws IOException {
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

        SeatsAllocator seatsAllocator = new SeatsAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatsAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A4", "A3", "B5");
    }

    @Test
    public void should_offer_4_adjacent_seats_nearer_the_middle_of_a_row_when_it_is_possible() throws IOException {
        // FIX ME

        //
        // Dock Street Auditorium-3
        //
        //      1   2   3   4   5   6   7   8   9  10
        // A : (2) (2) (1) (1) (1)  1   1   1   2   2
        // B :  2   2   1   1  (1) (1) (1) (1)  2   2
        // C :  2   2   2   2   2   2   2   2   2   2
        // D :  2   2   2   2   2   2   2   2   2   2
        // E :  3   3   3   3   3   3   3   3   3   3
        // F :  3   3   3   3   3   3   3   3   3   3
        final String showId = "3";
        final int partyRequested = 4;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatsAllocator seatsAllocator = new SeatsAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatsAllocator.makeSuggestions(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).isEmpty();

        assertThat(suggestionsMade.seatNames(PricingCategory.Second))
                .containsExactly("C4-C5-C6-C7", "D4-D5-D6-D7");
        assertThat(suggestionsMade.seatNames(PricingCategory.Third))
                .containsExactly("E4-E5-E6-E7", "F4-F5-F6-F7");
        assertThat(suggestionsMade.seatNames(PricingCategory.Mixed))
                .containsExactly("A6-A7-A8-A9", "B1-B2-B3-B4", "C4-C5-C6-C7");
    }
    @Test
    public void should_offer_3_adjacent_seats_nearer_the_middle_of_a_row_when_it_is_possible() throws IOException {
        // Dock Street Auditorium-3
        //
        //      1   2   3   4   5   6   7   8   9  10
        // A : (2) (2) (1) (1) (1)  1   1   1   2   2
        // B :  2   2   1   1  (1) (1) (1) (1)  2   2
        // C :  2   2   2   2   2   2   2   2   2   2
        // D :  2   2   2   2   2   2   2   2   2   2
        // E :  3   3   3   3   3   3   3   3   3   3
        // F :  3   3   3   3   3   3   3   3   3   3
        final String showId = "3";
        final int partyRequested = 3;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatsAllocator seatsAllocator = new SeatsAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatsAllocator.makeSuggestions(showId, partyRequested);

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
