package com.platform.ad.ad_platform.web;

import com.platform.ad.ad_platform.application.company.CompanyService;
import com.platform.ad.ad_platform.application.company.CompanyService.CompanyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Companies", description = "업체 API")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @Operation(summary = "업체 전체 조회")
    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getCompanies() {
        return ResponseEntity.ok(companyService.getCompanies());
    }

    @Operation(summary = "업체 단건 조회")
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }
}
