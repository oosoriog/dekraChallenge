package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.api.web.model.ProductResponse;
import com.dekraChallenge.dekra_challenge.api.web.model.ProductRequest;
import com.dekraChallenge.dekra_challenge.config.TaxProperties;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.tax.CalculatedTax;
import com.dekraChallenge.dekra_challenge.domain.tax.TaxCalculatorResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ProductWebMapper {

    private final TaxCalculatorResolver resolver;
    private final TaxProperties taxProperties;
    private final ProductWebStructMapper structMapper;

    public ProductWebMapper(TaxCalculatorResolver resolver,
                            TaxProperties taxProperties,
                            ProductWebStructMapper structMapper) {
        this.resolver = resolver;
        this.taxProperties = taxProperties;
        this.structMapper = structMapper;
    }

    public Product toDomain(ProductRequest req) {
        return structMapper.toDomain(req);
    }

    public ProductResponse toResponse(Product p) {
        CalculatedTax tax = resolver.calculate(taxProperties.getType(), p.getPrice());
        log.debug("Tax calculated for product id={}: type={}, rate={}%, amount={}, total={}",
                p.getId(), tax.type(), tax.percentage(), tax.amount(), tax.priceWithTax());
        return structMapper.toResponse(p, tax);
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream().map(this::toResponse).toList();
    }
}
