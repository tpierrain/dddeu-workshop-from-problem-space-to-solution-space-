package com.baasie.SeatsSuggestionsDomain;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SeatTest {


    @Test
    public void Be_a_Value_Type() {
        var firstInstance = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        var secondInstance = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);

        // Two different instances with same values should be equals
        assertThat(secondInstance).isEqualTo(firstInstance);

        // Should not mutate existing instance
        secondInstance.allocate();
        assertThat(secondInstance).isEqualTo(firstInstance);
    }
}