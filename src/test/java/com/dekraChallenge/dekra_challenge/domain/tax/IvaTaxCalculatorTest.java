package com.dekraChallenge.dekra_challenge.domain.tax;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class IvaTaxCalculatorTest {

    private final IvaTaxCalculator calculator = new IvaTaxCalculator();

    @Test
    void should_calculate_21_percent() {
        CalculatedTax result = calculator.calculate(new BigDecimal("100.00"));
        assertThat(result.amount()).isEqualByComparingTo("21.00");
    }

    @Test
    void should_return_complete_calculated_tax() {
        CalculatedTax result = calculator.calculate(new BigDecimal("100.00"));

        assertThat(result.type()).isEqualTo(TaxType.IVA);
        assertThat(result.percentage()).isEqualByComparingTo("21");
        assertThat(result.amount()).isEqualByComparingTo("21.00");
        assertThat(result.priceWithTax()).isEqualByComparingTo("121.00");
    }

    @Test
    void should_scale_result_to_two_decimals() {
        CalculatedTax result = calculator.calculate(new BigDecimal("100.00"));
        assertThat(result.amount().scale()).isEqualTo(2);
    }

    @Test
    void should_round_half_up_to_scale_2() {
        // 12.50 * 0.21 = 2.6250 -> HALF_UP -> 2.63
        CalculatedTax result = calculator.calculate(new BigDecimal("12.50"));
        assertThat(result.amount()).isEqualByComparingTo("2.63");
    }

    @Test
    void should_expose_percentage_21() {
        assertThat(calculator.getPercentage()).isEqualByComparingTo("21");
    }

    @Test
    void should_expose_type_IVA() {
        assertThat(calculator.getType()).isEqualTo(TaxType.IVA);
    }

    @Test
    void should_reject_null_price() {
        assertThatThrownBy(() -> calculator.calculate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("price");
    }
}
