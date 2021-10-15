package com.baasie.SeatsSuggestionsDomain;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class Seat {

    private String rowName;
    private int number;
    private PricingCategory pricingCategory;
    private SeatAvailability seatAvailability;
    private int distanceFromCentroid;

    public Seat(String rowName, int number, PricingCategory pricingCategory, SeatAvailability seatAvailability) {
        this.rowName = rowName;
        this.number = number;
        this.pricingCategory = pricingCategory;
        this.seatAvailability = seatAvailability;
        this.distanceFromCentroid = distanceFromCentroid;
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

    boolean isAdjacentWith(List<Seat> seats) {

        List<Seat> orderedSeats = seats.stream()
                .sorted(Comparator.comparing(Seat::number))
                .collect(Collectors.toCollection(ArrayList::new));

        for (Seat seat : orderedSeats) {
            if (number + 1 == seat.number || number - 1 == seat.number)
                return true;
        }
        return false;
    }

    public SeatAvailability seatAvailability() {
        return seatAvailability;
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

    public int distanceFromCentroid() {
        return this.distanceFromCentroid;
    }
}