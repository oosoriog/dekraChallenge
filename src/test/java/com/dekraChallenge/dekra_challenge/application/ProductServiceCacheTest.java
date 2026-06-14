package com.dekraChallenge.dekra_challenge.application;

import com.dekraChallenge.dekra_challenge.config.CacheConfig;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.port.out.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "app.demo-data.enabled=false")
class ProductServiceCacheTest {

    @MockitoBean
    private ProductRepository repository;

    @Autowired
    private ProductService service;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCaches() {
        cacheManager.getCacheNames().forEach(name ->
                cacheManager.getCache(name).clear());
    }

    @Test
    void getById_should_cache_result() {
        Product product = new Product(1L, "Teclado", "Mecánico", new BigDecimal("49.99"));
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        // First call hits repository
        Product first = service.getById(1L);
        // Second call should hit cache
        Product second = service.getById(1L);

        assertThat(first.getName()).isEqualTo("Teclado");
        assertThat(second.getName()).isEqualTo("Teclado");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void list_should_cache_result() {
        List<Product> products = List.of(
                new Product(1L, "A", null, BigDecimal.ONE),
                new Product(2L, "B", null, BigDecimal.TEN));
        when(repository.findAll()).thenReturn(products);

        List<Product> first = service.list();
        List<Product> second = service.list();

        assertThat(first).hasSize(2);
        assertThat(second).hasSize(2);
        verify(repository, times(1)).findAll();
    }

    @Test
    void create_should_evict_products_list_cache() {
        List<Product> initial = List.of(new Product(1L, "A", null, BigDecimal.ONE));
        List<Product> updated = List.of(
                new Product(1L, "A", null, BigDecimal.ONE),
                new Product(2L, "B", null, BigDecimal.TEN));

        when(repository.findAll()).thenReturn(initial, updated);
        when(repository.save(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new Product(2L, "B", null, BigDecimal.TEN));

        // Populate cache
        service.list();
        // Create evicts list cache
        service.create(Product.of("B", null, BigDecimal.TEN));
        // Next list call should hit repository again
        List<Product> result = service.list();

        assertThat(result).hasSize(2);
        verify(repository, times(2)).findAll();
    }

    @Test
    void update_should_evict_product_and_list_caches() {
        Product original = new Product(1L, "Teclado", "Mecánico", new BigDecimal("49.99"));
        Product changed = new Product(1L, "Teclado Pro", "Mecánico Pro", new BigDecimal("79.99"));

        when(repository.findById(1L)).thenReturn(Optional.of(original), Optional.of(changed));
        when(repository.update(org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any())).thenReturn(Optional.of(changed));

        // Populate cache
        service.getById(1L);
        // Update evicts caches
        service.update(1L, changed);
        // Next getById should hit repository again
        Product result = service.getById(1L);

        assertThat(result.getName()).isEqualTo("Teclado Pro");
        verify(repository, times(2)).findById(1L);
    }

    @Test
    void delete_should_evict_product_cache() {
        Product product = new Product(1L, "Teclado", "Mecánico", new BigDecimal("49.99"));
        when(repository.findById(1L)).thenReturn(Optional.of(product), Optional.empty());
        when(repository.softDelete(1L, "admin")).thenReturn(true);

        // Populate cache
        service.getById(1L);
        // Delete evicts cache
        service.delete(1L, "admin");

        // Verify cache was populated then evicted
        assertThat(cacheManager.getCache(CacheConfig.PRODUCT_BY_ID).get(1L)).isNull();
    }
}
