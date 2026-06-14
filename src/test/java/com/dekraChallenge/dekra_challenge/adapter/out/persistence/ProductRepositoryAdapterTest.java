package com.dekraChallenge.dekra_challenge.adapter.out.persistence;

import com.dekraChallenge.dekra_challenge.config.JpaAuditingConfig;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.port.out.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ProductRepositoryAdapter.class, ProductEntityMapperImpl.class, JpaAuditingConfig.class})
class ProductRepositoryAdapterTest {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private SpringDataProductRepository jpaRepo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_save_and_find_product() {
        Product saved = repository.save(Product.of("Teclado", "Mecánico", new BigDecimal("49.99")));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Teclado");
        assertThat(saved.getPrice()).isEqualByComparingTo("49.99");
    }

    @Test
    void should_set_createdAt_and_updatedAt_on_persist() {
        repository.save(Product.of("Monitor", null, new BigDecimal("200.00")));

        ProductJpaEntity entity = jpaRepo.findAll().get(0);
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.isDeleted()).isFalse();
    }

    @Test
    void should_set_createdBy_and_updatedBy_to_system_without_auth() {
        repository.save(Product.of("Monitor", null, new BigDecimal("200.00")));

        ProductJpaEntity entity = jpaRepo.findAll().get(0);
        assertThat(entity.getCreatedBy()).isEqualTo("system");
        assertThat(entity.getUpdatedBy()).isEqualTo("system");
    }

    @Test
    @WithMockUser(username = "admin")
    void should_set_createdBy_and_updatedBy_from_authenticated_user() {
        repository.save(Product.of("Teclado", "Mecánico", new BigDecimal("49.99")));

        ProductJpaEntity entity = jpaRepo.findAll().get(0);
        assertThat(entity.getCreatedBy()).isEqualTo("admin");
        assertThat(entity.getUpdatedBy()).isEqualTo("admin");
    }

    @Test
    @WithMockUser(username = "admin")
    void should_not_change_createdBy_on_update() {
        Product saved = repository.save(Product.of("Ratón", "Inalámbrico", new BigDecimal("19.95")));
        entityManager.flush();
        entityManager.clear();

        ProductJpaEntity beforeUpdate = jpaRepo.findById(saved.getId()).orElseThrow();
        assertThat(beforeUpdate.getCreatedBy()).isEqualTo("admin");

        Product changes = new Product(saved.getId(), "Ratón Pro", "Inalámbrico Pro", new BigDecimal("29.95"));
        repository.update(saved.getId(), changes);
        entityManager.flush();
        entityManager.clear();

        ProductJpaEntity afterUpdate = jpaRepo.findById(saved.getId()).orElseThrow();
        assertThat(afterUpdate.getCreatedBy()).isEqualTo("admin");
        assertThat(afterUpdate.getUpdatedBy()).isEqualTo("admin");
    }

    @Test
    void should_update_updatedAt_on_modify() throws InterruptedException {
        Product saved = repository.save(Product.of("Ratón", "Inalámbrico", new BigDecimal("19.95")));
        entityManager.flush();
        entityManager.clear();

        ProductJpaEntity beforeUpdate = jpaRepo.findById(saved.getId()).orElseThrow();
        var createdAt = beforeUpdate.getCreatedAt();
        var firstUpdatedAt = beforeUpdate.getUpdatedAt();

        // Small delay to ensure timestamp difference
        Thread.sleep(50);

        Product changes = new Product(saved.getId(), "Ratón Pro", "Inalámbrico Pro", new BigDecimal("29.95"));
        repository.update(saved.getId(), changes);
        entityManager.flush();
        entityManager.clear();

        ProductJpaEntity afterUpdate = jpaRepo.findById(saved.getId()).orElseThrow();
        assertThat(afterUpdate.getCreatedAt()).isEqualTo(createdAt);
        assertThat(afterUpdate.getUpdatedAt()).isAfter(firstUpdatedAt);
        assertThat(afterUpdate.getName()).isEqualTo("Ratón Pro");
    }

    @Test
    void should_exclude_deleted_from_findAll() {
        Product p1 = repository.save(Product.of("A", null, BigDecimal.ONE));
        repository.save(Product.of("B", null, BigDecimal.TEN));

        repository.softDelete(p1.getId(), "test-user");

        List<Product> result = repository.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("B");
    }

    @Test
    void should_return_empty_for_deleted_findById() {
        Product saved = repository.save(Product.of("C", null, BigDecimal.ONE));
        repository.softDelete(saved.getId(), "test-user");

        Optional<Product> found = repository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void should_soft_delete_setting_flag_timestamp_and_user() {
        Product saved = repository.save(Product.of("D", null, new BigDecimal("5.00")));

        boolean deleted = repository.softDelete(saved.getId(), "admin");

        assertThat(deleted).isTrue();
        ProductJpaEntity entity = jpaRepo.findById(saved.getId()).orElseThrow();
        assertThat(entity.isDeleted()).isTrue();
        assertThat(entity.getDeletedAt()).isNotNull();
        assertThat(entity.getDeletedBy()).isEqualTo("admin");
    }

    @Test
    void should_return_false_softDelete_when_missing() {
        boolean deleted = repository.softDelete(999L, "test-user");
        assertThat(deleted).isFalse();
    }

    @Test
    void should_return_false_softDelete_when_already_deleted() {
        Product saved = repository.save(Product.of("E", null, BigDecimal.ONE));
        repository.softDelete(saved.getId(), "user1");

        boolean deletedAgain = repository.softDelete(saved.getId(), "user2");
        assertThat(deletedAgain).isFalse();
    }

    @Test
    void should_return_empty_update_when_missing() {
        Product changes = Product.of("X", null, BigDecimal.ONE);
        Optional<Product> result = repository.update(999L, changes);
        assertThat(result).isEmpty();
    }

    @Test
    void should_return_empty_update_when_deleted() {
        Product saved = repository.save(Product.of("F", null, BigDecimal.ONE));
        repository.softDelete(saved.getId(), "test-user");

        Product changes = Product.of("F updated", null, BigDecimal.TEN);
        Optional<Product> result = repository.update(saved.getId(), changes);
        assertThat(result).isEmpty();
    }
}
