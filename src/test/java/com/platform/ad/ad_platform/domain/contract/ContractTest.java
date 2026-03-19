package com.platform.ad.ad_platform.domain.contract;

import com.platform.ad.ad_platform.common.exception.BusinessException;
import com.platform.ad.ad_platform.common.exception.ErrorCode;
import com.platform.ad.ad_platform.domain.company.Company;
import com.platform.ad.ad_platform.domain.product.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Contract 도메인 단위 테스트")
class ContractTest {

    private static final LocalDate TODAY = LocalDate.of(2025, 6, 1);
    private static final String CONTRACT_NUMBER = "AD-20250601-001";

    private Company company() {
        return Company.builder().id(1L).name("테스트 업체").type("호텔").build();
    }

    private Product product() {
        return Product.builder().name("노출 보장형 광고").description("설명").build();
    }

    private Contract createContract(LocalDate startDate, LocalDate endDate, long amount, LocalDate today) {
        return Contract.create(company(), product(), startDate, endDate, amount, today, CONTRACT_NUMBER);
    }

    // ───────────────────────────────────────────────────
    // 계약 상태 도출
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("계약 상태 도출")
    class DeriveStatus {

        private final LocalDate startDate = TODAY.plusDays(1);
        private final LocalDate endDate = startDate.plusDays(28);

        @Test
        @DisplayName("오늘이 시작일 이전이면 PENDING")
        void pending_whenTodayBeforeStartDate() {
            Contract contract = createContract(startDate, endDate, 100_000L, TODAY);

            assertThat(contract.getStatus()).isEqualTo(ContractStatus.PENDING);
        }

        @Test
        @DisplayName("오늘이 시작일과 같으면 IN_PROGRESS")
        void inProgress_whenTodayEqualsStartDate() {
            Contract contract = createContract(TODAY, TODAY.plusDays(28), 100_000L, TODAY);

            assertThat(contract.getStatus()).isEqualTo(ContractStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("오늘이 종료일과 같으면 IN_PROGRESS")
        void inProgress_whenTodayEqualsEndDate() {
            Contract contract = createContract(TODAY, TODAY.plusDays(28), 100_000L, TODAY);
            LocalDate futureToday = TODAY.plusDays(28);

            contract.refreshStatus(futureToday);

            assertThat(contract.getStatus()).isEqualTo(ContractStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("오늘이 종료일 이후면 COMPLETED")
        void completed_whenTodayAfterEndDate() {
            Contract contract = createContract(TODAY, TODAY.plusDays(28), 100_000L, TODAY);
            LocalDate futureToday = TODAY.plusDays(29);

            contract.refreshStatus(futureToday);

            assertThat(contract.getStatus()).isEqualTo(ContractStatus.COMPLETED);
        }
    }

    // ───────────────────────────────────────────────────
    // 날짜 유효성 검증
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("날짜 유효성 검증")
    class ValidateDate {

        @Test
        @DisplayName("시작일이 오늘보다 이전이면 INVALID_START_DATE 예외")
        void fail_whenStartDateBeforeToday() {
            LocalDate startDate = TODAY.minusDays(1);
            LocalDate endDate = startDate.plusDays(28);

            assertThatThrownBy(() -> createContract(startDate, endDate, 100_000L, TODAY))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.INVALID_START_DATE));
        }

        @Test
        @DisplayName("시작일이 오늘이면 성공")
        void success_whenStartDateIsToday() {
            assertThatNoException().isThrownBy(() -> createContract(TODAY, TODAY.plusDays(28), 100_000L, TODAY));
        }

        @Test
        @DisplayName("종료일이 시작일과 같으면 INVALID_DATE_RANGE 예외")
        void fail_whenEndDateEqualsStartDate() {
            LocalDate startDate = TODAY.plusDays(1);

            assertThatThrownBy(() -> createContract(startDate, startDate, 100_000L, TODAY))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.INVALID_DATE_RANGE));
        }

        @Test
        @DisplayName("종료일이 시작일보다 이전이면 INVALID_DATE_RANGE 예외")
        void fail_whenEndDateBeforeStartDate() {
            LocalDate startDate = TODAY.plusDays(10);
            LocalDate endDate = TODAY.plusDays(5);

            assertThatThrownBy(() -> createContract(startDate, endDate, 100_000L, TODAY))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.INVALID_DATE_RANGE));
        }

        @Test
        @DisplayName("계약 기간이 27일이면 INVALID_CONTRACT_PERIOD 예외")
        void fail_whenPeriodIs27Days() {
            LocalDate startDate = TODAY.plusDays(1);
            LocalDate endDate = startDate.plusDays(27);

            assertThatThrownBy(() -> createContract(startDate, endDate, 100_000L, TODAY))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.INVALID_CONTRACT_PERIOD));
        }

        @Test
        @DisplayName("계약 기간이 정확히 28일이면 성공")
        void success_whenPeriodIsExactly28Days() {
            LocalDate startDate = TODAY.plusDays(1);
            LocalDate endDate = startDate.plusDays(28);

            assertThatNoException().isThrownBy(() -> createContract(startDate, endDate, 100_000L, TODAY));
        }

        @Test
        @DisplayName("계약 기간이 28일 초과면 성공")
        void success_whenPeriodExceeds28Days() {
            LocalDate startDate = TODAY.plusDays(1);
            LocalDate endDate = startDate.plusDays(60);

            assertThatNoException().isThrownBy(() -> createContract(startDate, endDate, 100_000L, TODAY));
        }
    }

    // ───────────────────────────────────────────────────
    // 금액 유효성 검증
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("금액 유효성 검증")
    class ValidateAmount {

        private final LocalDate startDate = TODAY.plusDays(1);
        private final LocalDate endDate = startDate.plusDays(28);

        @Test
        @DisplayName("금액이 9,999원이면 INVALID_CONTRACT_AMOUNT 예외")
        void fail_whenAmountBelowMin() {
            assertThatThrownBy(() -> createContract(startDate, endDate, 9_999L, TODAY))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.INVALID_CONTRACT_AMOUNT));
        }

        @Test
        @DisplayName("최소 금액 10,000원은 성공")
        void success_whenAmountIsMin() {
            assertThatNoException().isThrownBy(() -> createContract(startDate, endDate, 10_000L, TODAY));
        }

        @Test
        @DisplayName("최대 금액 1,000,000원은 성공")
        void success_whenAmountIsMax() {
            assertThatNoException().isThrownBy(() -> createContract(startDate, endDate, 1_000_000L, TODAY));
        }

        @Test
        @DisplayName("금액이 1,000,001원이면 INVALID_CONTRACT_AMOUNT 예외")
        void fail_whenAmountExceedsMax() {
            assertThatThrownBy(() -> createContract(startDate, endDate, 1_000_001L, TODAY))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.INVALID_CONTRACT_AMOUNT));
        }
    }

    // ───────────────────────────────────────────────────
    // 계약 취소
    // ───────────────────────────────────────────────────

    @Nested
    @DisplayName("계약 취소")
    class Cancel {

        @Test
        @DisplayName("PENDING 계약은 취소 가능")
        void cancel_pendingContract() {
            LocalDate startDate = TODAY.plusDays(1);
            Contract contract = createContract(startDate, startDate.plusDays(28), 100_000L, TODAY);

            contract.cancel(TODAY);

            assertThat(contract.getStatus()).isEqualTo(ContractStatus.CANCELLED);
        }

        @Test
        @DisplayName("IN_PROGRESS 계약은 취소 가능")
        void cancel_inProgressContract() {
            Contract contract = createContract(TODAY, TODAY.plusDays(28), 100_000L, TODAY);

            contract.cancel(TODAY);

            assertThat(contract.getStatus()).isEqualTo(ContractStatus.CANCELLED);
        }

        @Test
        @DisplayName("COMPLETED 계약은 취소 불가 — CANNOT_CANCEL_CONTRACT 예외")
        void cannotCancel_completedContract() {
            Contract contract = createContract(TODAY, TODAY.plusDays(28), 100_000L, TODAY);
            LocalDate futureToday = TODAY.plusDays(29);
            contract.refreshStatus(futureToday);

            assertThatThrownBy(() -> contract.cancel(futureToday))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.CANNOT_CANCEL_CONTRACT));
        }

        @Test
        @DisplayName("CANCELLED 계약은 재취소 불가 — CANNOT_CANCEL_CONTRACT 예외")
        void cannotCancel_alreadyCancelledContract() {
            LocalDate startDate = TODAY.plusDays(1);
            Contract contract = createContract(startDate, startDate.plusDays(28), 100_000L, TODAY);
            contract.cancel(TODAY);

            assertThatThrownBy(() -> contract.cancel(TODAY))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                            .isEqualTo(ErrorCode.CANNOT_CANCEL_CONTRACT));
        }
    }
}
