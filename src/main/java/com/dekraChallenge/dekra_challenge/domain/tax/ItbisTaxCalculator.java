package com.dekraChallenge.dekra_challenge.domain.tax;

import java.math.BigDecimal;

public class ItbisTaxCalculator extends RateBasedTaxCalculator {

    private static final BigDecimal RATE_FRACTION = new BigDecimal("0.18");
    private static final BigDecimal PERCENTAGE = new BigDecimal("18");

    @Override
    public TaxType getType() {
        return TaxType.ITBIS;
    }

    @Override
    public BigDecimal getPercentage() {
        return PERCENTAGE;
    }

    @Override
    protected BigDecimal getRateFraction() {
        return RATE_FRACTION;
    }
}
