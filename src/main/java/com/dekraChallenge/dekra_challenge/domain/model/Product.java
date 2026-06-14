package com.dekraChallenge.dekra_challenge.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Product {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;

    public Product(Long id, String name, String description, BigDecimal price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (price == null) {
            throw new IllegalArgumentException("price must not be null");
        }
        if (price.signum() < 0) {
            throw new IllegalArgumentException("price must be greater than or equal to 0");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public static Product of(String name, String description, BigDecimal price) {
        return new Product(null, name, description, price);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product other)) {
            return false;
        }
        return Objects.equals(id, other.id)
                && Objects.equals(name, other.name)
                && Objects.equals(description, other.description)
                && (price == null ? other.price == null
                        : other.price != null && price.compareTo(other.price) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price == null ? null : price.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return "Product{id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", price=" + price + '}';
    }
}
