package com.platform.ad.ad_platform.application.contract.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ContractCreateRequest(
        @NotNull Long companyId,
        @NotNull Long productId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull Long amount
) {}
