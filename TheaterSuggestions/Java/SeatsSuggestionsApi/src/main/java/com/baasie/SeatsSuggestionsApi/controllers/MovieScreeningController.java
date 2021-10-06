package com.baasie.SeatsSuggestionsApi.controllers;

import com.baasie.SeatsSuggestionsInfra.AuditoriumSeatingAdapter;
import com.baasie.SeatsSuggestionsDomain.SeatAllocator;
import com.baasie.SeatsSuggestionsDomain.SuggestionsMade;
import com.baasie.ExternalDependencies.IProvideAuditoriumLayouts;
import com.baasie.ExternalDependencies.IProvideCurrentReservations;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/data_for_auditoriumSeating")
public class MovieScreeningController {

    private IProvideAuditoriumLayouts auditoriumSeatingRepository;
    private IProvideCurrentReservations seatReservationsProvider;

    public MovieScreeningController() {
    }

    // GET api/SeatsSuggestions?showId=5&party=3
    @GetMapping(produces = "application/json")
    public SuggestionsMade get(@RequestParam String showId, @RequestParam int party) {

//        SeatAllocator seatAllocator = new SeatAllocator(new AuditoriumSeatingAdapter(auditoriumSeatingRepository, seatReservationsProvider));
//        return seatAllocator.makeSuggestions(showId, party);
        return null;
    }
}
