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
            for (int i = 1; i <= 14; i++) {
                productRepository.save(
                        Product.builder()
                                .name("노출 보장형 광고 " + i)
                                .description("고객이 활발하게 탐색하는 페이지에 숙소를 동적으로 노출하도록 최적화")
                                .build()
                );
            }
        }

        if (contractRepository.count() == 0) {
            Company company1 = companyRepository.getReferenceById(10001L);
            Company company2 = companyRepository.getReferenceById(10002L);
            Company company3 = companyRepository.getReferenceById(10003L);
            Company company4 = companyRepository.getReferenceById(10004L);
            Company company5 = companyRepository.getReferenceById(10005L);
            Company company6 = companyRepository.getReferenceById(10006L);
            List<Product> products = productRepository.findAll();

            contractRepository.saveAll(List.of(
                    // company1 — 놀유니버스 그랜드 호텔 (3건)
                    Contract.builder()
                            .company(company1).product(products.get(0))
                            .startDate(LocalDate.of(2025, 10, 1)).endDate(LocalDate.of(2025, 12, 31))
                            .amount(800_000L).status(ContractStatus.COMPLETED)
                            .contractNumber("AD-20250920-001").build(),
                    Contract.builder()
                            .company(company1).product(products.get(1))
                            .startDate(LocalDate.of(2026, 1, 1)).endDate(LocalDate.of(2026, 3, 1))
                            .amount(500_000L).status(ContractStatus.COMPLETED)
                            .contractNumber("AD-20251210-001").build(),
                    Contract.builder()
                            .company(company1).product(products.get(2))
                            .startDate(LocalDate.of(2026, 4, 1)).endDate(LocalDate.of(2026, 6, 30))
                            .amount(700_000L).status(ContractStatus.PENDING)
                            .contractNumber("AD-20260310-001").build(),

                    // company2 — 놀유니버스 시티 호텔 강남 (3건)
                    Contract.builder()
                            .company(company2).product(products.get(0))
                            .startDate(LocalDate.of(2025, 11, 1)).endDate(LocalDate.of(2026, 1, 31))
                            .amount(450_000L).status(ContractStatus.COMPLETED)
                            .contractNumber("AD-20251015-001").build(),
                    Contract.builder()
                            .company(company2).product(products.get(3))
                            .startDate(LocalDate.of(2026, 3, 1)).endDate(LocalDate.of(2026, 5, 1))
                            .amount(300_000L).status(ContractStatus.IN_PROGRESS)
                            .contractNumber("AD-20260220-001").build(),
                    Contract.builder()
                            .company(company2).product(products.get(4))
                            .startDate(LocalDate.of(2026, 2, 1)).endDate(LocalDate.of(2026, 4, 30))
                            .amount(200_000L).status(ContractStatus.CANCELLED)
                            .contractNumber("AD-20260125-001").build(),

                    // company3 — 놀유니버스 오션뷰 호텔 (4건)
                    Contract.builder()
                            .company(company3).product(products.get(1))
                            .startDate(LocalDate.of(2025, 9, 1)).endDate(LocalDate.of(2025, 11, 30))
                            .amount(600_000L).status(ContractStatus.COMPLETED)
                            .contractNumber("AD-20250825-001").build(),
                    Contract.builder()
                            .company(company3).product(products.get(2))
                            .startDate(LocalDate.of(2026, 1, 10)).endDate(LocalDate.of(2026, 3, 10))
                            .amount(350_000L).status(ContractStatus.IN_PROGRESS)
                            .contractNumber("AD-20260105-001").build(),
                    Contract.builder()
                            .company(company3).product(products.get(5))
                            .startDate(LocalDate.of(2026, 5, 1)).endDate(LocalDate.of(2026, 7, 1))
                            .amount(200_000L).status(ContractStatus.PENDING)
                            .contractNumber("AD-20260312-001").build(),
                    Contract.builder()
                            .company(company3).product(products.get(6))
                            .startDate(LocalDate.of(2025, 12, 1)).endDate(LocalDate.of(2026, 1, 31))
                            .amount(120_000L).status(ContractStatus.CANCELLED)
                            .contractNumber("AD-20251120-001").build(),

                    // company4 — 포레스트 강남 펜션 (3건)
                    Contract.builder()
                            .company(company4).product(products.get(0))
                            .startDate(LocalDate.of(2025, 8, 1)).endDate(LocalDate.of(2025, 10, 31))
                            .amount(180_000L).status(ContractStatus.COMPLETED)
                            .contractNumber("AD-20250720-001").build(),
                    Contract.builder()
                            .company(company4).product(products.get(3))
                            .startDate(LocalDate.of(2026, 2, 1)).endDate(LocalDate.of(2026, 4, 1))
                            .amount(150_000L).status(ContractStatus.IN_PROGRESS)
                            .contractNumber("AD-20260128-001").build(),
                    Contract.builder()
                            .company(company4).product(products.get(7))
                            .startDate(LocalDate.of(2026, 4, 15)).endDate(LocalDate.of(2026, 6, 15))
                            .amount(220_000L).status(ContractStatus.PENDING)
                            .contractNumber("AD-20260314-001").build(),

                    // company5 — 포레스트 서초 펜션 (4건)
                    Contract.builder()
                            .company(company5).product(products.get(2))
                            .startDate(LocalDate.of(2025, 7, 1)).endDate(LocalDate.of(2025, 9, 30))
                            .amount(90_000L).status(ContractStatus.COMPLETED)
                            .contractNumber("AD-20250625-001").build(),
                    Contract.builder()
                            .company(company5).product(products.get(4))
                            .startDate(LocalDate.of(2026, 1, 15)).endDate(LocalDate.of(2026, 3, 15))
                            .amount(250_000L).status(ContractStatus.IN_PROGRESS)
                            .contractNumber("AD-20260110-001").build(),
                    Contract.builder()
                            .company(company5).product(products.get(8))
                            .startDate(LocalDate.of(2026, 3, 20)).endDate(LocalDate.of(2026, 5, 20))
                            .amount(310_000L).status(ContractStatus.PENDING)
                            .contractNumber("AD-20260301-001").build(),
                    Contract.builder()
                            .company(company5).product(products.get(9))
                            .startDate(LocalDate.of(2025, 11, 1)).endDate(LocalDate.of(2025, 12, 31))
                            .amount(100_000L).status(ContractStatus.CANCELLED)
                            .contractNumber("AD-20251028-001").build(),

                    // company6 — 포레스트 구로 펜션 (3건)
                    Contract.builder()
                            .company(company6).product(products.get(1))
                            .startDate(LocalDate.of(2025, 10, 15)).endDate(LocalDate.of(2025, 12, 15))
                            .amount(130_000L).status(ContractStatus.COMPLETED)
                            .contractNumber("AD-20251001-001").build(),
                    Contract.builder()
                            .company(company6).product(products.get(5))
                            .startDate(LocalDate.of(2026, 2, 10)).endDate(LocalDate.of(2026, 4, 10))
                            .amount(170_000L).status(ContractStatus.IN_PROGRESS)
                            .contractNumber("AD-20260205-001").build(),
                    Contract.builder()
                            .company(company6).product(products.get(10))
                            .startDate(LocalDate.of(2026, 5, 1)).endDate(LocalDate.of(2026, 7, 31))
                            .amount(990_000L).status(ContractStatus.PENDING)
                            .contractNumber("AD-20260315-001").build()
            ));
        }
    }
}
