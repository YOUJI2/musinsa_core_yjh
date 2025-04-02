package musinsa.domain.category.repository;

import java.util.Optional;
import musinsa.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  @Query("SELECT c FROM Category c WHERE c.name = :categoryName")
  Optional<Category> findByCategory(@Param("categoryName") String categoryName);
}
