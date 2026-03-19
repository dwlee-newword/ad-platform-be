package com.platform.ad.ad_platform.application.contract;

import com.platform.ad.ad_platform.application.contract.dto.ContractCreateRequest;
import com.platform.ad.ad_platform.application.contract.dto.ContractDetailResponse;
import com.platform.ad.ad_platform.common.exception.BusinessException;
import com.platform.ad.ad_platform.common.exception.ErrorCode;
import com.platform.ad.ad_platform.common.response.PageResponse;
import com.platform.ad.ad_platform.domain.company.Company;
import com.platform.ad.ad_platform.domain.company.CompanyRepository;
import com.platform.ad.ad_platform.domain.contract.ContractStatus;
import com.platform.ad.ad_platform.domain.product.Product;
import com.platform.ad.ad_platform.domain.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("ContractService 통합 테스트")
class ContractServiceIntegrationTest {

    @Autowired
    ContractService contractService;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    ProductRepository productRepository;

    Company company;
    Product product;

    @BeforeEach
    void setUp() {
        // 시드 업체 사용: 오늘 날짜 기준 count=3 → contractNumber "AD-YYYYMMDD-004"부터 생성 (시드 데이터와 충돌 없음)
        company = companyRepository.findById(10001L).orElseThrow();
        product = productRepository.save(
                Product.builder().name("테스트 상품").description("설명").build()
        );
    }

    // ───────────────────────────────────────────────────
    // 계약 생성
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("계약 생성")
    class CreateContract {

        @Test
        @DisplayName("정상 생성 — 응답에 contractNumber 포함, 상태는 시작일 기준으로 도출")
        void success() {
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = startDate.plusDays(28);

            ContractDetailResponse response = contractService.createContract(
                    new ContractCreateRequest(company.getId(), product.getId(), startDate, endDate, 100_000L)
            );

            assertThat(response.contractNumber()).startsWith("AD-");
            assertThat(response.companyName()).isEqualTo(company.getName());
            assertThat(response.status()).isEqualTo(ContractStatus.PENDING);
        }

        @Test
        @DisplayName("존재하지 않는 업체 ID — COMPANY_NOT_FOUND 예외")
        void fail_companyNotFound() {
            ContractCreateRequest request = new ContractCreateRequest(
                    999L, product.getId(),
                    LocalDate.now().plusDays(1), LocalDate.now().plusDays(29),
                    100_000L
            );

            assertThatThrownBy(() -> contractService.createContract(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.COMPANY_NOT_FOUND));
        }

        @Test
        @DisplayName("존재하지 않는 상품 ID — PRODUCT_NOT_FOUND 예외")
        void fail_productNotFound() {
            ContractCreateRequest request = new ContractCreateRequest(
                    company.getId(), 999L,
                    LocalDate.now().plusDays(1), LocalDate.now().plusDays(29),
                    100_000L
            );

            assertThatThrownBy(() -> contractService.createContract(request))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND));
        }

        @Test
        @DisplayName("동일 업체·상품에 기간이 겹치는 계약 — DUPLICATE_CONTRACT 예외")
        void fail_duplicateContract() {
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = startDate.plusDays(28);

            contractService.createContract(
                    new ContractCreateRequest(company.getId(), product.getId(), startDate, endDate, 100_000L)
            );

            // 기간이 겹치는 두 번째 계약
            ContractCreateRequest overlapping = new ContractCreateRequest(
                    company.getId(), product.getId(),
                    startDate.plusDays(7), endDate.plusDays(7),
                    100_000L
            );

            assertThatThrownBy(() -> contractService.createContract(overlapping))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.DUPLICATE_CONTRACT));
        }

        @Test
        @DisplayName("취소된 계약과 기간이 겹쳐도 정상 생성 가능")
        void success_afterCancelledOverlapping() {
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = startDate.plusDays(28);

            ContractDetailResponse first = contractService.createContract(
                    new ContractCreateRequest(company.getId(), product.getId(), startDate, endDate, 100_000L)
            );
            contractService.cancelContract(first.id());

            // 취소 후 같은 기간에 재계약 가능
            assertThatNoException().isThrownBy(() -> contractService.createContract(
                    new ContractCreateRequest(company.getId(), product.getId(), startDate, endDate, 100_000L)
            ));
        }
    }

    // ───────────────────────────────────────────────────
    // 계약 단건 조회
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("계약 단건 조회")
    class GetContract {

        @Test
        @DisplayName("존재하는 계약 ID — 상세 정보 반환")
        void success() {
            ContractDetailResponse created = contractService.createContract(
                    new ContractCreateRequest(
                            company.getId(), product.getId(),
                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(29),
                            100_000L
                    )
            );

            ContractDetailResponse found = contractService.getContract(created.id());

            assertThat(found.id()).isEqualTo(created.id());
            assertThat(found.contractNumber()).isEqualTo(created.contractNumber());
        }

        @Test
        @DisplayName("존재하지 않는 계약 ID — CONTRACT_NOT_FOUND 예외")
        void fail_contractNotFound() {
            assertThatThrownBy(() -> contractService.getContract(Long.MAX_VALUE))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.CONTRACT_NOT_FOUND));
        }
    }

    // ───────────────────────────────────────────────────
    // 계약 취소
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("계약 취소")
    class CancelContract {

        @Test
        @DisplayName("PENDING 계약 취소 — 상태가 CANCELLED로 변경")
        void success_cancelPending() {
            ContractDetailResponse created = contractService.createContract(
                    new ContractCreateRequest(
                            company.getId(), product.getId(),
                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(29),
                            100_000L
                    )
            );

            ContractDetailResponse cancelled = contractService.cancelContract(created.id());

            assertThat(cancelled.status()).isEqualTo(ContractStatus.CANCELLED);
        }

        @Test
        @DisplayName("존재하지 않는 계약 취소 — CONTRACT_NOT_FOUND 예외")
        void fail_contractNotFound() {
            assertThatThrownBy(() -> contractService.cancelContract(Long.MAX_VALUE))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.CONTRACT_NOT_FOUND));
        }
    }

    // ───────────────────────────────────────────────────
    // 계약 목록 조회 (필터링 + 페이지네이션)
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("계약 목록 조회")
    class GetContracts {

        @Test
        @DisplayName("업체 ID 필터 — 해당 업체 계약만 반환")
        void filter_byCompanyId() {
            LocalDate startDate = LocalDate.now().plusDays(1);
            contractService.createContract(new ContractCreateRequest(
                    company.getId(), product.getId(), startDate, startDate.plusDays(28), 100_000L
            ));

            org.springframework.data.domain.Pageable pageable =
                    org.springframework.data.domain.PageRequest.of(0, 5,
                            org.springframework.data.domain.Sort.by(
                                    org.springframework.data.domain.Sort.Direction.DESC, "id"));

            PageResponse<?> result = contractService.getContracts(
                    company.getId(), java.util.List.of(ContractStatus.values()), null, null, pageable
            );

            assertThat(result.getContent()).isNotEmpty();
            assertThat(result.getSize()).isEqualTo(5);
        }

        @Test
        @DisplayName("상태 필터 — CANCELLED만 조회 시 다른 상태 미포함")
        void filter_byStatus() {
            LocalDate startDate = LocalDate.now().plusDays(1);
            ContractDetailResponse created = contractService.createContract(new ContractCreateRequest(
                    company.getId(), product.getId(), startDate, startDate.plusDays(28), 100_000L
            ));
            contractService.cancelContract(created.id());

            org.springframework.data.domain.Pageable pageable =
                    org.springframework.data.domain.PageRequest.of(0, 5,
                            org.springframework.data.domain.Sort.by(
                                    org.springframework.data.domain.Sort.Direction.DESC, "id"));

            PageResponse<com.platform.ad.ad_platform.application.contract.dto.ContractListResponse> result =
                    contractService.getContracts(
                            company.getId(), java.util.List.of(ContractStatus.CANCELLED), null, null, pageable
                    );

            assertThat(result.getContent()).allMatch(c -> c.status() == ContractStatus.CANCELLED);
        }

        @Test
        @DisplayName("페이지 크기 초과 계약 — totalPages 2 이상")
        void pagination_whenContractsExceedPageSize() {
            LocalDate base = LocalDate.now().plusDays(100);
            // 6개 생성 (페이지 크기 5 초과)
            for (int i = 0; i < 6; i++) {
                LocalDate start = base.plusDays((long) i * 60);
                contractService.createContract(new ContractCreateRequest(
                        company.getId(), product.getId(), start, start.plusDays(28), 10_000L
                ));
            }

            org.springframework.data.domain.Pageable pageable =
                    org.springframework.data.domain.PageRequest.of(0, 5,
                            org.springframework.data.domain.Sort.by(
                                    org.springframework.data.domain.Sort.Direction.DESC, "id"));

            PageResponse<?> result = contractService.getContracts(
                    company.getId(), java.util.List.of(ContractStatus.values()), null, null, pageable
            );

            assertThat(result.getTotalCount()).isGreaterThanOrEqualTo(6);
            assertThat(result.getTotalPages()).isGreaterThanOrEqualTo(2);
            assertThat(result.getContent()).hasSize(5);
        }
    }
}
