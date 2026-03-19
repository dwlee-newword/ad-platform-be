package com.platform.ad.ad_platform.web;

import com.platform.ad.ad_platform.application.contract.ContractService;
import com.platform.ad.ad_platform.application.contract.dto.ContractCreateRequest;
import com.platform.ad.ad_platform.application.contract.dto.ContractDetailResponse;
import com.platform.ad.ad_platform.application.contract.dto.ContractListResponse;
import com.platform.ad.ad_platform.common.response.PageResponse;
import com.platform.ad.ad_platform.domain.contract.ContractStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Contracts", description = "계약 API")
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private static final int CONTRACT_PAGE_SIZE = 5;

    private final ContractService contractService;

    @Operation(summary = "계약 전체 조회")
    @GetMapping
    public ResponseEntity<PageResponse<ContractListResponse>> getContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) List<ContractStatus> statuses,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ContractStatus> effectiveStatuses = (statuses == null || statuses.isEmpty())
                ? List.of(ContractStatus.values())
                : statuses;
        return ResponseEntity.ok(contractService.getContracts(
                companyId, effectiveStatuses, startDate, endDate,
                PageRequest.of(page, CONTRACT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "contractDate", "id"))
        ));
    }

    @Operation(summary = "계약 단건 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ContractDetailResponse> getContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContract(id));
    }

    @Operation(summary = "계약 취소")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ContractDetailResponse> cancelContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.cancelContract(id));
    }

    @Operation(summary = "계약 생성")
    @PostMapping
    public ResponseEntity<ContractDetailResponse> createContract(@Valid @RequestBody ContractCreateRequest request) {
        ContractDetailResponse response = contractService.createContract(request);
        return ResponseEntity.created(URI.create("/api/contracts/" + response.id())).body(response);
    }
}
