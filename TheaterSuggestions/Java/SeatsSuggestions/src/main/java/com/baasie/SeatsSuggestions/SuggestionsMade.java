package com.baasie.SeatsSuggestions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SuggestionsMade {
    private String showId;
    private int partyRequested;

    public Map<PricingCategory, List<SuggestionMade>> forCategory = new HashMap<>();

    public SuggestionsMade(String showId, int partyRequested) {
        this.showId = showId;
        this.partyRequested = partyRequested;

        instantiateAnEmptyListForEveryPricingCategory();
    }

    public Iterable<String> seatNames(PricingCategory pricingCategory) {
        List<SuggestionMade> suggestionsMade = forCategory.get(pricingCategory);
        return suggestionsMade.stream().map(s -> String.join("-", s.seatNames())).collect(Collectors.toList());
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
