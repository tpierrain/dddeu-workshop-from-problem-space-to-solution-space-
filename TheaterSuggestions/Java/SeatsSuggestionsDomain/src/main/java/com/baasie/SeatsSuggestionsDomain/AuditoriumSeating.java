package com.baasie.SeatsSuggestionsDomain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode
public class AuditoriumSeating {

    private ImmutableMap<String, Row> rows;

    public AuditoriumSeating(Map<String, Row> rows) {
        this.rows = ImmutableMap.copyOf(rows);
    }

    public SeatingOptionSuggested suggestSeatingOptionFor(SuggestionRequest suggestionRequest) {
        for (Row row : rows.values()) {
            SeatingOptionSuggested seatingOptionSuggested = row.suggestSeatingOption(suggestionRequest);

            if (seatingOptionSuggested.matchExpectation()) {
                // Cool, we mark the seat as Allocated (that we turns into a SuggestionMode)
                return seatingOptionSuggested;
            }
        }

        return new SeatingOptionNotAvailable(suggestionRequest);
    }

    public AuditoriumSeating allocate(SeatingOptionSuggested seatingOptionSuggested) {
        // Update the seat references in the Auditorium
        return allocateSeats(seatingOptionSuggested.seats());
    }

    private AuditoriumSeating allocateSeats(List<Seat> updatedSeats) {
        Map<String, Row> newVersionOfRows = Maps.newHashMap(rows);

        for (Seat updatedSeat : updatedSeats) {
            Row formerRow = newVersionOfRows.get(updatedSeat.rowName());
            Row newVersionOfRow = formerRow.allocate(updatedSeat);
            newVersionOfRows.put(updatedSeat.rowName(), newVersionOfRow);
        }

        return new AuditoriumSeating(newVersionOfRows);
    }

    public ImmutableMap<String, Row> rows() {
        return rows;
    }
}
