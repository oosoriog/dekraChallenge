package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.api.web.model.ProductResponse;
import com.dekraChallenge.dekra_challenge.api.web.model.TipoImpuesto;
import com.dekraChallenge.dekra_challenge.config.TaxProperties;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.tax.ItbisTaxCalculator;
import com.dekraChallenge.dekra_challenge.domain.tax.IvaTaxCalculator;
import com.dekraChallenge.dekra_challenge.domain.tax.TaxCalculatorResolver;
import com.dekraChallenge.dekra_challenge.domain.tax.TaxType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ProductWebMapperTest {

    private final IvaTaxCalculator ivaCalc = new IvaTaxCalculator();
    private final ItbisTaxCalculator itbisCalc = new ItbisTaxCalculator();
    private final TaxCalculatorResolver resolver =
            new TaxCalculatorResolver(List.of(ivaCalc, itbisCalc));
    private final ProductWebStructMapper structMapper = new ProductWebStructMapperImpl();

    private ProductWebMapper mapperWith(TaxType type) {
        TaxProperties props = new TaxProperties();
        props.setType(type);
        return new ProductWebMapper(resolver, props, structMapper);
    }

    @Test
    void toDomain_maps_request_fields_correctly() {
        ProductWebMapper mapper = mapperWith(TaxType.IVA);

        com.dekraChallenge.dekra_challenge.api.web.model.ProductRequest req =
                new com.dekraChallenge.dekra_challenge.api.web.model.ProductRequest();
        req.setNombre("Teclado");
        req.setDescripcion("Mecánico");
        req.setPrecio(new BigDecimal("79.99"));

        Product domain = mapper.toDomain(req);

        assertThat(domain.getId()).isNull();
        assertThat(domain.getName()).isEqualTo("Teclado");
        assertThat(domain.getDescription()).isEqualTo("Mecánico");
        assertThat(domain.getPrice()).isEqualByComparingTo("79.99");
    }

    @Test
    void toResponse_with_IVA_computes_tax_fields_correctly() {
        ProductWebMapper mapper = mapperWith(TaxType.IVA);

        Product product = new Product(1L, "Monitor", "27 pulgadas", new BigDecimal("100.00"));
        ProductResponse response = mapper.toResponse(product);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNombre()).isEqualTo("Monitor");
        assertThat(response.getDescripcion()).isEqualTo("27 pulgadas");
        assertThat(response.getPrecio()).isEqualByComparingTo("100.00");
        assertThat(response.getTipoImpuesto()).isEqualTo(TipoImpuesto.IVA);
        assertThat(response.getPorcentajeImpuesto()).isEqualByComparingTo("21");
        assertThat(response.getImporteImpuesto()).isEqualByComparingTo("21.00");
        assertThat(response.getPrecioConImpuesto()).isEqualByComparingTo("121.00");
    }

    @Test
    void toResponse_with_ITBIS_computes_tax_fields_correctly() {
        ProductWebMapper mapper = mapperWith(TaxType.ITBIS);

        Product product = new Product(2L, "Ratón", null, new BigDecimal("100.00"));
        ProductResponse response = mapper.toResponse(product);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getNombre()).isEqualTo("Ratón");
        assertThat(response.getDescripcion()).isNull();
        assertThat(response.getPrecio()).isEqualByComparingTo("100.00");
        assertThat(response.getTipoImpuesto()).isEqualTo(TipoImpuesto.ITBIS);
        assertThat(response.getPorcentajeImpuesto()).isEqualByComparingTo("18");
        assertThat(response.getImporteImpuesto()).isEqualByComparingTo("18.00");
        assertThat(response.getPrecioConImpuesto()).isEqualByComparingTo("118.00");
    }

    @Test
    void toResponseList_maps_all_items() {
        ProductWebMapper mapper = mapperWith(TaxType.IVA);

        List<Product> products = List.of(
                new Product(1L, "A", null, new BigDecimal("10.00")),
                new Product(2L, "B", "desc", new BigDecimal("20.00"))
        );

        List<ProductResponse> responses = mapper.toResponseList(products);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getNombre()).isEqualTo("A");
        assertThat(responses.get(1).getNombre()).isEqualTo("B");
    }

    @Test
    void response_does_not_expose_audit_fields() {
        // Verify that ProductoResponse has no methods for audit fields
        Set<String> methodNames = Arrays.stream(ProductResponse.class.getMethods())
                .map(Method::getName)
                .collect(Collectors.toSet());

        assertThat(methodNames).doesNotContain(
                "getDeleted", "isDeleted", "setDeleted",
                "getCreatedAt", "setCreatedAt",
                "getUpdatedAt", "setUpdatedAt",
                "getDeletedAt", "setDeletedAt",
                "getDeletedBy", "setDeletedBy",
                "getCreatedBy", "setCreatedBy",
                "getUpdatedBy", "setUpdatedBy"
        );
    }
}
