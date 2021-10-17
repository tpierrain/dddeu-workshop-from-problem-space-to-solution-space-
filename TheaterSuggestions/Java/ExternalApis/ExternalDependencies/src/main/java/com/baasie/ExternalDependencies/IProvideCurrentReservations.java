package com.baasie.ExternalDependencies;

import com.baasie.ExternalDependencies.reservationsprovider.ReservedSeatsDto;
import com.baasie.SeatsSuggestionsDomain.ShowId;

public interface IProvideCurrentReservations {

    ReservedSeatsDto getReservedSeats(ShowId showId);
}
