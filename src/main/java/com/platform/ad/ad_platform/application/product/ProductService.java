package com.platform.ad.ad_platform.application.product;

import com.platform.ad.ad_platform.common.exception.ProductNotFoundException;
import com.platform.ad.ad_platform.common.response.PageResponse;
import com.platform.ad.ad_platform.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public record ProductResponse(Long id, String name, String description) {}

    public PageResponse<ProductResponse> getProducts(Pageable pageable) {
        return PageResponse.of(
                productRepository.findAll(pageable)
                        .map(p -> new ProductResponse(p.getId(), p.getName(), p.getDescription()))
        );
    }

    public ProductResponse getProduct(Long id) {
        return productRepository.findById(id)
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getDescription()))
                .orElseThrow(ProductNotFoundException::new);
    }
}
