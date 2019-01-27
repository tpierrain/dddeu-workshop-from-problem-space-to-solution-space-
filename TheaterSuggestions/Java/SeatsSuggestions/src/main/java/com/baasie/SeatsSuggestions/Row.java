package com.baasie.SeatsSuggestions;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private String name;
    private List<Seat> seats = new ArrayList<>();

    public List<Seat> seats() {
        return seats;
    }

    public SeatAllocation findAllocation(int partyRequested, PricingCategory pricingCategory)
    {
        for(Seat seat: seats) {
            if(seat.isAvailable() && seat.matchCategory(pricingCategory)) {
                SeatAllocation seatAllocation = new SeatAllocation(partyRequested, pricingCategory);
                seatAllocation.addSeat(seat);

                if (seatAllocation.matchExpectation())
                {
                    return seatAllocation;
                }
            }
        }

        return new AllocationNotAvailable(partyRequested, pricingCategory);
    }
}
