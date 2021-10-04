package com.baasie.ExternalDependencies;

import com.baasie.ExternalDependencies.reservationsprovider.ReservedSeatsDto;

public interface IProvideCurrentReservations {

    ReservedSeatsDto getReservedSeats(String showId);
}
