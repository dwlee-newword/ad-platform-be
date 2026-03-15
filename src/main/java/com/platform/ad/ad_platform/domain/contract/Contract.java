package com.platform.ad.ad_platform.domain.contract;

import com.platform.ad.ad_platform.domain.company.Company;
import com.platform.ad.ad_platform.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // TODO: README.md에 N+1, join 발생 억제 관련 내용 정리
    // TODO: 현재는 테스트 API용 전체조회라 pagination 필요 시 작성
    // TODO: 불필요한 LAZY 정리
    // TODO: LAZY
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ContractStatus status;

    @Builder
    private Contract(Company company, Product product, LocalDate startDate, LocalDate endDate,
                     Long amount, ContractStatus status) {
        this.company = company;
        this.product = product;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.status = status;
    }
}
