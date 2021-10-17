package com.baasie.SeatsSuggestionsDomain.port;

import com.baasie.SeatsSuggestionsDomain.AuditoriumSeating;
import com.baasie.SeatsSuggestionsDomain.ShowId;

public interface IAdaptAuditoriumSeating {

    AuditoriumSeating getAuditoriumSeating(ShowId showId);
}
