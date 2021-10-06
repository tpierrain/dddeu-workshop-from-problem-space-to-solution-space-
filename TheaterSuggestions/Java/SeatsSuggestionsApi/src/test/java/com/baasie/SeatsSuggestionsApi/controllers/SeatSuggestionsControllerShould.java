package com.baasie.SeatsSuggestionsApi.controllers;

import com.baasie.SeatsSuggestionsApi.SeatSuggestionApiApplication;
import com.baasie.SeatsSuggestionsApi.controllers.helpers.Given;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static io.restassured.RestAssured.given;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = SeatSuggestionApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestDatabase
public class SeatSuggestionsControllerShould {

    @LocalServerPort
    private int randomServerPort;


    @Before
    public void before() {
        RestAssured.baseURI = "http://localhost:" + randomServerPort + "/api/SeatsSuggestions/";
    }

//    @Test
//    public void reserve_one_seat_when_available() throws IOException {
//        Given.The.fordTheaterTicketBooth();
//
//        SeatsAllocated seatsAllocated =
//                given()
//                        .pathParam("showId", Given.The.ford_theater_id).pathParam("partyRequested", 1)
//                        .when().post("/{showId}/allocateseats/{partyRequested}")
//                        .then().statusCode(200).extract().as(SeatsAllocated.class);
//
//        assertThat(seatsAllocated.reservedSeats()).hasSize(1);
//        assertThat(seatsAllocated.reservedSeats().get(0).toString()).isEqualTo("A3");
//
//    }
}
