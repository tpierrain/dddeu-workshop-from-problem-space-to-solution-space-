package com.baasie.SeatsSuggestions;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class AuditoriumSeating {

    private ImmutableMap<String, Row> rows;

    public AuditoriumSeating(Map<String, Row> rows) {
        this.rows = ImmutableMap.copyOf(rows);
    }

    public SeatAllocation makeAllocationFor(int partyRequested, PricingCategory pricingCategory) {
        for (Row row : rows.values()) {
            SeatAllocation seatAllocation = row.findAllocation(partyRequested, pricingCategory);

            if (seatAllocation.matchExpectation()) {
                // Cool, we mark the seat as Suggested (that we turns into a SuggestionMode)
                return seatAllocation;
            }
        }

        return new AllocationNotAvailable(partyRequested, pricingCategory);
    }
}
