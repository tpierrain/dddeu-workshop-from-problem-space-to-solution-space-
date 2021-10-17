package com.baasie.SeatsSuggestionsInfra;

import com.baasie.ExternalDependencies.auditoriumlayoutrepository.AuditoriumLayoutRepository;
import com.baasie.ExternalDependencies.reservationsprovider.ReservationsProvider;
import com.baasie.SeatsSuggestionsDomain.AuditoriumSeating;
import com.baasie.SeatsSuggestionsDomain.ShowId;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.common.truth.Truth.assertThat;

public class AuditoriumSeatingTest {

    @Test
    public void be_a_Value_Type() throws IOException, URISyntaxException {
        AuditoriumSeatingAdapter auditoriumLayoutAdapter =
                new AuditoriumSeatingAdapter(new AuditoriumLayoutRepository(), new ReservationsProvider());

        ShowId showIdWithoutReservationYet = new ShowId("18");
        AuditoriumSeating auditoriumSeatingFirstInstance =
                auditoriumLayoutAdapter.getAuditoriumSeating(showIdWithoutReservationYet);
        AuditoriumSeating auditoriumSeatingSecondInstance =
                auditoriumLayoutAdapter.getAuditoriumSeating(showIdWithoutReservationYet);

        // Two different instances with same values should be equals
        assertThat(auditoriumSeatingSecondInstance).isEqualTo(auditoriumSeatingFirstInstance);

        // Should not mutate existing instance
        auditoriumSeatingSecondInstance.rows().values().iterator().next().seats().iterator().next().allocate();
        assertThat(auditoriumSeatingSecondInstance).isEqualTo(auditoriumSeatingFirstInstance);
    }

}