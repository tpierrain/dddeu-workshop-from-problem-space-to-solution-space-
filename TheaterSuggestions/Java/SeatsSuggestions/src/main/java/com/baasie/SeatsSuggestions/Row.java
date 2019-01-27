package com.baasie.SeatsSuggestions;

import java.util.List;

public class Row {
    private String name;
    private List<Seat> seats;

    public Row(String name, List<Seat> seats) {
        this.name = name;
        this.seats = seats;
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public SeatAllocation findAllocation(int partyRequested, PricingCategory pricingCategory) {
        for (Seat seat : seats) {
            if (seat.isAvailable() && seat.matchCategory(pricingCategory)) {
                SeatAllocation seatAllocation = new SeatAllocation(partyRequested, pricingCategory);
                seatAllocation.addSeat(seat);

                if (seatAllocation.matchExpectation()) {
                    return seatAllocation;
                }
            }
        }

        return new AllocationNotAvailable(partyRequested, pricingCategory);
    }

    public List<Seat> seats() {
        return seats;
    }

}
