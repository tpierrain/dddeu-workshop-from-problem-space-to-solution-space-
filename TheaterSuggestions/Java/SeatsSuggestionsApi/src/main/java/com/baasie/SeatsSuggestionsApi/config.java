package com.baasie.SeatsSuggestionsApi;

import com.baasie.ExternalDependencies.IProvideAuditoriumLayouts;
import com.baasie.ExternalDependencies.IProvideCurrentReservations;
import com.baasie.SeatsSuggestionsDomain.SeatAllocator;
import com.baasie.SeatsSuggestionsDomain.port.IProvideAuditoriumSeating;
import com.baasie.SeatsSuggestionsInfra.AuditoriumSeatingAdapter;
import com.baasie.SeatsSuggestionsInfra.AuditoriumWebRepository;
import com.baasie.SeatsSuggestionsInfra.SeatReservationsWebAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class config {

//    @Bean
//    public IProvideAuditoriumLayouts iProvideAuditoriumLayouts() {
//        return new AuditoriumWebRepository("http://localhost:50950/");
//    }
//
//    @Bean
//    public IProvideCurrentReservations iProvideCurrentReservations() {
//        return new SeatReservationsWebAdapter("http://localhost:50951/");
//    }
//
//    @Bean
//    public IProvideAuditoriumSeating iProvideAuditoriumSeating(IProvideAuditoriumLayouts iProvideAuditoriumLayouts, IProvideCurrentReservations iProvideCurrentReservations) throws IOException {
//
//        return new SeatAllocator(new AuditoriumSeatingAdapter(iProvideAuditoriumLayouts, iProvideCurrentReservations));
//
//    }



}
