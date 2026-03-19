package com.platform.ad.ad_platform.application.company;

import com.platform.ad.ad_platform.common.exception.CompanyNotFoundException;
import com.platform.ad.ad_platform.domain.company.Company;
import com.platform.ad.ad_platform.domain.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;

    public record CompanyResponse(Long id, String name, String type) {}
    public record CompanyCountResponse(long count) {}
    public record CompanySearchResponse(List<CompanyResponse> items, boolean hasMore) {}

    public CompanyCountResponse getCompanyCount() {
        return new CompanyCountResponse(companyRepository.count());
    }

    public CompanySearchResponse getCompanies(String keyword, Integer limit) {
        List<Company> companies;
        boolean hasMore = false;

        if (keyword == null || keyword.isBlank()) {
            companies = companyRepository.findAll();
        } else {
            // limit+1건 조회해 초과 여부 확인 (peek 패턴)
            List<Company> fetched = companyRepository.findByNameContainingIgnoreCase(
                    keyword, PageRequest.of(0, limit + 1));
            hasMore = fetched.size() > limit;
            companies = hasMore ? fetched.subList(0, limit) : fetched;
        }

        List<CompanyResponse> items = new ArrayList<>();
        for (Company c : companies) {
            items.add(new CompanyResponse(c.getId(), c.getName(), c.getType()));
        }
        return new CompanySearchResponse(items, hasMore);
    }

    public CompanyResponse getCompany(Long id) {
        return companyRepository.findById(id)
                .map(c -> new CompanyResponse(c.getId(), c.getName(), c.getType()))
                .orElseThrow(CompanyNotFoundException::new);
    }
}
