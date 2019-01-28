package com.baasie.SeatsSuggestionsDomain;

import lombok.EqualsAndHashCode;

import static com.baasie.SeatsSuggestionsDomain.SeatCollectionExtensions.*;

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

    public boolean isAdjacentWith(int number) {
        return this.number + 1 == number || this.number - 1 == number;
    }

    public int computeDistanceFromRowCentroid(int rowSize) {
        int seatLocation = number;

        if (isOdd(rowSize)) {
            return computeDistanceFromCentroid(seatLocation, rowSize);
        }

        if (isCentroid(seatLocation, rowSize)) {
            return 0;
        }

        if (seatLocation < centroidIndex(rowSize)) {
            return computeDistanceFromCentroid(seatLocation, rowSize);
        }

        return computeDistanceFromCentroid(seatLocation, rowSize) - 1;
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
