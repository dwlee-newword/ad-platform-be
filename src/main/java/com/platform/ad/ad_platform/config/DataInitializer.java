package com.platform.ad.ad_platform.config;

import com.platform.ad.ad_platform.domain.company.Company;
import com.platform.ad.ad_platform.domain.company.CompanyRepository;
import com.platform.ad.ad_platform.domain.product.Product;
import com.platform.ad.ad_platform.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (companyRepository.count() == 0) {
            companyRepository.saveAll(List.of(
                Company.builder().id(10001L).name("놀유니버스 그랜드 호텔").type("호텔").build(),
                Company.builder().id(10002L).name("놀유니버스 시티 호텔 강남").type("호텔").build(),
                Company.builder().id(10003L).name("놀유니버스 오션뷰 호텔").type("호텔").build(),
                Company.builder().id(10004L).name("포레스트 강남 펜션").type("펜션").build(),
                Company.builder().id(10005L).name("포레스트 서초 펜션").type("펜션").build(),
                Company.builder().id(10006L).name("포레스트 구로 펜션").type("펜션").build()
            ));
        }

        if (productRepository.count() == 0) {
            productRepository.save(
                Product.builder()
                    .name("노출 보장형 광고")
                    .description("고객이 활발하게 탐색하는 페이지에 숙소를 동적으로 노출하도록 최적화")
                    .build()
            );
        }
    }
}
