package com.baasie.SeatsSuggestionsDomain.port;

import com.baasie.SeatsSuggestionsDomain.SuggestionsMade;

public interface IProvideAuditoriumSeating {

    SuggestionsMade makeSuggestions(String showId, int partyRequested);
}
