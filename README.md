# AD Platform Backend

광고 상품 구매 서비스 백엔드

---

## 1. 기술 스택 및 런타임 환경

| 항목         | 내용                                              | 비고 |
|------------|-------------------------------------------------|----|
| Language   | Java 21                                         |    |
| Framework  | Spring Boot 3.5.11                              |    |
| Build Tool | Gradle                                          |    |
| ORM        | Spring Data JPA (Hibernate)                     |    |
| Database   | H2 (인메모리, 개발용)                                  |    |
| API 문서     | SpringDoc OpenAPI (Swagger UI)                  |    |
| 기타         | Lombok, Spring Validation, Spring Boot DevTools |    |

---

## 2. 프로젝트 구조

> 작업 중

---

## 3. 설계 및 구현

> 작업 중

---

## 4. 프로젝트 실행 방법

> 작업 중

---

## 5. 테스트 방법

### 5-1. 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 클래스만 실행
./gradlew test --tests "com.platform.ad.ad_platform.domain.contract.ContractTest"
./gradlew test --tests "com.platform.ad.ad_platform.application.contract.ContractServiceIntegrationTest"
./gradlew test --tests "com.platform.ad.ad_platform.web.ContractControllerTest"

# 특정 메서드만 실행
./gradlew test --tests "com.platform.ad.ad_platform.domain.contract.ContractTest.계약 상태 도출.pending_whenTodayBeforeStartDate"
```

테스트 결과 리포트: `build/reports/tests/test/index.html`

---

### 5-2. 테스트 구성

#### ContractTest — 도메인 단위 테스트

Spring 컨텍스트 없이 `Contract` 엔티티의 비즈니스 로직만 검증합니다.

| 분류 | 테스트 항목 |
|---|---|
| 계약 상태 도출 | 오늘 < 시작일 → `PENDING` |
| | 시작일 ≤ 오늘 ≤ 종료일 → `IN_PROGRESS` (경계값 포함) |
| | 오늘 > 종료일 → `COMPLETED` |
| 날짜 유효성 | 종료일 = 시작일 → `INVALID_DATE_RANGE` |
| | 종료일 < 시작일 → `INVALID_DATE_RANGE` |
| | 계약 기간 27일 → `INVALID_CONTRACT_PERIOD` |
| | 계약 기간 정확히 28일 → 성공 (경계값) |
| | 계약 기간 28일 초과 → 성공 |
| 금액 유효성 | 9,999원 → `INVALID_CONTRACT_AMOUNT` |
| | 10,000원 → 성공 (최솟값 경계) |
| | 1,000,000원 → 성공 (최댓값 경계) |
| | 1,000,001원 → `INVALID_CONTRACT_AMOUNT` |
| 계약 취소 | `PENDING` 취소 → `CANCELLED` |
| | `IN_PROGRESS` 취소 → `CANCELLED` |
| | `COMPLETED` 취소 → `CANNOT_CANCEL_CONTRACT` |
| | `CANCELLED` 재취소 → `CANNOT_CANCEL_CONTRACT` |

#### ContractServiceIntegrationTest — 서비스 통합 테스트

`@SpringBootTest` + `@Transactional`(롤백) + H2 인메모리 DB로 실행합니다.

| 분류 | 테스트 항목 |
|---|---|
| 계약 생성 | 정상 생성 — contractNumber 포함, 상태 도출 확인 |
| | 존재하지 않는 업체 → `COMPANY_NOT_FOUND` (404) |
| | 존재하지 않는 상품 → `PRODUCT_NOT_FOUND` (404) |
| | 기간이 겹치는 계약 → `DUPLICATE_CONTRACT` (409) |
| | 취소 후 동일 기간 재계약 → 성공 |
| 계약 단건 조회 | 정상 조회 — id, contractNumber 일치 확인 |
| | 존재하지 않는 ID → `CONTRACT_NOT_FOUND` (404) |
| 계약 취소 | `PENDING` 계약 취소 → 상태 `CANCELLED` |
| | 존재하지 않는 ID → `CONTRACT_NOT_FOUND` (404) |
| 계약 목록 조회 | 업체 ID 필터 — 해당 업체 계약만 반환 |
| | 상태 필터 — `CANCELLED`만 조회 시 다른 상태 미포함 |
| | 5건 초과 시 totalPages ≥ 2, content 크기 = 5 |

#### ContractControllerTest — 컨트롤러 통합 테스트

`@SpringBootTest` + `@AutoConfigureMockMvc` + `@Transactional`(롤백) 으로 실제 HTTP 레이어를 검증합니다.

| 엔드포인트 | 테스트 항목 |
|---|---|
| `POST /api/contracts` | 정상 생성 → 201, Location 헤더, 응답 필드 확인 |
| | 27일 계약 기간 → 400, `code: INVALID_CONTRACT_PERIOD` |
| | 9,999원 금액 → 400, `code: INVALID_CONTRACT_AMOUNT` |
| | 존재하지 않는 업체 → 404, `code: COMPANY_NOT_FOUND` |
| | 기간 겹침 → 409, `code: DUPLICATE_CONTRACT` |
| `GET /api/contracts/{id}` | 정상 조회 → 200, 응답 필드 구조 확인 |
| | 존재하지 않는 ID → 404, `code: CONTRACT_NOT_FOUND` |
| `PATCH /api/contracts/{id}/cancel` | `PENDING` 취소 → 200, `status: CANCELLED` |
| | 존재하지 않는 ID → 404, `code: CONTRACT_NOT_FOUND` |
| | `CANCELLED` 재취소 → 400, `code: CANNOT_CANCEL_CONTRACT` |
| `GET /api/contracts` | 페이지네이션 응답 구조 확인 (`content`, `totalCount`, `totalPages`, `page`, `size`) |
| | 업체 ID 필터 동작 확인 |
| | 상태 필터 — `PENDING`만 조회 시 결과 일치 |

---

### 5-3. 에러 응답 스펙

모든 비즈니스 예외는 아래 형식으로 반환됩니다.

```json
{
  "code": "ERROR_CODE",
  "message": "에러 메시지"
}
```

| HTTP 상태 | code |
|---|---|
| 400 | `INVALID_CONTRACT_PERIOD`, `INVALID_CONTRACT_AMOUNT`, `INVALID_DATE_RANGE`, `CANNOT_CANCEL_CONTRACT` |
| 404 | `COMPANY_NOT_FOUND`, `PRODUCT_NOT_FOUND`, `CONTRACT_NOT_FOUND` |
| 409 | `DUPLICATE_CONTRACT`, `CONTRACT_NUMBER_LIMIT_EXCEEDED` |

---

## 6. 개선사항 및 제약사항

> 작업 중

---