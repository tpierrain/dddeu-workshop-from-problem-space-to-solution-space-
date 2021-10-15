package com.baasie.SeatsSuggestions;

import java.util.*;
import java.util.stream.Collectors;

public class SuggestionsMade {
    private final String showId;
    private final int partyRequested;

    public Map<PricingCategory, List<SuggestionMade>> forCategory = new HashMap<>();

    public SuggestionsMade(String showId, int partyRequested) {
        this.showId = showId;
        this.partyRequested = partyRequested;

        instantiateAnEmptyListForEveryPricingCategory();
    }

    public List<String> seatNames(PricingCategory pricingCategory) {
        return forCategory.get(pricingCategory).stream().map(s -> String.join("-", s.seatNames())).collect(Collectors.toList());
    }

    private void instantiateAnEmptyListForEveryPricingCategory() {
        for (PricingCategory pricingCategory : PricingCategory.values()) {
            forCategory.put(pricingCategory, new ArrayList<>());
        }
    }

    public void add(Iterable<SuggestionMade> suggestions) {
        suggestions.forEach(suggestionMade -> forCategory.get(suggestionMade.pricingCategory()).add(suggestionMade));
    }

    public boolean matchExpectations() {
        return forCategory.values().stream().flatMap(List::stream).anyMatch(SuggestionMade::MatchExpectation);
    }
}
