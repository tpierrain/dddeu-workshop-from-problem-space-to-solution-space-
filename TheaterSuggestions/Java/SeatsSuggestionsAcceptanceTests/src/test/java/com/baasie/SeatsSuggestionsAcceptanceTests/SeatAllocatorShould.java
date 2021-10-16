package com.baasie.SeatsSuggestionsAcceptanceTests;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class SeatAllocatorShould {

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

        SuggestionMade suggestionMade = seatAllocator.makeSuggestion(showId, partyRequested);

        assertThat(suggestionMade.suggestedSeats()).hasSize(1);
        assertThat(suggestionMade.suggestedSeats().get(0).toString()).isEqualTo("A3");
    }

    @Test
    public void return_SeatsNotAvailable_when_Auditorium_has_all_its_seats_already_reserved() throws IOException {
        // Madison Auditorium-5
        //      1   2   3   4   5   6   7   8   9  10
        // A : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
        // B : (2) (2) (1) (1) (1) (1) (1) (1) (2) (2)
        final String showId = "5";
        final int partyRequested = 1;

        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        SeatAllocator seatAllocator = new SeatAllocator(auditoriumLayoutAdapter);

        SuggestionMade suggestionMade = seatAllocator.makeSuggestion(showId, partyRequested);

        assertThat(suggestionMade.suggestedSeats()).hasSize(0);
        assertThat(suggestionMade).isInstanceOf(SuggestionNotAvailable.class);
    }

}
