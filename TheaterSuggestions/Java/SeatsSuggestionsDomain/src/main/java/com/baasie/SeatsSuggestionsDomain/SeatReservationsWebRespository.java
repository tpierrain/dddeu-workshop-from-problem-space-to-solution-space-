package com.baasie.SeatsSuggestionsDomain;

import com.baasie.ExternalDependencies.IProvideCurrentReservations;
import com.baasie.ExternalDependencies.reservationsprovider.ReservedSeatsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SeatReservationsWebRespository implements IProvideCurrentReservations {

    private String uriSeatReservationService;

    public SeatReservationsWebRespository() {
        this.uriSeatReservationService = "http://localhost:8091/";
    }

    @Override
    public ReservedSeatsDto getReservedSeats(String showId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ReservedSeatsDto> response
                = restTemplate.getForEntity(uriSeatReservationService + "/api/data_for_reservation_seats/" + showId, ReservedSeatsDto.class);
        return response.getBody();
    }
}
