package com.baasie.SeatsSuggestionsAcceptanceTests;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.SeatsSuggestions.*;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class SeatsAllocatorShould {


    @Test
    public void Return_SeatsNotAvailable_when_Auditorium_has_all_its_seats_already_reserved() throws IOException {
        final String showId = "5";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestion(showId, partyRequested);

        assertThat(suggestionsMade).isInstanceOf(SuggestionNotAvailable.class);
    }

    @Test
    public void Suggest_one_seat_when_Auditorium_contains_one_available_seat_only() throws IOException {
        final String showId = "1";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestion(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A3");
    }

    @Test
    public void Offer_several_suggestions_ie_1_per_PricingCategory_and_other_one_without_category_affinity() throws IOException {
        final String showId = "18";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionsMade suggestionsMade = seatAllocator.makeSuggestion(showId, partyRequested);

        assertThat(suggestionsMade.seatNames(PricingCategory.First)).containsExactly("A3", "A4", "A5");
        assertThat(suggestionsMade.seatNames(PricingCategory.Second)).containsExactly("A1", "A2", "A9");
        assertThat(suggestionsMade.seatNames(PricingCategory.Third)).containsExactly("E1", "E2", "E3");

        // BUG!!! => return A6, A7, A8 instead of the expected A1, A2, A3
        assertThat(suggestionsMade.seatNames(PricingCategory.Mixed)).containsExactly("A1", "A2", "A3");
    }

}
