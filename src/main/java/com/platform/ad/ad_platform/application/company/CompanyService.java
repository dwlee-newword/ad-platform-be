package com.platform.ad.ad_platform.application.company;

import com.platform.ad.ad_platform.common.exception.CompanyNotFoundException;
import com.platform.ad.ad_platform.domain.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;

    public record CompanyResponse(Long id, String name, String type) {}

    public List<CompanyResponse> getCompanies() {
        // TODO: 실제 구현 시 함수형 사용 자제
        return companyRepository.findAll().stream()
                .map(c -> new CompanyResponse(c.getId(), c.getName(), c.getType()))
                .toList();
    }

    public CompanyResponse getCompany(Long id) {
        return companyRepository.findById(id)
                .map(c -> new CompanyResponse(c.getId(), c.getName(), c.getType()))
                .orElseThrow(CompanyNotFoundException::new);
    }
}
