package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.adapter.out.persistence.ProductJpaEntity;
import com.dekraChallenge.dekra_challenge.adapter.out.persistence.SpringDataProductRepository;
import com.dekraChallenge.dekra_challenge.config.JwtProperties;
import com.dekraChallenge.dekra_challenge.support.TestJwtFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataProductRepository repository;

    @Autowired
    private JwtProperties jwtProperties;

    private TestJwtFactory jwtFactory;

    private static final Pattern ID_PATTERN = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    private static final String PRODUCT_JSON = """
            {"nombre": "Teclado", "descripcion": "Mecánico", "precio": 100.00}
            """;

    @BeforeEach
    void setUp() {
        jwtFactory = new TestJwtFactory(jwtProperties.getSecret());
    }

    // --- Authentication (401) ---

    @Test
    void getProductos_no_token_returns_401() throws Exception {
        mockMvc.perform(get("/productos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProductos_invalid_token_returns_401() throws Exception {
        mockMvc.perform(get("/productos")
                        .header("Authorization", "Bearer " + jwtFactory.invalidToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProductos_garbage_token_returns_401() throws Exception {
        mockMvc.perform(get("/productos")
                        .header("Authorization", "Bearer not-a-real-jwt"))
                .andExpect(status().isUnauthorized());
    }

    // --- USER role: reads allowed, writes forbidden ---

    @Test
    void getProductos_user_token_returns_200() throws Exception {
        mockMvc.perform(get("/productos")
                        .header("Authorization", "Bearer " + jwtFactory.userToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getProductoById_user_token_returns_200() throws Exception {
        long id = createProductAsAdmin();

        mockMvc.perform(get("/productos/" + id)
                        .header("Authorization", "Bearer " + jwtFactory.userToken()))
                .andExpect(status().isOk());
    }

    @Test
    void postProductos_user_token_returns_403() throws Exception {
        mockMvc.perform(post("/productos")
                        .header("Authorization", "Bearer " + jwtFactory.userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void putProductos_user_token_returns_403() throws Exception {
        long id = createProductAsAdmin();

        mockMvc.perform(put("/productos/" + id)
                        .header("Authorization", "Bearer " + jwtFactory.userToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProductos_user_token_returns_403() throws Exception {
        long id = createProductAsAdmin();

        mockMvc.perform(delete("/productos/" + id)
                        .header("Authorization", "Bearer " + jwtFactory.userToken()))
                .andExpect(status().isForbidden());
    }

    // --- ADMIN role: writes allowed ---

    @Test
    void postProductos_admin_token_returns_201() throws Exception {
        mockMvc.perform(post("/productos")
                        .header("Authorization", "Bearer " + jwtFactory.adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void putProductos_admin_token_returns_200() throws Exception {
        long id = createProductAsAdmin();

        String updated = """
                {"nombre": "Teclado Pro", "descripcion": "RGB", "precio": 150.00}
                """;

        mockMvc.perform(put("/productos/" + id)
                        .header("Authorization", "Bearer " + jwtFactory.adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updated))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProductos_admin_token_returns_204() throws Exception {
        long id = createProductAsAdmin();

        mockMvc.perform(delete("/productos/" + id)
                        .header("Authorization", "Bearer " + jwtFactory.adminToken()))
                .andExpect(status().isNoContent());
    }

    // --- Audit propagation: deletedBy = JWT subject ---

    @Test
    void delete_stores_deletedBy_from_jwt_subject() throws Exception {
        long id = createProductAsAdmin();

        mockMvc.perform(delete("/productos/" + id)
                        .header("Authorization", "Bearer " + jwtFactory.adminToken()))
                .andExpect(status().isNoContent());

        // Load the raw entity (including soft-deleted rows) to inspect audit fields.
        ProductJpaEntity entity = repository.findById(id).orElseThrow();
        assertThat(entity.isDeleted()).isTrue();
        assertThat(entity.getDeletedBy()).isEqualTo("admin");
    }

    // --- Helpers ---

    private long createProductAsAdmin() throws Exception {
        MvcResult result = mockMvc.perform(post("/productos")
                        .header("Authorization", "Bearer " + jwtFactory.adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PRODUCT_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Matcher matcher = ID_PATTERN.matcher(responseBody);
        if (!matcher.find()) {
            throw new IllegalStateException("Could not extract id from response: " + responseBody);
        }
        return Long.parseLong(matcher.group(1));
    }
}
