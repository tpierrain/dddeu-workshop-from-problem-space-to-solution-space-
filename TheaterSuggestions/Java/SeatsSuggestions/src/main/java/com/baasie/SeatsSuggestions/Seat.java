package com.baasie.SeatsSuggestions;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Seat {

    private String rowName;
    private int number;
    private PricingCategory pricingCategory;
    private SeatAvailability seatAvailability;

    public Seat(String rowName, int number, PricingCategory pricingCategory, SeatAvailability seatAvailability) {
        this.rowName = rowName;
        this.number = number;
        this.pricingCategory = pricingCategory;
        this.seatAvailability = seatAvailability;
    }

    public boolean isAvailable() {
        return seatAvailability == SeatAvailability.Available;
    }

    public boolean matchCategory(PricingCategory pricingCategory) {
        if (pricingCategory == PricingCategory.Mixed) {
            return true;
        }

        return this.pricingCategory == pricingCategory;
    }

    public Seat allocate() {
        if (seatAvailability == SeatAvailability.Available) {
            return new Seat(rowName, number, pricingCategory, SeatAvailability.Allocated);
        }
        return this;
    }

    public boolean sameSeatLocation(Seat seat) {
        return rowName.equals(seat.rowName) && number == seat.number;
    }

    public String rowName() {
        return rowName;
    }

    public int number() {
        return number;
    }

    public PricingCategory pricingCategory() {
        return pricingCategory;
    }

    @Override
    public String toString() {
        return rowName + number;
    }
}
