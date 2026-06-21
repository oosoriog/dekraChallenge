package com.dekraChallenge.dekra_challenge.adapter.out.persistence;

import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductFilter;
import com.dekraChallenge.dekra_challenge.domain.port.out.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository jpaRepository;
    private final ProductEntityMapper mapper;

    public ProductRepositoryAdapter(SpringDataProductRepository jpaRepository,
                                    ProductEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = mapper.toNewEntity(product);
        ProductJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }


    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findByIdAndDeletedFalse(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Product> update(Long id, Product changes) {
        return jpaRepository.findByIdAndDeletedFalse(id)
                .map(entity -> {
                    mapper.updateEntity(changes, entity);
                    ProductJpaEntity updated = jpaRepository.save(entity);
                    return mapper.toDomain(updated);
                });
    }

    @Override
    public boolean softDelete(Long id, String deletedBy) {
        Optional<ProductJpaEntity> found = jpaRepository.findByIdAndDeletedFalse(id);
        if (found.isEmpty()) {
            return false;
        }
        ProductJpaEntity entity = found.get();
        entity.setDeleted(true);
        entity.setDeletedAt(Instant.now());
        entity.setDeletedBy(deletedBy);
        jpaRepository.save(entity);
        return true;
    }

    @Override
    public List<Product> search(ProductFilter filter) {
        Specification<ProductJpaEntity> spec = buildSpecification(filter);
        return jpaRepository.findAll(spec).stream()
                .map(mapper::toDomain)
                .toList();
    }

    private Specification<ProductJpaEntity> buildSpecification(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude soft-deleted
            predicates.add(cb.equal(root.get("deleted"), false));

            if (filter.id() != null) {
                predicates.add(cb.equal(root.get("id"), filter.id()));
            }

            if (filter.name() != null && !filter.name().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + filter.name().toLowerCase() + "%"));
            }

            if (filter.description() != null && !filter.description().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + filter.description().toLowerCase() + "%"));
            }

            if (filter.priceMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.priceMin()));
            }

            if (filter.priceMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.priceMax()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
