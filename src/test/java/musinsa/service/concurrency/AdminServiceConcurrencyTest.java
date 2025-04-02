package musinsa.service.concurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ErrorCode;
import musinsa.domain.brand.entity.Brand;
import musinsa.domain.brand.repository.BrandRepository;
import musinsa.domain.category.entity.Category;
import musinsa.domain.category.repository.CategoryRepository;
import musinsa.domain.product.dto.BrandUpsertRequest;
import musinsa.domain.product.dto.ProductUpsertRequest;
import musinsa.domain.product.entity.Product;
import musinsa.domain.product.repository.ProductRepository;
import musinsa.service.AdminService;
import musinsa.util.ConcurrentTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminServiceConcurrencyTest {

  @Autowired
  private AdminService adminService;

  @Autowired
  private BrandRepository brandRepository;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private ProductRepository productRepository;

  private Long productId;

  @BeforeEach
  void setup() {
    Product product = productRepository.findAll().get(0);
    productId = product.getId();
  }

  @Test
  void 상품_동시생성_테스트() throws InterruptedException {
    int threadCount = 10;
    String productName = "test_product";
    ProductUpsertRequest request = new ProductUpsertRequest(
        null,
        productName,
        500,
        "A",
        "TOP"
        );

    List<Boolean> results = ConcurrentTestUtils.concurrentTaskWithResult(threadCount, () -> {
      try {
        adminService.upsertProduct(request);
        return true;
      } catch (BusinessException e) {
        return false;
      }
    });

    long successCount = results.stream().filter(Boolean::booleanValue).count();
    assertEquals(1, successCount, "여러 요청시 단, 하나의 상품만 생성되어야 한다.");
  }

  @Test
  void 상품_동시수정_테스트() throws InterruptedException {
    int threadCount = 10;
    String updateName = "concurrent-update";
    List<Integer> prices = IntStream.range(0, threadCount).map(i -> 500 + i).boxed().toList();

    ConcurrentTestUtils.concurrentTask(threadCount, () -> {
      int price = prices.get(ThreadLocalRandom.current().nextInt(threadCount)); // 변경 가격 가져오기

      ProductUpsertRequest request = new ProductUpsertRequest(productId, updateName, price, "A", "TOP");
      adminService.upsertProduct(request);
    });

    // 마지막 저장된 상품 조회
    Product updatedProduct = productRepository.findById(productId)
        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

    assertEquals(updateName, updatedProduct.getName());
    assertTrue(prices.contains(updatedProduct.getPrice()), "최종 가격은 입력 가격 중 하나여야 함");
  }

  @Test
  void 상품_동시삭제_테스트() throws InterruptedException {
    // 1. 상품 생성
    Brand brand = brandRepository.findById(1L).get();
    Category category = categoryRepository.findById(1L).get();
    Product product = Product.create("delete_product", brand, category, 3000000);

    // 2. 저장
    Product savedProduct = productRepository.save(product);
    Long productId = savedProduct.getId();

    // 2. 동시 삭제 테스트
    List<Boolean> results = ConcurrentTestUtils.concurrentTaskWithResult(10, () -> {
      try {
        adminService.deleteProduct(productId);
        return true;
      } catch (BusinessException e) {
        return false;
      }
    });

    long successCount = results.stream().filter(Boolean::booleanValue).count();
    assertEquals(1, successCount, "하나의 요청만 삭제되고 나머지는 무시 되어야 함");
  }

  @Test
  void 브랜드_동시생성_테스트() throws InterruptedException {
    BrandUpsertRequest request = new BrandUpsertRequest(null, "new_musinsa");

    List<Boolean> results = ConcurrentTestUtils.concurrentTaskWithResult(10, () -> {
      try {
        adminService.upsertBrand(request);
        return true;
      } catch (Exception e) {
        return false;
      }
    });

    long successCount = results.stream().filter(Boolean::booleanValue).count();
    assertEquals(1, successCount, "여러 요청시 단, 하나의 브랜드만 생성되어야 한다.");
  }

  @Test
  void 브랜드_동시수정_테스트() throws InterruptedException {
    Brand savedBrand = brandRepository.save(Brand.create("musinsa"));

    List<Boolean> results = ConcurrentTestUtils.concurrentTaskWithResult(10, () -> {
      try {
        adminService.upsertBrand(new BrandUpsertRequest(savedBrand.getId(), "musinsa_renewal"));
        return true;
      } catch (Exception e) {
        return false;
      }
    });

    long successCount = results.stream().filter(Boolean::booleanValue).count();
    assertTrue(successCount >= 1);
  }

  @Test
  void 브랜드_동시삭제_테스트() throws InterruptedException {
    Brand brand = Brand.create("mmusinsa");
    brand = brandRepository.save(brand);
    Long brandId = brand.getId();

    List<Boolean> results = ConcurrentTestUtils.concurrentTaskWithResult(10, () -> {
      try {
        adminService.deleteBrand(brandId);
        return true;
      } catch (Exception e) {
        return false;
      }
    });

    long successCount = results.stream().filter(Boolean::booleanValue).count();
    assertEquals(1, successCount, "하나의 요청만 삭제되고 나머지는 무시 되어야 함");
  }
}
