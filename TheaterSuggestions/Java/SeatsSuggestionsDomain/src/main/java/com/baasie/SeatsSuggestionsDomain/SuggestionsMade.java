package com.baasie.SeatsSuggestionsDomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SuggestionsMade {
    public Map<PricingCategory, List<SuggestionMade>> forCategory = new HashMap<>();
    private ShowId showId;
    private PartyRequested partyRequested;

    public SuggestionsMade(ShowId showId, PartyRequested partyRequested) {
        this.showId = showId;
        this.partyRequested = partyRequested;

        instantiateAnEmptyListForEveryPricingCategory();
    }

    //.sorted(Comparator.comparing(Seat::number))
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