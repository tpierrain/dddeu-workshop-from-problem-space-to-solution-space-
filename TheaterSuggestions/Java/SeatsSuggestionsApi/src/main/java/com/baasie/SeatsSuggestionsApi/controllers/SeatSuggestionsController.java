package com.baasie.SeatsSuggestionsApi.controllers;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.SeatsSuggestions.AuditoriumSeatingAdapter;
import com.baasie.SeatsSuggestions.SeatAllocator;
import com.baasie.SeatsSuggestions.SuggestionsMade;
import com.baasie.ExternalDependencies.IProvideAuditoriumLayouts;
import com.baasie.ExternalDependencies.IProvideCurrentReservations;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/SeatsSuggestions")
public class SeatSuggestionsController {


    public SeatSuggestionsController() {
    }

    // GET api/SeatsSuggestions?showId=5&party=3
    @GetMapping(produces = "application/json")
    public SuggestionsMade get(@RequestParam String showId, @RequestParam int party) throws IOException {

        SeatAllocator seatAllocator = new SeatAllocator(new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider()));
        return seatAllocator.makeSuggestions(showId, party);
    }
}
