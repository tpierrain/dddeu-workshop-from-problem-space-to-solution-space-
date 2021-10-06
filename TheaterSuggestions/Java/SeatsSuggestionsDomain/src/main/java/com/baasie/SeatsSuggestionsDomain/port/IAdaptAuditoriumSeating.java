package com.baasie.SeatsSuggestionsDomain.port;

import com.baasie.SeatsSuggestionsDomain.AuditoriumSeating;

public interface IAdaptAuditoriumSeating {

    AuditoriumSeating getAuditoriumSeating(String showId);
}
