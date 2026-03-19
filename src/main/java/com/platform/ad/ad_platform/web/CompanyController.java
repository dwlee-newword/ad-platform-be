package com.platform.ad.ad_platform.web;

import com.platform.ad.ad_platform.application.company.CompanyService;
import com.platform.ad.ad_platform.application.company.CompanyService.CompanyCountResponse;
import com.platform.ad.ad_platform.application.company.CompanyService.CompanyResponse;
import com.platform.ad.ad_platform.application.company.CompanyService.CompanySearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Companies", description = "업체 API")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "업체 수 조회")
    @GetMapping("/count")
    public ResponseEntity<CompanyCountResponse> getCompanyCount() {
        return ResponseEntity.ok(companyService.getCompanyCount());
    }

    @Operation(summary = "업체 목록 조회 (keyword 필터 선택)")
    @GetMapping
    public ResponseEntity<CompanySearchResponse> getCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer limit) {
        log.info("getCompanies keyword: {}, limit: {}", keyword, limit);
        return ResponseEntity.ok(companyService.getCompanies(keyword, limit));
    }

    @Operation(summary = "업체 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }
}
