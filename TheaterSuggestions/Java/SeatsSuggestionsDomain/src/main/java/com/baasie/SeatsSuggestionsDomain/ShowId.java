package com.baasie.SeatsSuggestionsDomain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ShowId {

    private final String _id;

    public ShowId(String id)
    {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'id' cannot be null");
        }

        if (id.chars().filter(Character::isDigit).count() != id.length())
        {
            throw new IllegalArgumentException("Parameter 'id' should be a number");
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
