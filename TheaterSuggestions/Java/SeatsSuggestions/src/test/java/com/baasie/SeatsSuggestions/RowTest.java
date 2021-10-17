package com.baasie.SeatsSuggestions;

import org.junit.Test;

import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;

public class RowTest {

    @Test
    public void be_a_Value_Type() {
        Seat a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        Seat a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);

        // Two different instances with same values should be equals
        Row rowFirstInstance = new Row("A", Arrays.asList(a1, a2));
        Row rowSecondInstance = new Row("A", Arrays.asList(a1, a2));
        assertThat(rowSecondInstance).isEqualTo(rowFirstInstance);
    }

}