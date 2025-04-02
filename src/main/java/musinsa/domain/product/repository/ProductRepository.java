package musinsa.domain.product.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import musinsa.domain.brand.entity.Brand;
import musinsa.domain.category.entity.Category;
import musinsa.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT p FROM Product p JOIN FETCH p.brand JOIN FETCH p.category WHERE p.category = :category ORDER BY p.price")
  List<Product> findAllByCategoryOrderByPrice(@Param("category") Category category);

  @Query("SELECT COUNT(p) = 0 FROM Product p WHERE p.category = :category AND p.price < :price")
  boolean isLowestPriceInCategory(@Param("category") Category category, @Param("price") int price);

  @Query("SELECT COUNT(p) = 0 FROM Product p WHERE p.category = :category AND p.price > :price")
  boolean isHighestPriceInCategory(@Param("category") Category category, @Param("price") int price);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Product p WHERE p.id = :id")
  Optional<Product> findByIdForUpdate(@Param("id") Long id);

  @Query("SELECT p.id FROM Product p WHERE p.brand = :brand")
  List<Long> findProductIdListByBrand(@Param("brand") Brand brand);

  @Modifying
  @Query("DELETE FROM Product p WHERE p.id IN :idList")
  void deleteByProductIdList(@Param("ids") List<Long> idList); //네이티브 쿼리로 일괄 제거(JPA X)

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
