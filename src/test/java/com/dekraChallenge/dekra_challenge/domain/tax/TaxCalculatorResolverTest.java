package com.dekraChallenge.dekra_challenge.domain.tax;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaxCalculatorResolverTest {

    private final TaxCalculatorResolver resolver = new TaxCalculatorResolver(
            List.of(new IvaTaxCalculator(), new ItbisTaxCalculator()));

    @Test
    void should_resolve_IVA_calculator() {
        TaxCalculator resolved = resolver.resolve(TaxType.IVA);
        assertThat(resolved).isInstanceOf(IvaTaxCalculator.class);
        assertThat(resolved.getType()).isEqualTo(TaxType.IVA);
    }

    @Test
    void should_resolve_ITBIS_calculator() {
        TaxCalculator resolved = resolver.resolve(TaxType.ITBIS);
        assertThat(resolved).isInstanceOf(ItbisTaxCalculator.class);
        assertThat(resolved.getType()).isEqualTo(TaxType.ITBIS);
    }

    @Test
    void should_build_calculated_tax_for_IVA() {
        CalculatedTax result = resolver.calculate(TaxType.IVA, new BigDecimal("100.00"));

        assertThat(result.type()).isEqualTo(TaxType.IVA);
        assertThat(result.percentage()).isEqualByComparingTo("21");
        assertThat(result.amount()).isEqualByComparingTo("21.00");
        assertThat(result.priceWithTax()).isEqualByComparingTo("121.00");
    }

    @Test
    void should_build_calculated_tax_for_ITBIS() {
        CalculatedTax result = resolver.calculate(TaxType.ITBIS, new BigDecimal("100.00"));

        assertThat(result.type()).isEqualTo(TaxType.ITBIS);
        assertThat(result.percentage()).isEqualByComparingTo("18");
        assertThat(result.amount()).isEqualByComparingTo("18.00");
        assertThat(result.priceWithTax()).isEqualByComparingTo("118.00");
    }

    @Test
    void should_throw_when_taxtype_is_null() {
        assertThatThrownBy(() -> resolver.resolve(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("type");
    }

    @Test
    void should_throw_when_taxtype_not_configured() {
        TaxCalculatorResolver onlyIva =
                new TaxCalculatorResolver(List.of(new IvaTaxCalculator()));

        assertThatThrownBy(() -> onlyIva.resolve(TaxType.ITBIS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ITBIS");
    }

    @Test
    void should_throw_when_duplicate_calculator_for_same_type() {
        assertThatThrownBy(() -> new TaxCalculatorResolver(
                List.of(new IvaTaxCalculator(), new IvaTaxCalculator())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate");
    }
}
