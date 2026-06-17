package com.dekraChallenge.dekra_challenge.application;

import com.dekraChallenge.dekra_challenge.config.TaxProperties;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductFilter;
import com.dekraChallenge.dekra_challenge.domain.tax.ItbisTaxCalculator;
import com.dekraChallenge.dekra_challenge.domain.tax.IvaTaxCalculator;
import com.dekraChallenge.dekra_challenge.domain.tax.TaxCalculatorResolver;
import com.dekraChallenge.dekra_challenge.domain.tax.TaxType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductViewServiceTest {

    @Mock
    private ProductService productService;

    private final TaxCalculatorResolver resolver =
            new TaxCalculatorResolver(List.of(new IvaTaxCalculator(), new ItbisTaxCalculator()));

    private ProductViewService viewServiceWith(TaxType type) {
        TaxProperties props = new TaxProperties();
        props.setType(type);
        return new ProductViewService(productService, resolver, props);
    }

    @Test
    void create_enriches_product_with_iva_tax() {
        Product created = new Product(1L, "Teclado", "Mecánico", new BigDecimal("100.00"));
        when(productService.create(any())).thenReturn(created);

        ProductView view = viewServiceWith(TaxType.IVA).create(created);

        assertThat(view.product()).isSameAs(created);
        assertThat(view.tax().type()).isEqualTo(TaxType.IVA);
        assertThat(view.tax().amount()).isEqualByComparingTo("21.00");
        assertThat(view.tax().priceWithTax()).isEqualByComparingTo("121.00");
    }

    @Test
    void getById_enriches_product_with_itbis_tax() {
        Product product = new Product(2L, "Ratón", null, new BigDecimal("100.00"));
        when(productService.getById(2L)).thenReturn(product);

        ProductView view = viewServiceWith(TaxType.ITBIS).getById(2L);

        assertThat(view.product()).isSameAs(product);
        assertThat(view.tax().type()).isEqualTo(TaxType.ITBIS);
        assertThat(view.tax().amount()).isEqualByComparingTo("18.00");
        assertThat(view.tax().priceWithTax()).isEqualByComparingTo("118.00");
    }

    @Test
    void update_enriches_product_with_tax() {
        Product updated = new Product(3L, "Monitor", "4K", new BigDecimal("200.00"));
        when(productService.update(eq(3L), any())).thenReturn(updated);

        ProductView view = viewServiceWith(TaxType.IVA).update(3L, updated);

        assertThat(view.product()).isSameAs(updated);
        assertThat(view.tax().amount()).isEqualByComparingTo("42.00");
        assertThat(view.tax().priceWithTax()).isEqualByComparingTo("242.00");
    }

    @Test
    void search_enriches_all_products_with_tax() {
        List<Product> products = List.of(
                new Product(1L, "A", null, new BigDecimal("10.00")),
                new Product(2L, "B", "desc", new BigDecimal("20.00")));
        when(productService.search(any(ProductFilter.class))).thenReturn(products);

        List<ProductView> views =
                viewServiceWith(TaxType.IVA).search(new ProductFilter(null, null, null, null, null));

        assertThat(views).hasSize(2);
        assertThat(views.get(0).tax().priceWithTax()).isEqualByComparingTo("12.10");
        assertThat(views.get(1).tax().priceWithTax()).isEqualByComparingTo("24.20");
    }
}

