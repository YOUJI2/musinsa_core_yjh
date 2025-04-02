package musinsa.domain.brand.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import musinsa.common.model.BaseEntity;
import musinsa.domain.product.entity.Product;

@Getter
@Builder
@Entity
@Table(name = "brand")
@AllArgsConstructor
@NoArgsConstructor
public class Brand extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
  private List<Product> products = new ArrayList<>();

  public void updateBrandName(String brandName) {
    this.name = brandName;
  }

  public static Brand create(String brandName) {
    return Brand.builder().name(brandName).build();
  }

  public static Brand create(Long id, String name) {
    return Brand.builder().id(id).name(name).build();
  }
}
