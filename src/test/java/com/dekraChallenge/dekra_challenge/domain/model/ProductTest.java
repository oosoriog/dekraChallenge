package com.dekraChallenge.dekra_challenge.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void should_create_product_when_valid() {
        Product product = new Product(1L, "Teclado", "Teclado mecánico", new BigDecimal("49.99"));

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("Teclado");
        assertThat(product.getDescription()).isEqualTo("Teclado mecánico");
        assertThat(product.getPrice()).isEqualByComparingTo("49.99");
    }

    @Test
    void should_allow_null_id_when_not_persisted() {
        Product product = Product.of("Ratón", "Ratón inalámbrico", new BigDecimal("19.95"));

        assertThat(product.getId()).isNull();
        assertThat(product.getName()).isEqualTo("Ratón");
    }

    @Test
    void should_allow_null_description() {
        Product product = Product.of("Monitor", null, new BigDecimal("150.00"));

        assertThat(product.getDescription()).isNull();
    }

    @Test
    void should_allow_zero_price() {
        Product product = Product.of("Muestra gratis", "Promo", BigDecimal.ZERO);

        assertThat(product.getPrice()).isEqualByComparingTo("0");
    }

    @Test
    void should_reject_null_name() {
        assertThatThrownBy(() -> Product.of(null, "desc", new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_reject_blank_name() {
        assertThatThrownBy(() -> Product.of("   ", "desc", new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void should_reject_null_price() {
        assertThatThrownBy(() -> Product.of("Product", "desc", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");
    }

    @Test
    void should_reject_negative_price() {
        assertThatThrownBy(() -> Product.of("Product", "desc", new BigDecimal("-0.01")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");
    }

    // --- equals / hashCode contract ---

    @Test
    void should_be_equal_to_itself() {
        Product p = new Product(1L, "A", "d", new BigDecimal("10.00"));

        assertThat(p).isEqualTo(p);
        assertThat(p.hashCode()).isEqualTo(p.hashCode());
    }

    @Test
    void should_be_equal_when_all_fields_match_with_different_price_scale() {
        Product p1 = new Product(1L, "A", "d", new BigDecimal("10.00"));
        Product p2 = new Product(1L, "A", "d", new BigDecimal("10.0"));

        assertThat(p1).isEqualTo(p2);
        assertThat(p1).hasSameHashCodeAs(p2);
    }

    @Test
    void should_not_be_equal_to_null() {
        Product p = new Product(1L, "A", "d", new BigDecimal("10.00"));

        assertThat(p).isNotEqualTo(null);
    }

    @Test
    void should_not_be_equal_to_other_type() {
        Product p = new Product(1L, "A", "d", new BigDecimal("10.00"));

        assertThat(p).isNotEqualTo("not a product");
    }

    @Test
    void should_not_be_equal_when_id_differs() {
        Product p1 = new Product(1L, "A", "d", new BigDecimal("10.00"));
        Product p2 = new Product(2L, "A", "d", new BigDecimal("10.00"));

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void should_not_be_equal_when_name_differs() {
        Product p1 = new Product(1L, "A", "d", new BigDecimal("10.00"));
        Product p2 = new Product(1L, "B", "d", new BigDecimal("10.00"));

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void should_not_be_equal_when_description_differs() {
        Product p1 = new Product(1L, "A", "d1", new BigDecimal("10.00"));
        Product p2 = new Product(1L, "A", "d2", new BigDecimal("10.00"));

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void should_not_be_equal_when_price_differs() {
        Product p1 = new Product(1L, "A", "d", new BigDecimal("10.00"));
        Product p2 = new Product(1L, "A", "d", new BigDecimal("20.00"));

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void should_be_equal_when_both_have_null_id_and_description() {
        Product p1 = Product.of("A", null, new BigDecimal("10.00"));
        Product p2 = Product.of("A", null, new BigDecimal("10.00"));

        assertThat(p1).isEqualTo(p2);
        assertThat(p1).hasSameHashCodeAs(p2);
    }

    @Test
    void toString_should_contain_fields() {
        Product p = new Product(1L, "Teclado", "d", new BigDecimal("10.00"));

        assertThat(p.toString()).contains("Teclado").contains("id=1");
    }
}
