package com.dekraChallenge.dekra_challenge.application;

import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductNotFoundException;
import com.dekraChallenge.dekra_challenge.domain.port.out.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(repository);
    }

    @Test
    void should_create_product() {
        Product input = Product.of("Teclado", "Mecánico", new BigDecimal("49.99"));
        Product saved = new Product(1L, "Teclado", "Mecánico", new BigDecimal("49.99"));
        when(repository.save(any())).thenReturn(saved);

        Product result = service.create(input);

        assertThat(result.getId()).isEqualTo(1L);
        verify(repository).save(input);
    }

    @Test
    void should_list_only_active_products() {
        Product p1 = new Product(1L, "A", null, BigDecimal.ONE);
        Product p2 = new Product(2L, "B", null, BigDecimal.TEN);
        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = service.list();

        assertThat(result).hasSize(2);
    }

    @Test
    void should_get_by_id() {
        Product p = new Product(1L, "A", null, BigDecimal.ONE);
        when(repository.findById(1L)).thenReturn(Optional.of(p));

        Product result = service.getById(1L);

        assertThat(result.getName()).isEqualTo("A");
    }

    @Test
    void should_throw_not_found_when_get_missing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void should_update_existing() {
        Product changes = new Product(1L, "Updated", "Desc", new BigDecimal("10.00"));
        when(repository.update(eq(1L), any())).thenReturn(Optional.of(changes));

        Product result = service.update(1L, changes);

        assertThat(result.getName()).isEqualTo("Updated");
    }

    @Test
    void should_throw_not_found_when_update_missing() {
        Product changes = Product.of("X", null, BigDecimal.ONE);
        when(repository.update(eq(99L), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, changes))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void should_soft_delete_with_username() {
        when(repository.softDelete(1L, "test-user")).thenReturn(true);

        service.delete(1L, "test-user");

        verify(repository).softDelete(1L, "test-user");
    }

    @Test
    void should_throw_not_found_when_delete_missing() {
        when(repository.softDelete(99L, "test-user")).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L, "test-user"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void should_throw_when_username_null() {
        assertThatThrownBy(() -> service.delete(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }

    @Test
    void should_throw_when_username_blank() {
        assertThatThrownBy(() -> service.delete(1L, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("username");
    }
}
