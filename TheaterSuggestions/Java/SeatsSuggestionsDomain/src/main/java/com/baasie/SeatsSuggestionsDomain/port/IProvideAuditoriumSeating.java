package com.baasie.SeatsSuggestionsDomain.port;

import com.baasie.SeatsSuggestionsDomain.PartyRequested;
import com.baasie.SeatsSuggestionsDomain.ShowId;
import com.baasie.SeatsSuggestionsDomain.SuggestionsMade;

public interface IProvideAuditoriumSeating {

    SuggestionsMade makeSuggestions(ShowId showId, PartyRequested partyRequested);
}
