package musinsa.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import musinsa.common.model.ResponseObject;
import musinsa.common.util.ResponseUtils;
import musinsa.domain.product.dto.BrandUpsertRequest;
import musinsa.domain.product.dto.ProductUpsertRequest;
import musinsa.service.AdminService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "어드민용 API", description = "새로운 브랜드 등록과 함께, 각 브랜드의 상품 추가, 수정 및 삭제 기능을 제공합니다.")
@RequestMapping(value = "/api/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

  private final AdminService adminService;

  /**
   *  구현 4-1) 상품 저장 및 수정 API
   *  @RequestBody productUpsertRequest
   */
  @Operation(summary = "상품 저장 및 수정", description = "상품 ID유무로 생성 수정을 분기하여 처리")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "productId : 상품 저장 및 수정 요청 (productId가 null 또는 생략되면 생성, 존재하면 수정)\n"
                  + "name : 생성 or 수정할 상품 이름(중복시 예외처리)\n"
                  + "price : 상품 가격\n"
                  + "brand : 브랜드 이름(기존의 이름은 resources/musinda-data.sql 파일을 참고해주세요.)\n"
                  + "category : 카테고리는 대문자로 입력해주세요(ex TOP, OUTER, PANTS, SNEAKERS, BAG, HAT, SOCKS, ACCESSORY)\n"
      ,
      required = true
  )
  @PostMapping("/product/upsert")
  public ResponseEntity<ResponseObject<Void>> createOrUpdateProduct(@RequestBody ProductUpsertRequest productUpsertRequest) {
    adminService.upsertProduct(productUpsertRequest);
    return ResponseUtils.createResponseEntityBySuccess();
  }

  /**
   *  구현 4-2) 상품 삭제 API
   *  @PathVariable productId
   */
  @Operation(summary = "상품 삭제", description = "상품 ID로 해당 정보를 삭제")
  @Parameter(name = "productId", description = "상품 ID의 경우 resources/musinda-data.sql 파일을 참고해주세요.")
  @DeleteMapping("/product/delete/{productId}")
  public ResponseEntity<ResponseObject<Void>> deleteProduct(@PathVariable String productId) {
    adminService.deleteProduct(Long.parseLong(productId));
    return ResponseUtils.createResponseEntityBySuccess();
  }

  /**
   *  구현 4-3) 브랜드 저장 및 수정 API
   * @RequestBody brandUpsertRequest
   */
  @Operation(summary = "브랜드 저장 및 수정", description = "브랜드 ID유무로 생성 수정을 분기하여 처리")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "brand : 브랜드 저장 및 수정 요청 (brandId가 null 또는 생략되면 생성, 존재하면 수정)\n"
                  + "name : 생성 or 수정할 브랜드 이름(중복시 예외처리)\n"
      ,
      required = true
  )
  @PostMapping("/brand/upsert")
  public ResponseEntity<ResponseObject<Void>> createOrUpdateBrand(@RequestBody BrandUpsertRequest brandUpsertRequest) {
    adminService.upsertBrand(brandUpsertRequest);
    return ResponseUtils.createResponseEntityBySuccess();
  }

  /**
   *  구현 4-4) 브랜드 삭제 API
   *  @PathVariable brandId
   */
  @Operation(summary = "브랜드 삭제", description = "브랜드 ID로 해당 정보를 삭제 및 관련 상품들도 모두 제거")
  @Parameter(name = "brandId", description = "브랜드 ID의 경우 resources/musinda-data.sql 파일을 참고해주세요.")
  @DeleteMapping("/brand/delete/{brandId}")
  public ResponseEntity<ResponseObject<Void>> deleteBrand(@PathVariable String brandId) {
    adminService.deleteBrand(Long.parseLong(brandId));
    return ResponseUtils.createResponseEntityBySuccess();
  }
}
