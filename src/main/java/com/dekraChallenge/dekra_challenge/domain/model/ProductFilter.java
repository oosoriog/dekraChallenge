package com.dekraChallenge.dekra_challenge.domain.model;

import java.math.BigDecimal;

public record ProductFilter(
        Long id,
        String name,
        String description,
        BigDecimal priceMin,
        BigDecimal priceMax) {
}
