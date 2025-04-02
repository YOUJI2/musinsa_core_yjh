package musinsa.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import musinsa.common.model.BaseEntity;
import musinsa.domain.brand.entity.Brand;
import musinsa.domain.category.entity.Category;

@Getter
@Builder
@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  @Column
  private Integer price;

  @ManyToOne(fetch = FetchType.LAZY)
  private Brand brand;

  @ManyToOne(fetch = FetchType.LAZY)
  private Category category;

  public void updateProductInfo(Brand brand, Category category, int price, String name) {
    this.brand = brand;
    this.category = category;
    this.name = name;
    this.price = price;
  }

  public static Product create(String name, Brand brand, Category category, int price) {
    return Product.builder()
        .name(name)
        .brand(brand)
        .category(category)
        .price(price)
        .build();
  }
}
