package com.dekraChallenge.dekra_challenge.config;

import com.dekraChallenge.dekra_challenge.application.ProductService;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.model.ProductFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true", matchIfMissing = true)
public class DemoDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataInitializer.class);

    private final ProductService productService;

    public DemoDataInitializer(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!productService.search(new ProductFilter(null, null, null, null, null)).isEmpty()) {
            log.info("Demo data already present, skipping seed.");
            return;
        }

        log.info("Seeding demo products for Docker profile...");

        productService.create(Product.of(
                "Paquete de leche",
                "8 botes de leche",
                new BigDecimal("10.00")
        ));

        productService.create(Product.of(
                "Café",
                "Paquete de café",
                new BigDecimal("4.50")
        ));

        productService.create(Product.of(
                "Cuaderno",
                "Cuaderno mediano",
                new BigDecimal("2.30")
        ));

        productService.create(Product.of(
                "Botella de agua",
                "Botella pequeña",
                new BigDecimal("1.20")
        ));

        productService.create(Product.of(
                "Bolígrafo",
                "Bolígrafo BIC",
                new BigDecimal("0.80")
        ));

        log.info("Demo product seed complete: 4 products created.");
    }
}
