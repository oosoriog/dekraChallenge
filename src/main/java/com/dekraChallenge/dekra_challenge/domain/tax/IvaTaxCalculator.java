package com.dekraChallenge.dekra_challenge.domain.tax;

import java.math.BigDecimal;

public class IvaTaxCalculator extends RateBasedTaxCalculator {

    private static final BigDecimal RATE_FRACTION = new BigDecimal("0.21");
    private static final BigDecimal PERCENTAGE = new BigDecimal("21");

    @Override
    public TaxType getType() {
        return TaxType.IVA;
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
