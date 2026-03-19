package com.platform.ad.ad_platform.web;

import com.platform.ad.ad_platform.application.product.ProductService;
import com.platform.ad.ad_platform.application.product.ProductService.ProductResponse;
import com.platform.ad.ad_platform.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Products", description = "광고 상품 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final int PRODUCT_PAGE_SIZE = 8;

    private final ProductService productService;

    @Operation(summary = "상품 전체 조회")
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(productService.getProducts(
                PageRequest.of(page, PRODUCT_PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"))
        ));
    }

    @Operation(summary = "상품 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }
}
