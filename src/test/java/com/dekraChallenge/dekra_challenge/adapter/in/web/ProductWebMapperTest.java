package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.api.web.model.ProductRequest;
import com.dekraChallenge.dekra_challenge.api.web.model.ProductResponse;
import com.dekraChallenge.dekra_challenge.api.web.model.TipoImpuesto;
import com.dekraChallenge.dekra_challenge.application.ProductView;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.tax.CalculatedTax;
import com.dekraChallenge.dekra_challenge.domain.tax.ItbisTaxCalculator;
import com.dekraChallenge.dekra_challenge.domain.tax.IvaTaxCalculator;
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
    private final ProductWebStructMapper mapper = new ProductWebStructMapperImpl();

    private ProductView ivaView(Product product) {
        CalculatedTax tax = ivaCalc.calculate(product.getPrice());
        return new ProductView(product, tax);
    }

    private ProductView itbisView(Product product) {
        CalculatedTax tax = itbisCalc.calculate(product.getPrice());
        return new ProductView(product, tax);
    }

    @Test
    void toDomain_maps_request_fields_correctly() {
        ProductRequest req = new ProductRequest();
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
    void toResponse_with_IVA_maps_tax_fields_correctly() {
        Product product = new Product(1L, "Monitor", "27 pulgadas", new BigDecimal("100.00"));
        ProductResponse response = mapper.toResponse(ivaView(product));

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
    void toResponse_with_ITBIS_maps_tax_fields_correctly() {
        Product product = new Product(2L, "Ratón", null, new BigDecimal("100.00"));
        ProductResponse response = mapper.toResponse(itbisView(product));

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
        List<ProductView> views = List.of(
                ivaView(new Product(1L, "A", null, new BigDecimal("10.00"))),
                ivaView(new Product(2L, "B", "desc", new BigDecimal("20.00")))
        );

        List<ProductResponse> responses = mapper.toResponseList(views);

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
