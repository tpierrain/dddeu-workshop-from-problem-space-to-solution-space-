package com.baasie.SeatsSuggestions;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class AuditoriumSeating {

    private ImmutableMap<String, Row> rows;

    public AuditoriumSeating(Map<String, Row> rows) {
        this.rows = ImmutableMap.copyOf(rows);
    }

    public SeatingOptionSuggested suggestSeatingOptionFor(int partyRequested, PricingCategory pricingCategory) {
        for (Row row : rows.values()) {
            SeatingOptionSuggested seatingOptionSuggested = row.suggestSeatingOption(partyRequested, pricingCategory);

            if (seatingOptionSuggested.matchExpectation()) {
                // Cool, we mark the seat as Allocated (that we turns into a SuggestionMode)
                return seatingOptionSuggested;
            }
        }

        return new SeatingOptionNotAvailable(partyRequested, pricingCategory);
    }

    public ImmutableMap<String, Row> rows() {
        return this.rows;
    }
}
