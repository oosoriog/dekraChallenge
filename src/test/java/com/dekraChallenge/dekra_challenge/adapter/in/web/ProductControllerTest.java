package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.adapter.in.web.auth.CurrentUserProvider;
import com.dekraChallenge.dekra_challenge.application.ProductService;
import com.dekraChallenge.dekra_challenge.config.TaxConfig;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductFilter;
import com.dekraChallenge.dekra_challenge.domain.model.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({TaxConfig.class, ProductWebMapper.class, ProductWebStructMapperImpl.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CurrentUserProvider currentUserProvider() {
            return () -> "admin";
        }
    }

    // --- GET /productos (the public path stays Spanish) ---

    @Test
    void listProducts_returns_200_with_tax_fields() throws Exception {
        Product p = new Product(1L, "Teclado", "Mecánico", new BigDecimal("100.00"));
        when(productService.search(any(ProductFilter.class))).thenReturn(List.of(p));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Teclado"))
                .andExpect(jsonPath("$[0].descripcion").value("Mecánico"))
                .andExpect(jsonPath("$[0].precio").value(100.00))
                .andExpect(jsonPath("$[0].tipoImpuesto").value("IVA"))
                .andExpect(jsonPath("$[0].porcentajeImpuesto").value(21))
                .andExpect(jsonPath("$[0].importeImpuesto").value(21.00))
                .andExpect(jsonPath("$[0].precioConImpuesto").value(121.00))
                // No audit fields
                .andExpect(jsonPath("$[0].deleted").doesNotExist())
                .andExpect(jsonPath("$[0].createdAt").doesNotExist())
                .andExpect(jsonPath("$[0].updatedAt").doesNotExist())
                .andExpect(jsonPath("$[0].deletedAt").doesNotExist())
                .andExpect(jsonPath("$[0].deletedBy").doesNotExist());
    }

    @Test
    void listProducts_with_filter_params_delegates_correctly() throws Exception {
        when(productService.search(any(ProductFilter.class))).thenReturn(List.of());

        mockMvc.perform(get("/productos")
                        .param("nombre", "tec")
                        .param("precioMin", "10.00")
                        .param("precioMax", "200.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        ArgumentCaptor<ProductFilter> captor = ArgumentCaptor.forClass(ProductFilter.class);
        verify(productService).search(captor.capture());
        ProductFilter filter = captor.getValue();
        assertThat(filter.name()).isEqualTo("tec");
        assertThat(filter.priceMin()).isEqualByComparingTo("10.00");
        assertThat(filter.priceMax()).isEqualByComparingTo("200.00");
        assertThat(filter.id()).isNull();
        assertThat(filter.description()).isNull();
    }

    // --- GET /productos/{id} ---

    @Test
    void getProductoById_returns_200_with_tax_fields() throws Exception {
        Product p = new Product(5L, "Monitor", "4K", new BigDecimal("300.00"));
        when(productService.getById(5L)).thenReturn(p);

        mockMvc.perform(get("/productos/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("Monitor"))
                .andExpect(jsonPath("$.descripcion").value("4K"))
                .andExpect(jsonPath("$.precio").value(300.00))
                .andExpect(jsonPath("$.tipoImpuesto").value("IVA"))
                .andExpect(jsonPath("$.porcentajeImpuesto").value(21))
                .andExpect(jsonPath("$.importeImpuesto").value(63.00))
                .andExpect(jsonPath("$.precioConImpuesto").value(363.00))
                // No audit fields
                .andExpect(jsonPath("$.deleted").doesNotExist())
                .andExpect(jsonPath("$.createdAt").doesNotExist());
    }

    @Test
    void getProductoById_missing_returns_404_with_error_response() throws Exception {
        when(productService.getById(99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(get("/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"))
                .andExpect(jsonPath("$.path").value("/productos/99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // --- POST /productos ---

    @Test
    void createProducto_valid_returns_201_with_location_and_body() throws Exception {
        Product created = new Product(10L, "USB Hub", "4 puertos", new BigDecimal("25.00"));
        when(productService.create(any(Product.class))).thenReturn(created);

        String body = """
                {"nombre": "USB Hub", "descripcion": "4 puertos", "precio": 25.00}
                """;

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/productos/10")))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nombre").value("USB Hub"))
                .andExpect(jsonPath("$.tipoImpuesto").value("IVA"))
                .andExpect(jsonPath("$.importeImpuesto").value(5.25))
                .andExpect(jsonPath("$.precioConImpuesto").value(30.25));
    }

    @Test
    void createProducto_blank_nombre_returns_400_with_field_errors() throws Exception {
        String body = """
                {"nombre": "", "precio": 10.00}
                """;

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("nombre")));
    }

    @Test
    void createProducto_negative_precio_returns_400_with_field_errors() throws Exception {
        String body = """
                {"nombre": "Test", "precio": -5.00}
                """;

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.fieldErrors[*].field", hasItem("precio")));
    }

    // --- PUT /productos/{id} ---

    @Test
    void updateProducto_valid_returns_200() throws Exception {
        Product updated = new Product(3L, "Ratón Pro", "Inalámbrico", new BigDecimal("45.00"));
        when(productService.update(eq(3L), any(Product.class))).thenReturn(updated);

        String body = """
                {"nombre": "Ratón Pro", "descripcion": "Inalámbrico", "precio": 45.00}
                """;

        mockMvc.perform(put("/productos/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Ratón Pro"))
                .andExpect(jsonPath("$.tipoImpuesto").value("IVA"));
    }

    @Test
    void updateProducto_missing_returns_404() throws Exception {
        when(productService.update(eq(999L), any(Product.class)))
                .thenThrow(new ProductNotFoundException(999L));

        String body = """
                {"nombre": "X", "precio": 10.00}
                """;

        mockMvc.perform(put("/productos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // --- DELETE /productos/{id} ---

    @Test
    void deleteProducto_returns_204_and_uses_system_username() throws Exception {
        doNothing().when(productService).delete(7L, "admin");

        mockMvc.perform(delete("/productos/7"))
                .andExpect(status().isNoContent());

        verify(productService).delete(7L, "admin");
    }

    @Test
    void deleteProducto_missing_returns_404() throws Exception {
        doThrow(new ProductNotFoundException(88L)).when(productService).delete(88L, "admin");

        mockMvc.perform(delete("/productos/88"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with id: 88"));
    }

    // --- Verify no audit fields in responses ---

    @Test
    void responses_do_not_contain_audit_fields() throws Exception {
        Product p = new Product(1L, "Test", null, new BigDecimal("50.00"));
        when(productService.search(any(ProductFilter.class))).thenReturn(List.of(p));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].deleted").doesNotExist())
                .andExpect(jsonPath("$[0].createdAt").doesNotExist())
                .andExpect(jsonPath("$[0].updatedAt").doesNotExist())
                .andExpect(jsonPath("$[0].deletedAt").doesNotExist())
                .andExpect(jsonPath("$[0].deletedBy").doesNotExist());
    }
}
