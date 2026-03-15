package com.platform.ad.ad_platform.web;

import com.platform.ad.ad_platform.application.contract.ContractService;
import com.platform.ad.ad_platform.application.contract.dto.ContractDetailResponse;
import com.platform.ad.ad_platform.application.contract.dto.ContractListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Contracts", description = "계약 API")
@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @Operation(summary = "계약 전체 조회")
    @GetMapping
    public ResponseEntity<List<ContractListResponse>> getContracts() {
        return ResponseEntity.ok(contractService.getContracts());
    }

    @Operation(summary = "계약 단건 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ContractDetailResponse> getContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContract(id));
    }
}
