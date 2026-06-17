package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.api.web.model.ProductResponse;
import com.dekraChallenge.dekra_challenge.api.web.model.ProductRequest;
import com.dekraChallenge.dekra_challenge.application.ProductView;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductWebMapper {

    private final ProductWebStructMapper structMapper;

    public ProductWebMapper(ProductWebStructMapper structMapper) {
        this.structMapper = structMapper;
    }

    public Product toDomain(ProductRequest req) {
        return structMapper.toDomain(req);
    }

    public ProductResponse toResponse(ProductView view) {
        return structMapper.toResponse(view.product(), view.tax());
    }

    public List<ProductResponse> toResponseList(List<ProductView> views) {
        return views.stream().map(this::toResponse).toList();
    }
}
