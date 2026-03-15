package com.platform.ad.ad_platform.application.contract.dto;

import com.platform.ad.ad_platform.domain.contract.Contract;
import com.platform.ad.ad_platform.domain.contract.ContractStatus;

import java.time.LocalDate;

public record ContractDetailResponse(
        Long id,
        Long companyId,
        String companyName,
        Long productId,
        String productName,
        LocalDate startDate,
        LocalDate endDate,
        Long amount,
        ContractStatus status
) {

    public static ContractDetailResponse of(Contract contract) {
        return new ContractDetailResponse(
                contract.getId(),
                contract.getCompany().getId(),
                contract.getCompany().getName(),
                contract.getProduct().getId(),
                contract.getProduct().getName(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getAmount(),
                contract.getStatus()
        );
    }
}
