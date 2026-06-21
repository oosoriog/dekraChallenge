package com.dekraChallenge.dekra_challenge.application;

import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.tax.CalculatedTax;

public record ProductView(Product product, CalculatedTax tax) {
}

