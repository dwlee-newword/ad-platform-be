package com.platform.ad.ad_platform.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ad.ad_platform.application.contract.ContractService;
import com.platform.ad.ad_platform.application.contract.dto.ContractCreateRequest;
import com.platform.ad.ad_platform.application.contract.dto.ContractDetailResponse;
import com.platform.ad.ad_platform.domain.company.Company;
import com.platform.ad.ad_platform.domain.company.CompanyRepository;
import com.platform.ad.ad_platform.domain.product.Product;
import com.platform.ad.ad_platform.domain.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("ContractController 통합 테스트")
class ContractControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ContractService contractService;
    @Autowired CompanyRepository companyRepository;
    @Autowired ProductRepository productRepository;

    Company company;
    Product product;

    @BeforeEach
    void setUp() {
        // 시드 업체 사용: 오늘 날짜 기준 count=3 → contractNumber "AD-YYYYMMDD-004"부터 생성 (시드 데이터와 충돌 없음)
        company = companyRepository.findById(10001L).orElseThrow();
        product = productRepository.save(
                Product.builder().name("컨트롤러테스트상품").description("설명").build()
        );
    }

    private Map<String, Object> validRequestBody(LocalDate startDate, LocalDate endDate, long amount) {
        return Map.of(
                "companyId", company.getId(),
                "productId", product.getId(),
                "startDate", startDate.toString(),
                "endDate", endDate.toString(),
                "amount", amount
        );
    }

    // ───────────────────────────────────────────────────
    // POST /api/contracts
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/contracts — 계약 생성")
    class CreateContract {

        @Test
        @DisplayName("정상 생성 — 201, Location 헤더 포함, 응답 본문에 contractNumber 존재")
        void success_returns201() throws Exception {
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = startDate.plusDays(28);

            mockMvc.perform(post("/api/contracts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestBody(startDate, endDate, 100_000L))))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.contractNumber").isNotEmpty())
                    .andExpect(jsonPath("$.companyName").value(company.getName()))
                    .andExpect(jsonPath("$.status").value("PENDING"));
        }

        @Test
        @DisplayName("계약 기간 27일 — 400, INVALID_CONTRACT_PERIOD 에러 코드 반환")
        void fail_invalidPeriod_returns400() throws Exception {
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = startDate.plusDays(27);

            mockMvc.perform(post("/api/contracts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestBody(startDate, endDate, 100_000L))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("INVALID_CONTRACT_PERIOD"))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details").isEmpty());
        }

        @Test
        @DisplayName("금액 9,999원 — 400, INVALID_CONTRACT_AMOUNT 에러 코드 반환")
        void fail_invalidAmount_returns400() throws Exception {
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = startDate.plusDays(28);

            mockMvc.perform(post("/api/contracts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestBody(startDate, endDate, 9_999L))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("INVALID_CONTRACT_AMOUNT"))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details").isEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 업체 — 404, COMPANY_NOT_FOUND 에러 코드 반환")
        void fail_companyNotFound_returns404() throws Exception {
            Map<String, Object> body = Map.of(
                    "companyId", 999999L,
                    "productId", product.getId(),
                    "startDate", LocalDate.now().plusDays(1).toString(),
                    "endDate", LocalDate.now().plusDays(29).toString(),
                    "amount", 100_000L
            );

            mockMvc.perform(post("/api/contracts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("COMPANY_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details").isEmpty());
        }

        @Test
        @DisplayName("필수 필드 누락 — 400, VALIDATION_FAILED 에러 코드 + details 배열 반환")
        void fail_missingRequiredFields_returns400() throws Exception {
            Map<String, Object> body = Map.of(
                    "startDate", LocalDate.now().plusDays(1).toString(),
                    "endDate", LocalDate.now().plusDays(29).toString(),
                    "amount", 100_000L
            );

            mockMvc.perform(post("/api/contracts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details").isNotEmpty());
        }

        @Test
        @DisplayName("기간이 겹치는 중복 계약 — 409, DUPLICATE_CONTRACT 에러 코드 반환")
        void fail_duplicateContract_returns409() throws Exception {
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = startDate.plusDays(28);

            contractService.createContract(
                    new ContractCreateRequest(company.getId(), product.getId(), startDate, endDate, 100_000L)
            );

            mockMvc.perform(post("/api/contracts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequestBody(startDate, endDate, 100_000L))))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value("DUPLICATE_CONTRACT"))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details").isEmpty());
        }
    }

    // ───────────────────────────────────────────────────
    // GET /api/contracts/{id}
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/contracts/{id} — 계약 단건 조회")
    class GetContract {

        @Test
        @DisplayName("존재하는 계약 — 200, 상세 응답 반환")
        void success_returns200() throws Exception {
            ContractDetailResponse created = contractService.createContract(
                    new ContractCreateRequest(
                            company.getId(), product.getId(),
                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(29),
                            100_000L
                    )
            );

            mockMvc.perform(get("/api/contracts/{id}", created.id()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(created.id()))
                    .andExpect(jsonPath("$.contractNumber").value(created.contractNumber()))
                    .andExpect(jsonPath("$.companyName").isNotEmpty())
                    .andExpect(jsonPath("$.productName").isNotEmpty())
                    .andExpect(jsonPath("$.startDate").isNotEmpty())
                    .andExpect(jsonPath("$.endDate").isNotEmpty())
                    .andExpect(jsonPath("$.amount").isNumber())
                    .andExpect(jsonPath("$.status").isNotEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 계약 — 404, CONTRACT_NOT_FOUND 에러 코드 반환")
        void fail_notFound_returns404() throws Exception {
            mockMvc.perform(get("/api/contracts/{id}", Long.MAX_VALUE))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("CONTRACT_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details").isEmpty());
        }
    }

    // ───────────────────────────────────────────────────
    // POST /api/contracts/{id}/cancel
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/contracts/{id}/cancel — 계약 취소")
    class CancelContract {

        @Test
        @DisplayName("PENDING 계약 취소 — 200, 상태 CANCELLED 반환")
        void success_cancelPending() throws Exception {
            ContractDetailResponse created = contractService.createContract(
                    new ContractCreateRequest(
                            company.getId(), product.getId(),
                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(29),
                            100_000L
                    )
            );

            mockMvc.perform(post("/api/contracts/{id}/cancel", created.id()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CANCELLED"));
        }

        @Test
        @DisplayName("존재하지 않는 계약 취소 — 404, CONTRACT_NOT_FOUND 에러 코드 반환")
        void fail_notFound_returns404() throws Exception {
            mockMvc.perform(post("/api/contracts/{id}/cancel", Long.MAX_VALUE))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("CONTRACT_NOT_FOUND"))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details").isEmpty());
        }

        @Test
        @DisplayName("CANCELLED 계약 재취소 — 400, CANNOT_CANCEL_CONTRACT 에러 코드 반환")
        void fail_alreadyCancelled_returns400() throws Exception {
            ContractDetailResponse created = contractService.createContract(
                    new ContractCreateRequest(
                            company.getId(), product.getId(),
                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(29),
                            100_000L
                    )
            );
            contractService.cancelContract(created.id());

            mockMvc.perform(post("/api/contracts/{id}/cancel", created.id()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("CANNOT_CANCEL_CONTRACT"))
                    .andExpect(jsonPath("$.message").isNotEmpty())
                    .andExpect(jsonPath("$.details").isArray())
                    .andExpect(jsonPath("$.details").isEmpty());
        }
    }

    // ───────────────────────────────────────────────────
    // GET /api/contracts
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/contracts — 계약 목록 조회")
    class GetContracts {

        @Test
        @DisplayName("페이지네이션 응답 구조 — content, totalCount, totalPages, page, size 포함")
        void success_paginationStructure() throws Exception {
            mockMvc.perform(get("/api/contracts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.totalCount").isNumber())
                    .andExpect(jsonPath("$.totalPages").isNumber())
                    .andExpect(jsonPath("$.page").isNumber())
                    .andExpect(jsonPath("$.size").value(5));
        }

        @Test
        @DisplayName("업체 ID 필터 — 해당 업체 계약만 포함된 결과 반환")
        void filter_byCompanyId() throws Exception {
            contractService.createContract(new ContractCreateRequest(
                    company.getId(), product.getId(),
                    LocalDate.now().plusDays(1), LocalDate.now().plusDays(29),
                    100_000L
            ));

            mockMvc.perform(get("/api/contracts")
                            .param("companyId", String.valueOf(company.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                    .andExpect(jsonPath("$.content[0].companyName").value(company.getName()));
        }

        @Test
        @DisplayName("상태 필터 — PENDING만 조회 시 다른 상태 미포함")
        void filter_byStatus() throws Exception {
            mockMvc.perform(get("/api/contracts")
                            .param("companyId", String.valueOf(company.getId()))
                            .param("statuses", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[*].status",
                            org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.is("PENDING"))));
        }
    }
}
