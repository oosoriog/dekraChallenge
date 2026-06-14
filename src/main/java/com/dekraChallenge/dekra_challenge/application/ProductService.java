package com.dekraChallenge.dekra_challenge.application;

import com.dekraChallenge.dekra_challenge.config.CacheConfig;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductFilter;
import com.dekraChallenge.dekra_challenge.domain.model.ProductNotFoundException;
import com.dekraChallenge.dekra_challenge.domain.port.out.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PRODUCTS, allEntries = true)
    })
    public Product create(Product product) {
        Product created = repository.save(product);
        log.info("Product created: id={}, name='{}'", created.getId(), created.getName());
        return created;
    }

    @Cacheable(value = CacheConfig.PRODUCTS, key = "'all'")
    public List<Product> list() {
        return repository.findAll();
    }

    @Cacheable(value = CacheConfig.PRODUCT_BY_ID, key = "#id")
    public Product getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found: id={}", id);
                    return new ProductNotFoundException(id);
                });
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PRODUCTS, allEntries = true),
            @CacheEvict(value = CacheConfig.PRODUCT_BY_ID, key = "#id")
    })
    public Product update(Long id, Product changes) {
        Product updated = repository.update(id, changes)
                .orElseThrow(() -> {
                    log.warn("Product not found for update: id={}", id);
                    return new ProductNotFoundException(id);
                });
        log.info("Product updated: id={}, name='{}'", updated.getId(), updated.getName());
        return updated;
    }

    public List<Product> search(ProductFilter filter) {
        log.debug("Searching products with filter: {}", filter);
        return repository.search(filter);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheConfig.PRODUCTS, allEntries = true),
            @CacheEvict(value = CacheConfig.PRODUCT_BY_ID, key = "#id")
    })
    public void delete(Long id, String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username must not be null or blank for soft delete");
        }
        boolean deleted = repository.softDelete(id, username);
        if (!deleted) {
            log.warn("Product not found for delete: id={}", id);
            throw new ProductNotFoundException(id);
        }
        log.info("Product deleted: id={}, deletedBy='{}'", id, username);
    }
}
