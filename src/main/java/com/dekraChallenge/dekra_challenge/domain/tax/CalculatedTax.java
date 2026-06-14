package com.dekraChallenge.dekra_challenge.domain.tax;

import java.math.BigDecimal;

public record CalculatedTax(
        TaxType type,
        BigDecimal percentage,
        BigDecimal amount,
        BigDecimal priceWithTax) {
}
