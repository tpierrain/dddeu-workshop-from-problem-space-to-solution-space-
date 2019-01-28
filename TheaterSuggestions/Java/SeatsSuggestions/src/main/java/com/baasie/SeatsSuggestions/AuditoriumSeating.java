package com.baasie.SeatsSuggestions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode
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

    public AuditoriumSeating allocate(SeatingOptionSuggested seatingOptionSuggested) {
        // Update the seat references in the Auditorium
        return allocateSeats(seatingOptionSuggested.seats());
    }

    private AuditoriumSeating allocateSeats(Iterable<Seat> updatedSeats) {
        Map<String, Row> newVersionOfRows = Maps.newHashMap(rows);

        updatedSeats.forEach(updatedSeat -> {
            Row formerRow = newVersionOfRows.get(updatedSeat.rowName());
            Row newVersionOfRow = formerRow.allocate(updatedSeat);
            newVersionOfRows.put(updatedSeat.rowName(), newVersionOfRow);
        });

        return new AuditoriumSeating(newVersionOfRows);
    }

    public ImmutableMap<String, Row> rows() {
        return rows;
    }
}
