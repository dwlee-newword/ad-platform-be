package com.platform.ad.ad_platform.domain.contract;

import com.platform.ad.ad_platform.common.exception.ErrorCode;
import com.platform.ad.ad_platform.common.exception.InvalidContractException;
import com.platform.ad.ad_platform.domain.company.Company;
import com.platform.ad.ad_platform.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column(name = "contract_date", nullable = false, updatable = false)
    private LocalDate contractDate;

    @Column(name = "contract_number", nullable = false, unique = true, updatable = false, length = 16)
    private String contractNumber;

    @Builder
    private Contract(Company company, Product product, LocalDate startDate, LocalDate endDate,
                     Long amount, ContractStatus status, String contractNumber) {
        this.company = company;
        this.product = product;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.status = status;
        this.contractNumber = contractNumber;
    }

    public static Contract create(Company company, Product product,
                                  LocalDate startDate, LocalDate endDate,
                                  Long amount, LocalDate today, String contractNumber) {
        validate(startDate, endDate, amount, today);
        return Contract.builder()
                .company(company)
                .product(product)
                .startDate(startDate)
                .endDate(endDate)
                .amount(amount)
                .status(deriveStatus(startDate, endDate, today))
                .contractNumber(contractNumber)
                .build();
    }

    private static void validate(LocalDate startDate, LocalDate endDate, Long amount, LocalDate today) {
        if (startDate.isBefore(today)) {
            throw new InvalidContractException(ErrorCode.INVALID_START_DATE);
        }
        if (!endDate.isAfter(startDate)) {
            throw new InvalidContractException(ErrorCode.INVALID_DATE_RANGE);
        }
        if (endDate.isBefore(startDate.plusDays(28))) {
            throw new InvalidContractException(ErrorCode.INVALID_CONTRACT_PERIOD);
        }
        if (amount < 10_000 || amount > 1_000_000) {
            throw new InvalidContractException(ErrorCode.INVALID_CONTRACT_AMOUNT);
        }
    }

    private static ContractStatus deriveStatus(LocalDate startDate, LocalDate endDate, LocalDate today) {
        if (today.isBefore(startDate)) return ContractStatus.PENDING;
        if (!today.isAfter(endDate)) return ContractStatus.IN_PROGRESS;
        return ContractStatus.COMPLETED;
    }

    public ContractStatus refreshStatus(LocalDate today) {
        if (this.status != ContractStatus.CANCELLED) {
            this.status = deriveStatus(startDate, endDate, today);
        }
        return this.status;
    }

    public void cancel(LocalDate today) {
        refreshStatus(today);
        if (this.status != ContractStatus.PENDING && this.status != ContractStatus.IN_PROGRESS) {
            throw new InvalidContractException(ErrorCode.CANNOT_CANCEL_CONTRACT);
        }
        this.status = ContractStatus.CANCELLED;
    }
}
