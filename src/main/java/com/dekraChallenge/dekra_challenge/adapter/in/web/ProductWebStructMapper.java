package com.dekraChallenge.dekra_challenge.adapter.in.web;

import com.dekraChallenge.dekra_challenge.api.web.model.ProductRequest;
import com.dekraChallenge.dekra_challenge.api.web.model.ProductResponse;
import com.dekraChallenge.dekra_challenge.domain.model.Product;
import com.dekraChallenge.dekra_challenge.domain.tax.CalculatedTax;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductWebStructMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "nombre")
    @Mapping(target = "description", source = "descripcion")
    @Mapping(target = "price", source = "precio")
    Product toDomain(ProductRequest request);

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "nombre", source = "product.name")
    @Mapping(target = "descripcion", source = "product.description")
    @Mapping(target = "precio", source = "product.price")
    @Mapping(target = "tipoImpuesto", source = "tax.type")
    @Mapping(target = "porcentajeImpuesto", source = "tax.percentage")
    @Mapping(target = "importeImpuesto", source = "tax.amount")
    @Mapping(target = "precioConImpuesto", source = "tax.priceWithTax")
    ProductResponse toResponse(Product product, CalculatedTax tax);
}
