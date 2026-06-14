package com.dekraChallenge.dekra_challenge.domain.tax;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaxCalculatorResolver {

    private final Map<TaxType, TaxCalculator> calculatorsByType;

    public TaxCalculatorResolver(List<? extends TaxCalculator> calculators) {
        Objects.requireNonNull(calculators, "calculators must not be null");
        Map<TaxType, TaxCalculator> map = new EnumMap<>(TaxType.class);
        for (TaxCalculator calculator : calculators) {
            Objects.requireNonNull(calculator, "calculator must not be null");
            TaxType type = calculator.getType();
            if (map.putIfAbsent(type, calculator) != null) {
                throw new IllegalArgumentException("Duplicate calculator for tax type: " + type);
            }
        }
        this.calculatorsByType = map;
    }

    public TaxCalculator resolve(TaxType type) {
        Objects.requireNonNull(type, "type must not be null");
        TaxCalculator calculator = calculatorsByType.get(type);
        if (calculator == null) {
            throw new IllegalArgumentException("No tax calculator configured for tax type: " + type);
        }
        return calculator;
    }

    public CalculatedTax calculate(TaxType type, BigDecimal price) {
        Objects.requireNonNull(price, "price must not be null");
        return resolve(type).calculate(price);
    }
}
