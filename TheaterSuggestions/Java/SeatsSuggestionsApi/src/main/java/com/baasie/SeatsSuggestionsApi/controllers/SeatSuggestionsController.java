package com.baasie.SeatsSuggestionsApi.controllers;

import com.baasie.SeatsSuggestionsDomain.PartyRequested;
import com.baasie.SeatsSuggestionsDomain.ShowId;
import com.baasie.SeatsSuggestionsDomain.SuggestionsMade;
import com.baasie.SeatsSuggestionsDomain.port.IProvideAuditoriumSeating;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/SeatsSuggestions")
public class SeatSuggestionsController {

    private IProvideAuditoriumSeating iProvideAuditoriumSeating;

    public SeatSuggestionsController(IProvideAuditoriumSeating iProvideAuditoriumSeating) {
        this.iProvideAuditoriumSeating = iProvideAuditoriumSeating;
    }

    // GET api/SeatsSuggestions?showId=5&party=3
    @GetMapping(produces = "application/json")
    public ResponseEntity<SuggestionsMade> makeSuggestions(@RequestParam String showId, @RequestParam int party) {
        SuggestionsMade suggestionsMade = iProvideAuditoriumSeating.makeSuggestions(new ShowId(showId),new PartyRequested(party));
        return ResponseEntity.ok(suggestionsMade);
    }
}
