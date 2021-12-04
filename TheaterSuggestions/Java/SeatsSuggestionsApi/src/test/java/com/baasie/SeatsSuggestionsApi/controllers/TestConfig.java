package com.baasie.SeatsSuggestionsApi.controllers;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.SeatsSuggestionsDomain.SeatsAllocator;
import com.baasie.SeatsSuggestionsDomain.port.IProvideAuditoriumSeating;
import com.baasie.SeatsSuggestionsInfra.AuditoriumSeatingAdapter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@TestConfiguration
public class TestConfig {

    @Bean
    public IProvideAuditoriumSeating iProvideAuditoriumSeating() throws IOException {

        return new SeatsAllocator(new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider()));

    }
}
