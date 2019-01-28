package com.baasie.SeatsSuggestionsApi.controllers;


import com.baasie.ExternalDependencies.IProvideAuditoriumLayouts;
import com.baasie.ExternalDependencies.IProvideCurrentReservations;
import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumDto;
import com.baasie.SeatsSuggestionsDomain.AuditoriumSeatingAdapter;
import com.baasie.SeatsSuggestionsDomain.Seat;
import com.baasie.SeatsSuggestionsDomain.SeatAllocator;
import com.baasie.SeatsSuggestionsDomain.SuggestionsMade;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping("api/SeatsSuggestions")
public class SeatSuggestionsController {

    private IProvideAuditoriumLayouts auditoriumSeatingRepository;
    private IProvideCurrentReservations seatReservationsProvider;

    public SeatSuggestionsController(IProvideAuditoriumLayouts auditoriumSeatingRepository, IProvideCurrentReservations seatReservationsProvider) {
        this.auditoriumSeatingRepository = auditoriumSeatingRepository;
        this.seatReservationsProvider = seatReservationsProvider;
    }

    // GET api/SeatsSuggestions?showId=5&party=3
    @GetMapping(produces = "application/json")
    public SuggestionsMade get(@RequestParam String showId, @RequestParam int party) {

        SeatAllocator seatAllocator = new SeatAllocator(new AuditoriumSeatingAdapter(auditoriumSeatingRepository, seatReservationsProvider));
        SuggestionsMade suggestions = seatAllocator.makeSuggestions(showId, party);

        return suggestions;
    }
}
