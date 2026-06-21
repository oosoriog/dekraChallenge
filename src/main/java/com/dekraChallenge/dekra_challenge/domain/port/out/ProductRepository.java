package com.dekraChallenge.dekra_challenge.domain.port.out;

import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductFilter;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);


    Optional<Product> findById(Long id);

    Optional<Product> update(Long id, Product changes);

    boolean softDelete(Long id, String deletedBy);

    List<Product> search(ProductFilter filter);
}
