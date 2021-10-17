package com.baasie.SeatsSuggestionsDomain;

import java.util.stream.IntStream;

public class ShowId {

    private final String _id;

    public ShowId(String id)
    {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException(String.format("Parameter 'id' cannot be null"));
        }

        if (id.chars().filter(c -> Character.isDigit(c)).count() != id.length())
        {
            throw new IllegalArgumentException(String.format("Parameter 'id' should be a number"));
        }
        _id  = id;
    }

    public String ID() { return _id; }

    @Override
    public String toString()
    {
        return _id;
    }
}
