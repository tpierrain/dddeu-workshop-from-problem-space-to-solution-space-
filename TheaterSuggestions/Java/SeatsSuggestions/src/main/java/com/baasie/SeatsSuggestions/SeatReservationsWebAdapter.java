package com.baasie.SeatsSuggestions;

import com.baasie.ExternalDependencies.IProvideCurrentReservations;
import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumDto;
import com.baasie.ExternalDependencies.reservationsprovider.ReservedSeatsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class SeatReservationsWebAdapter implements IProvideCurrentReservations {

    private final String uriSeatReservationService;

    public SeatReservationsWebAdapter(String uriSeatReservationService) {
        this.uriSeatReservationService = uriSeatReservationService;
    }

    @Override
    public ReservedSeatsDto getReservedSeats(String showId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity(uriSeatReservationService + "/api/data_for_reservation_seats/" + showId, String.class);
        ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        try {
            return mapper.readValue(response.getBody(), ReservedSeatsDto.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
