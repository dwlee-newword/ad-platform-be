package com.platform.ad.ad_platform.application.product;

import com.platform.ad.ad_platform.common.exception.ProductNotFoundException;
import com.platform.ad.ad_platform.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public record ProductResponse(Long id, String name, String description) {}

    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getDescription()))
                .toList();
    }

    public ProductResponse getProduct(Long id) {
        return productRepository.findById(id)
                .map(p -> new ProductResponse(p.getId(), p.getName(), p.getDescription()))
                .orElseThrow(ProductNotFoundException::new);
    }
}
