package musinsa.domain.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductUpsertRequest(
    @JsonProperty("productId") Long productId,
    @JsonProperty("name") String name,
    @JsonProperty("price") int price,
    @JsonProperty("brand") String brand,
    @JsonProperty("category") String category
) {}