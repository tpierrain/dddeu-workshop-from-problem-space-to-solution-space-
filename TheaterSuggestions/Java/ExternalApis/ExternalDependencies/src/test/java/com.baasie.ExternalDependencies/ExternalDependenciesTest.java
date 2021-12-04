package com.baasie.ExternalDependencies;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumDto;
import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.auditoriumlayoutrepository.SeatDto;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.ExternalDependencies.reservationsprovider.ReservedSeatsDto;
import com.baasie.SeatsSuggestionsDomain.ShowId;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class ExternalDependenciesTest {

    @Test
    public void should_allow_us_to_retrieve_reserved_seats_for_a_given_ShowId() throws IOException {
        ReservationsProvider seatsRepository = new ReservationsProvider();
        ReservedSeatsDto reservedSeatsDto = seatsRepository.getReservedSeats(new ShowId("1"));

        //AssertJ
        org.assertj.core.api.Assertions.assertThat(reservedSeatsDto.reservedSeats()).hasSize(19);

        //Hamcrest
        org.hamcrest.MatcherAssert.assertThat(reservedSeatsDto.reservedSeats(), IsCollectionWithSize.hasSize(19));

        //Google Truth
        com.google.common.truth.Truth.assertThat(reservedSeatsDto.reservedSeats()).hasSize(19);
    }

    @Test
    public void should_allow_us_to_retrieve_AuditoriumLayout_for_a_given_ShowId() throws IOException {

        AuditoriumLayoutRepository eventRepository = new AuditoriumLayoutRepository();
        AuditoriumDto theaterDto = eventRepository.GetAuditoriumLayoutFor(new ShowId("2"));

        //Google Truth
        com.google.common.truth.Truth.assertThat(theaterDto.rows()).hasSize(6);
        com.google.common.truth.Truth.assertThat(theaterDto.corridors()).hasSize(2);
        SeatDto firstSeatOfFirstRow = theaterDto.rows().get("A").get(0);
        System.out.println(firstSeatOfFirstRow);
        com.google.common.truth.Truth.assertThat(firstSeatOfFirstRow.category()).isEqualTo(2);

    }

}