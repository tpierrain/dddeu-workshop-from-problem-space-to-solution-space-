package com.baasie.SeatsSuggestionsDomain;

import java.util.HashMap;
import java.util.Map;

public enum PricingCategory {
    First(1),
    Second(2),
    Third(3),
    Mixed(4);

    private static final Map map = new HashMap();

    static {
        for (var pageType : PricingCategory.values()) {
            map.put(pageType.value, pageType);
        }
    }

    private final int value;

    PricingCategory(int value) {
        this.value = value;
    }

    public static PricingCategory valueOf(int pageType) {
        return (PricingCategory) map.get(pageType);
    }

    public int getValue() {
        return value;
    }


}