package com.baasie.SeatsSuggestionsApi.controllers;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.SeatsSuggestionsApi.SeatSuggestionApiApplication;
import com.baasie.SeatsSuggestionsDomain.SeatsAllocator;
import com.baasie.SeatsSuggestionsDomain.SuggestionsMade;
import com.baasie.SeatsSuggestionsInfra.AuditoriumSeatingAdapter;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = SeatSuggestionApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import(TestConfig.class)
public class SeatSuggestionsControllerTest {

    @LocalServerPort
    private int randomServerPort;

    @Before
    public void before() {
        RestAssured.baseURI = "http://localhost:" + randomServerPort + "/api/SeatsSuggestions/";
    }

    @Test
    public void Return_SeatsNotAvailable_when_Auditorium_has_all_its_seats_already_reserved() throws IOException {
        final String showId = "5";
        final int party = 1;

        new SeatsAllocator(new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider()));

        SuggestionsMade suggestionsMade =
                given()
                        .queryParam("showId", showId).queryParam("party", party)
                        .when().get()
                        .then().statusCode(200).extract().as(SuggestionsMade.class);


        assertThat(suggestionsMade).isInstanceOf(SuggestionsMade.class);
    }
}
