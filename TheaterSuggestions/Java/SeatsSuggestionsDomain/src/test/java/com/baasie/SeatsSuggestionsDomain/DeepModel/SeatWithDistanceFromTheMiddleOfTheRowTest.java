package com.baasie.SeatsSuggestionsDomain.DeepModel;

import com.baasie.SeatsSuggestionsDomain.PricingCategory;
import com.baasie.SeatsSuggestionsDomain.Seat;
import com.baasie.SeatsSuggestionsDomain.SeatAvailability;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class SeatWithDistanceFromTheMiddleOfTheRowTest {
    @Test
    public void be_a_Value_Type() {
        // Two different instances with same values should be equals
        var a1 = new Seat("A", 1, PricingCategory.Second, SeatAvailability.Available);
        var firstInstance = new SeatWithDistance(a1, 5);
        var secondInstance = new SeatWithDistance(a1, 5);
        assertThat(secondInstance).isEqualTo(firstInstance);
    }
}
