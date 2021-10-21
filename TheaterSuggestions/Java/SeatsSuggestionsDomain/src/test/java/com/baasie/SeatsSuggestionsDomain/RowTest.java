package com.baasie.SeatsSuggestionsDomain;

import org.junit.Test;

import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;

public class RowTest {

    @Test
    public void be_a_Value_Type() {
        var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        var a2 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);

        // Two different instances with same values should be equals
       var rowFirstInstance = new Row("A", Arrays.asList(a1, a2));
       var rowSecondInstance = new Row("A", Arrays.asList(a1, a2));
        assertThat(rowSecondInstance).isEqualTo(rowFirstInstance);

        // Should not mutate existing instance
        var a3 = new Seat("A", 2, PricingCategory.Second, SeatAvailability.Available);
        var newRowWithNewSeatAdded = rowSecondInstance.addSeat(a3);
        assertThat(newRowWithNewSeatAdded).isNotEqualTo(rowFirstInstance);
    }

}