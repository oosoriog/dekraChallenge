package com.dekraChallenge.dekra_challenge.config;

import com.dekraChallenge.dekra_challenge.domain.tax.ItbisTaxCalculator;
import com.dekraChallenge.dekra_challenge.domain.tax.IvaTaxCalculator;
import com.dekraChallenge.dekra_challenge.domain.tax.TaxCalculatorResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties(TaxProperties.class)
public class TaxConfig {

    @Bean
    public IvaTaxCalculator calculadorDeImpuestosIVA() {
        return new IvaTaxCalculator();
    }

    @Bean
    public ItbisTaxCalculator calculadorDeImpuestosITBIS() {
        return new ItbisTaxCalculator();
    }

    @Bean
    public TaxCalculatorResolver taxCalculatorResolver(
            IvaTaxCalculator iva,
            ItbisTaxCalculator itbis) {
        return new TaxCalculatorResolver(List.of(iva, itbis));
    }
}
