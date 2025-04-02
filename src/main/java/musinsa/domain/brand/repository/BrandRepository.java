package musinsa.domain.brand.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import musinsa.domain.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

  @Query("SELECT b FROM Brand b WHERE b.name = :brandName")
  Optional<Brand> findByBrandName(@Param("brandName") String brandName);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT b FROM Brand b WHERE b.id = :id")
  Optional<Brand> findByIdForUpdate(@Param("id") Long id);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
