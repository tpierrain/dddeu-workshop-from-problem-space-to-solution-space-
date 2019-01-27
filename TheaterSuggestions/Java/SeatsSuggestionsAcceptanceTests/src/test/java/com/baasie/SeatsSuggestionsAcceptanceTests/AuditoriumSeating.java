package com.baasie.SeatsSuggestionsAcceptanceTests;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class AuditoriumSeating {

    private ImmutableMap<String, Row> rows;

    public AuditoriumSeating(Map<String, Row> rows) {
        this.rows = ImmutableMap.copyOf(rows);
    }

    public ImmutableMap<String, Row> rows() {
        return rows;
    }
}
