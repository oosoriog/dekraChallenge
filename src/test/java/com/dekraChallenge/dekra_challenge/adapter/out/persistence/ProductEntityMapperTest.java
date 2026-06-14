package com.dekraChallenge.dekra_challenge.adapter.out.persistence;

import com.dekraChallenge.dekra_challenge.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Focused tests for the MapStruct-generated {@link ProductEntityMapper}.
 *
 * <p>Verifies pure structural mapping and, critically, that audit and soft-delete metadata
 * are never populated from the domain model nor overwritten on update.</p>
 */
class ProductEntityMapperTest {

    private final ProductEntityMapper mapper = new ProductEntityMapperImpl();

    @Test
    void toDomain_maps_business_fields() {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(7L);
        entity.setName("Teclado");
        entity.setDescription("Mecánico");
        entity.setPrice(new BigDecimal("49.99"));

        Product domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(7L);
        assertThat(domain.getName()).isEqualTo("Teclado");
        assertThat(domain.getDescription()).isEqualTo("Mecánico");
        assertThat(domain.getPrice()).isEqualByComparingTo("49.99");
    }

    @Test
    void toNewEntity_maps_business_fields_and_ignores_audit_and_soft_delete() {
        Product product = new Product(99L, "Monitor", "4K", new BigDecimal("200.00"));

        ProductJpaEntity entity = mapper.toNewEntity(product);

        // Business fields mapped
        assertThat(entity.getName()).isEqualTo("Monitor");
        assertThat(entity.getDescription()).isEqualTo("4K");
        assertThat(entity.getPrice()).isEqualByComparingTo("200.00");
        // id is not copied from the domain (DB-generated)
        assertThat(entity.getId()).isNull();
        // audit/soft-delete metadata is not populated from the domain
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
        assertThat(entity.getCreatedBy()).isNull();
        assertThat(entity.getUpdatedBy()).isNull();
        assertThat(entity.getDeletedAt()).isNull();
        assertThat(entity.getDeletedBy()).isNull();
        assertThat(entity.isDeleted()).isFalse();
    }

    @Test
    void updateEntity_updates_business_fields_only_and_preserves_audit_and_soft_delete() {
        Instant created = Instant.parse("2020-01-01T00:00:00Z");
        Instant updated = Instant.parse("2020-06-01T00:00:00Z");

        ProductJpaEntity existing = new ProductJpaEntity();
        existing.setId(5L);
        existing.setName("Old name");
        existing.setDescription("Old desc");
        existing.setPrice(new BigDecimal("10.00"));
        existing.setCreatedAt(created);
        existing.setUpdatedAt(updated);
        existing.setCreatedBy("creator");
        existing.setUpdatedBy("editor");
        existing.setDeleted(false);
        existing.setDeletedAt(null);
        existing.setDeletedBy(null);

        Product changes = new Product(5L, "New name", "New desc", new BigDecimal("25.50"));

        mapper.updateEntity(changes, existing);

        // Business fields updated
        assertThat(existing.getName()).isEqualTo("New name");
        assertThat(existing.getDescription()).isEqualTo("New desc");
        assertThat(existing.getPrice()).isEqualByComparingTo("25.50");
        // id unchanged
        assertThat(existing.getId()).isEqualTo(5L);
        // audit metadata preserved (not overwritten by the mapper)
        assertThat(existing.getCreatedAt()).isEqualTo(created);
        assertThat(existing.getUpdatedAt()).isEqualTo(updated);
        assertThat(existing.getCreatedBy()).isEqualTo("creator");
        assertThat(existing.getUpdatedBy()).isEqualTo("editor");
        // soft-delete metadata preserved
        assertThat(existing.isDeleted()).isFalse();
        assertThat(existing.getDeletedAt()).isNull();
        assertThat(existing.getDeletedBy()).isNull();
    }

    @Test
    void updateEntity_does_not_resurrect_soft_deleted_metadata() {
        ProductJpaEntity deletedEntity = new ProductJpaEntity();
        deletedEntity.setId(8L);
        deletedEntity.setName("Gone");
        deletedEntity.setPrice(new BigDecimal("1.00"));
        deletedEntity.setDeleted(true);
        deletedEntity.setDeletedAt(Instant.parse("2021-01-01T00:00:00Z"));
        deletedEntity.setDeletedBy("admin");

        Product changes = new Product(8L, "Gone updated", null, new BigDecimal("2.00"));

        mapper.updateEntity(changes, deletedEntity);

        // soft-delete flags remain untouched by the structural mapper
        assertThat(deletedEntity.isDeleted()).isTrue();
        assertThat(deletedEntity.getDeletedAt()).isEqualTo(Instant.parse("2021-01-01T00:00:00Z"));
        assertThat(deletedEntity.getDeletedBy()).isEqualTo("admin");
    }
}
