# Object Mother / Test Data Builder Template

Use test data helpers only when setup is duplicated or noisy.

## Object Mother Example

```java
final class ProductoMother {

  private ProductoMother() {
  }

  static Producto validProduct() {
    return new Producto(1L, "Laptop", "Development laptop", new BigDecimal("1200.00"));
  }

  static Producto productWithPrice(String price) {
    return new Producto(1L, "Laptop", "Development laptop", new BigDecimal(price));
  }
}
```

## Builder Example

```java
final class ProductoTestBuilder {

  private Long id = 1L;
  private String nombre = "Laptop";
  private String descripcion = "Development laptop";
  private BigDecimal precio = new BigDecimal("1200.00");

  ProductoTestBuilder withNombre(String nombre) {
    this.nombre = nombre;
    return this;
  }

  ProductoTestBuilder withPrecio(BigDecimal precio) {
    this.precio = precio;
    return this;
  }

  Producto build() {
    return new Producto(id, nombre, descripcion, precio);
  }
}
```

Keep helpers under `src/test/java`.
