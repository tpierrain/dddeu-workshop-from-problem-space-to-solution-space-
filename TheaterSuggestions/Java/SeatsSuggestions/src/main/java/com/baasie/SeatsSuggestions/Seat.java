package com.baasie.SeatsSuggestions;

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

    public void allocate() {
        if (seatAvailability == SeatAvailability.Available) {
            seatAvailability = SeatAvailability.Allocated;
        }
    }

    @Override
    public String toString() {
        return rowName + number;
    }
}
