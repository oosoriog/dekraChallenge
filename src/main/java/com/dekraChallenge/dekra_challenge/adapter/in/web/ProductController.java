package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.adapter.in.web.auth.CurrentUserProvider;
import com.dekraChallenge.dekra_challenge.api.web.api.ProductsApi;
import com.dekraChallenge.dekra_challenge.api.web.model.ProductResponse;
import com.dekraChallenge.dekra_challenge.api.web.model.ProductRequest;
import com.dekraChallenge.dekra_challenge.application.ProductService;
import com.dekraChallenge.dekra_challenge.application.ProductView;
import com.dekraChallenge.dekra_challenge.application.ProductViewService;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
public class ProductController implements ProductsApi {

    private final ProductService service;
    private final ProductViewService viewService;
    private final ProductWebStructMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public ProductController(ProductService service,
                             ProductViewService viewService,
                             ProductWebStructMapper mapper,
                             CurrentUserProvider currentUserProvider) {
        this.service = service;
        this.viewService = viewService;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public ResponseEntity<ProductResponse> createProduct(ProductRequest productRequest) {
        Product domain = mapper.toDomain(productRequest);
        ProductView created = viewService.create(domain);
        ProductResponse response = mapper.toResponse(created);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.product().getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<ProductResponse> getProductById(Long id) {
        ProductView view = viewService.getById(id);
        return ResponseEntity.ok(mapper.toResponse(view));
    }

    @Override
    public ResponseEntity<List<ProductResponse>> productsList(Long id, String name,
                                                             String description,
                                                             BigDecimal priceMin,
                                                             BigDecimal priceMax) {
        ProductFilter filter = new ProductFilter(id, name, description, priceMin, priceMax);
        List<ProductView> views = viewService.search(filter);
        return ResponseEntity.ok(mapper.toResponseList(views));
    }

    @Override
    public ResponseEntity<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {
        Product domain = mapper.toDomain(productRequest);
        ProductView updated = viewService.update(id, domain);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @Override
    public ResponseEntity<Void> deleteProduct(Long id) {
        service.delete(id, currentUserProvider.currentUsername());
        return ResponseEntity.noContent().build();
    }
}
