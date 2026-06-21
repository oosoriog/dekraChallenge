package com.dekraChallenge.dekra_challenge.application;

import com.dekraChallenge.dekra_challenge.config.TaxProperties;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductFilter;
import com.dekraChallenge.dekra_challenge.domain.tax.CalculatedTax;
import com.dekraChallenge.dekra_challenge.domain.tax.TaxCalculatorResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductViewService {

    private final ProductService productService;
    private final TaxCalculatorResolver taxResolver;
    private final TaxProperties taxProperties;

    public ProductViewService(ProductService productService,
                              TaxCalculatorResolver taxResolver,
                              TaxProperties taxProperties) {
        this.productService = productService;
        this.taxResolver = taxResolver;
        this.taxProperties = taxProperties;
    }

    public ProductView create(Product product) {
        return toView(productService.create(product));
    }

    public ProductView getById(Long id) {
        return toView(productService.getById(id));
    }

    public ProductView update(Long id, Product changes) {
        return toView(productService.update(id, changes));
    }

    public List<ProductView> search(ProductFilter filter) {
        return productService.search(filter).stream()
                .map(this::toView)
                .toList();
    }

    private ProductView toView(Product product) {
        CalculatedTax tax = taxResolver.calculate(taxProperties.getType(), product.getPrice());
        log.debug("Tax calculated for product id={}: type={}, rate={}%, amount={}, total={}",
                product.getId(), tax.type(), tax.percentage(), tax.amount(), tax.priceWithTax());
        return new ProductView(product, tax);
    }
}

