package com.baasie.SeatsSuggestionsDomain;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class PartyRequested {

    public static final int MAX_PARTY_SIZE = 20;
    private final int _partySize;

    public PartyRequested(int partySize)
    {
        if (partySize <= 0 || partySize > MAX_PARTY_SIZE)
            throw new IllegalArgumentException(String.format("Parameter 'partySize' should be greater than zero and less than 20"));

        _partySize = partySize;
    }

    public int partySize() { return _partySize; }
}
