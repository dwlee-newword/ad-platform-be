package com.platform.ad.ad_platform.config;

import com.platform.ad.ad_platform.domain.company.Company;
import com.platform.ad.ad_platform.domain.company.CompanyRepository;
import com.platform.ad.ad_platform.domain.contract.Contract;
import com.platform.ad.ad_platform.domain.contract.ContractRepository;
import com.platform.ad.ad_platform.domain.contract.ContractStatus;
import com.platform.ad.ad_platform.domain.product.Product;
import com.platform.ad.ad_platform.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;
    private final ContractRepository contractRepository;

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

        if (contractRepository.count() == 0) {
            Company company1 = companyRepository.getReferenceById(10001L);
            Company company2 = companyRepository.getReferenceById(10002L);
            Company company3 = companyRepository.getReferenceById(10003L);
            Company company4 = companyRepository.getReferenceById(10004L);
            Company company5 = companyRepository.getReferenceById(10005L);
            Product product = productRepository.findAll().getFirst();

            contractRepository.saveAll(List.of(
                Contract.builder()
                    .company(company1)
                    .product(product)
                    .startDate(LocalDate.of(2026, 1, 1))
                    .endDate(LocalDate.of(2026, 3, 1))
                    .amount(500_000L)
                    .status(ContractStatus.COMPLETED)
                    .build(),
                Contract.builder()
                    .company(company2)
                    .product(product)
                    .startDate(LocalDate.of(2026, 3, 1))
                    .endDate(LocalDate.of(2026, 5, 1))
                    .amount(300_000L)
                    .status(ContractStatus.IN_PROGRESS)
                    .build(),
                Contract.builder()
                    .company(company3)
                    .product(product)
                    .startDate(LocalDate.of(2026, 5, 1))
                    .endDate(LocalDate.of(2026, 7, 1))
                    .amount(200_000L)
                    .status(ContractStatus.PENDING)
                    .build(),
                Contract.builder()
                    .company(company4)
                    .product(product)
                    .startDate(LocalDate.of(2026, 2, 1))
                    .endDate(LocalDate.of(2026, 4, 1))
                    .amount(150_000L)
                    .status(ContractStatus.CANCELLED)
                    .build(),
                Contract.builder()
                    .company(company5)
                    .product(product)
                    .startDate(LocalDate.of(2026, 1, 15))
                    .endDate(LocalDate.of(2026, 3, 15))
                    .amount(250_000L)
                    .status(ContractStatus.FAILED)
                    .build()
            ));
        }
    }
}
