package com.platform.ad.ad_platform.application.contract.dto;

import com.platform.ad.ad_platform.domain.contract.Contract;
import com.platform.ad.ad_platform.domain.contract.ContractStatus;

import java.time.LocalDate;

public record ContractListResponse(
        Long id,
        String contractNumber,
        LocalDate contractDate,
        String companyName,
        String productName,
        LocalDate startDate,
        LocalDate endDate,
        Long amount,
        ContractStatus status
) {

    public static ContractListResponse of(Contract contract) {
        return new ContractListResponse(
                contract.getId(),
                contract.getContractNumber(),
                contract.getContractDate(),
                contract.getCompany().getName(),
                contract.getProduct().getName(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getAmount(),
                contract.getStatus()
        );
    }
}
