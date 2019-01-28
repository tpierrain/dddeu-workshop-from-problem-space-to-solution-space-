package com.baasie.SeatsSuggestions;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SeatTest {


    @Test
    public void Be_a_Value_Type() {
        Seat firstInstance = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        Seat secondInstance = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);

        // Two different instances with same values should be equals
        assertThat(secondInstance).isEqualTo(firstInstance);

        // Should not mutate existing instance
        secondInstance.allocate();
        assertThat(secondInstance).isEqualTo(firstInstance);
    }
}