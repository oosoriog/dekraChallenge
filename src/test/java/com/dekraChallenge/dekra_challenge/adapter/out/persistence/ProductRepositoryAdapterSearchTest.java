package com.dekraChallenge.dekra_challenge.adapter.out.persistence;

import com.dekraChallenge.dekra_challenge.config.JpaAuditingConfig;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductFilter;
import com.dekraChallenge.dekra_challenge.domain.port.out.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the dynamic-search JPA Specification branches in {@link ProductRepositoryAdapter}
 * (each filter present/absent, and soft-delete exclusion).
 */
@DataJpaTest
@Import({ProductRepositoryAdapter.class, ProductEntityMapperImpl.class, JpaAuditingConfig.class})
class ProductRepositoryAdapterSearchTest {

    @Autowired
    private ProductRepository repository;

    private Long keyboardId;

    @BeforeEach
    void seed() {
        Product keyboard = repository.save(Product.of("Teclado mecánico", "RGB switches", new BigDecimal("80.00")));
        repository.save(Product.of("Ratón", "inalámbrico", new BigDecimal("20.00")));
        repository.save(Product.of("Monitor", "4K UHD", new BigDecimal("300.00")));
        Product deleted = repository.save(Product.of("Teclado viejo", "obsoleto", new BigDecimal("5.00")));
        repository.softDelete(deleted.getId(), "tester");
        keyboardId = keyboard.getId();
    }

    @Test
    void should_return_all_non_deleted_when_filter_empty() {
        List<Product> result = repository.search(new ProductFilter(null, null, null, null, null));

        assertThat(result).hasSize(3);
        assertThat(result).noneMatch(p -> p.getName().equals("Teclado viejo"));
    }

    @Test
    void should_filter_by_exact_id() {
        List<Product> result = repository.search(new ProductFilter(keyboardId, null, null, null, null));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Teclado mecánico");
    }

    @Test
    void should_filter_by_name_partial_case_insensitive() {
        List<Product> result = repository.search(new ProductFilter(null, "teclado", null, null, null));

        // "Teclado viejo" is soft-deleted, so only the non-deleted "Teclado mecánico" matches
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Teclado mecánico");
    }

    @Test
    void should_ignore_blank_name_filter() {
        List<Product> result = repository.search(new ProductFilter(null, "   ", null, null, null));

        assertThat(result).hasSize(3);
    }

    @Test
    void should_filter_by_description_partial_case_insensitive() {
        List<Product> result = repository.search(new ProductFilter(null, null, "INALÁMBRICO", null, null));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ratón");
    }

    @Test
    void should_ignore_blank_description_filter() {
        List<Product> result = repository.search(new ProductFilter(null, null, "  ", null, null));

        assertThat(result).hasSize(3);
    }

    @Test
    void should_filter_by_price_min() {
        List<Product> result = repository.search(
                new ProductFilter(null, null, null, new BigDecimal("50.00"), null));

        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Teclado mecánico", "Monitor");
    }

    @Test
    void should_filter_by_price_max() {
        List<Product> result = repository.search(
                new ProductFilter(null, null, null, null, new BigDecimal("50.00")));

        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Ratón");
    }

    @Test
    void should_filter_by_price_range() {
        List<Product> result = repository.search(
                new ProductFilter(null, null, null, new BigDecimal("10.00"), new BigDecimal("100.00")));

        assertThat(result).extracting(Product::getName)
                .containsExactlyInAnyOrder("Teclado mecánico", "Ratón");
    }

    @Test
    void should_combine_filters() {
        List<Product> result = repository.search(
                new ProductFilter(null, "teclado", "rgb", new BigDecimal("50.00"), new BigDecimal("100.00")));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Teclado mecánico");
    }

    @Test
    void should_exclude_soft_deleted_even_when_matching_filter() {
        // "Teclado viejo" matches "teclado" but is soft-deleted
        List<Product> result = repository.search(new ProductFilter(null, "viejo", null, null, null));

        assertThat(result).isEmpty();
    }
}
