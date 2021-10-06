package com.baasie.SeatsSuggestionsDomain;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.baasie.SeatsSuggestionsDomain.SeatCollectionExtensions.*;

@EqualsAndHashCode
public class Seat {

    private String rowName;
    private int number;
    private PricingCategory pricingCategory;
    private SeatAvailability seatAvailability;
    private int distanceFromCentroid;

    public Seat(String rowName, int number, PricingCategory pricingCategory, SeatAvailability seatAvailability) {
        this(rowName, number, pricingCategory, seatAvailability, 0);
    }

    public Seat(String rowName, int number, PricingCategory pricingCategory, SeatAvailability seatAvailability, int distanceFromCentroid) {
        this.rowName = rowName;
        this.number = number;
        this.pricingCategory = pricingCategory;
        this.seatAvailability = seatAvailability;
        this.distanceFromCentroid = distanceFromCentroid;
    }

    boolean isAvailable() {
        return seatAvailability == SeatAvailability.Available;
    }

    boolean matchCategory(PricingCategory pricingCategory) {
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

    public int distanceFromCentroid() {
        return this.distanceFromCentroid;
    }
}