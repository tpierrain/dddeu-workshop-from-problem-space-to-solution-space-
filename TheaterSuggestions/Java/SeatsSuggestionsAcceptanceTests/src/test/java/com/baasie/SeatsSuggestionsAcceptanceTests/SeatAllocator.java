package com.baasie.SeatsSuggestionsAcceptanceTests;

import java.util.Map;

public class SeatAllocator {

    private final AuditoriumSeatingAdapter auditoriumSeatingAdapter;

    public SeatAllocator(AuditoriumSeatingAdapter auditoriumLayoutAdapter) {
        this.auditoriumSeatingAdapter = auditoriumLayoutAdapter;
    }

    public SuggestionMade makeSuggestion(String showId, int partyRequested) {
        Suggestion suggestion = new Suggestion(partyRequested);

        AuditoriumSeating theaterLayout = auditoriumSeatingAdapter.getAuditoriumSeating(showId);

        for (Map.Entry<String, Row> entry : theaterLayout.rows().entrySet()) {
            for (Seat seat : entry.getValue().seats()) {
                if (seat.isAvailable()) {
                    suggestion.addSeat(seat);
                    if (suggestion.isFulFilled()) {
                        return new SuggestionMade(suggestion.seats(), suggestion.partyRequested());
                    }
                }
            }
        }
        return new SuggestionNotAvailable(partyRequested);
    }
}
