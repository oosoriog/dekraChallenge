package com.dekraChallenge.dekra_challenge.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface SpringDataProductRepository
        extends JpaRepository<ProductJpaEntity, Long>, JpaSpecificationExecutor<ProductJpaEntity> {

    List<ProductJpaEntity> findByDeletedFalse();

    Optional<ProductJpaEntity> findByIdAndDeletedFalse(Long id);
}
