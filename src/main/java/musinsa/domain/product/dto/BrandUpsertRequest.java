package musinsa.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BrandUpsertRequest(
    @JsonProperty("brandId") Long brandId,
    @JsonProperty("brand") String brand
) {}
