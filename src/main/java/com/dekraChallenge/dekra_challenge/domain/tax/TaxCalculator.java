package com.dekraChallenge.dekra_challenge.domain.tax;

import java.math.BigDecimal;

public interface TaxCalculator {

    TaxType getType();

    CalculatedTax calculate(BigDecimal price);
}
