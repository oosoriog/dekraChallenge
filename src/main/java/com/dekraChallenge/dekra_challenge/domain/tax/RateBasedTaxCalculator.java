package com.dekraChallenge.dekra_challenge.domain.tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public abstract class RateBasedTaxCalculator implements TaxCalculator {

    static final int MONETARY_SCALE = 2;

    static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    @Override
    public CalculatedTax calculate(BigDecimal price) {
        Objects.requireNonNull(price, "price must not be null");
        BigDecimal amount = price.multiply(getRateFraction())
                .setScale(MONETARY_SCALE, ROUNDING_MODE);
        BigDecimal priceWithTax = price.add(amount)
                .setScale(MONETARY_SCALE, ROUNDING_MODE);
        return new CalculatedTax(getType(), getPercentage(), amount, priceWithTax);
    }

    public abstract BigDecimal getPercentage();

    protected abstract BigDecimal getRateFraction();
}
