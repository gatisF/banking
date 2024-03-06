package io.mintos.banking.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum Currency {
    EUR,
    USD,
    GBP;

    public static boolean isValid(String currency) {
        return Arrays.stream(Currency.values())
                .map(Currency::name)
                .collect(Collectors.toSet())
                .contains(currency);
    }
}
