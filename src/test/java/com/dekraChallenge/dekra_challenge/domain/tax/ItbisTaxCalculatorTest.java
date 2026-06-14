package com.dekraChallenge.dekra_challenge.domain.tax;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ItbisTaxCalculatorTest {

    private final ItbisTaxCalculator calculator = new ItbisTaxCalculator();

    @Test
    void should_calculate_18_percent() {
        CalculatedTax result = calculator.calculate(new BigDecimal("100.00"));
        assertThat(result.amount()).isEqualByComparingTo("18.00");
    }

    @Test
    void should_return_complete_calculated_tax() {
        CalculatedTax result = calculator.calculate(new BigDecimal("100.00"));

        assertThat(result.type()).isEqualTo(TaxType.ITBIS);
        assertThat(result.percentage()).isEqualByComparingTo("18");
        assertThat(result.amount()).isEqualByComparingTo("18.00");
        assertThat(result.priceWithTax()).isEqualByComparingTo("118.00");
    }

    @Test
    void should_scale_result_to_two_decimals() {
        CalculatedTax result = calculator.calculate(new BigDecimal("100.00"));
        assertThat(result.amount().scale()).isEqualTo(2);
    }

    @Test
    void should_round_half_up_to_scale_2() {
        // 12.75 * 0.18 = 2.2950 -> HALF_UP -> 2.30
        CalculatedTax result = calculator.calculate(new BigDecimal("12.75"));
        assertThat(result.amount()).isEqualByComparingTo("2.30");
    }

    @Test
    void should_expose_percentage_18() {
        assertThat(calculator.getPercentage()).isEqualByComparingTo("18");
    }

    @Test
    void should_expose_type_ITBIS() {
        assertThat(calculator.getType()).isEqualTo(TaxType.ITBIS);
    }

    @Test
    void should_reject_null_price() {
        assertThatThrownBy(() -> calculator.calculate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("price");
    }
}
