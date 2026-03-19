package com.platform.ad.ad_platform.application.contract;

import com.platform.ad.ad_platform.application.contract.dto.ContractCreateRequest;
import com.platform.ad.ad_platform.application.contract.dto.ContractDetailResponse;
import com.platform.ad.ad_platform.application.contract.dto.ContractListResponse;
import com.platform.ad.ad_platform.common.exception.CompanyNotFoundException;
import com.platform.ad.ad_platform.common.exception.ContractNotFoundException;
import com.platform.ad.ad_platform.common.exception.DuplicateContractException;
import com.platform.ad.ad_platform.common.exception.InvalidContractException;
import com.platform.ad.ad_platform.common.exception.ErrorCode;
import com.platform.ad.ad_platform.common.exception.ProductNotFoundException;
import com.platform.ad.ad_platform.common.response.PageResponse;
import com.platform.ad.ad_platform.domain.company.CompanyRepository;
import com.platform.ad.ad_platform.domain.contract.Contract;
import com.platform.ad.ad_platform.domain.contract.ContractRepository;
import com.platform.ad.ad_platform.domain.contract.ContractStatus;
import com.platform.ad.ad_platform.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractService {

    private final ContractRepository contractRepository;
    private final CompanyRepository companyRepository;
    private final ProductRepository productRepository;

    @Transactional
    public PageResponse<ContractListResponse> getContracts(
            Long companyId,
            List<ContractStatus> statuses,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        var page = contractRepository.findAllWithFilters(companyId, statuses, startDate, endDate, pageable);
        page.forEach(c -> c.refreshStatus(LocalDate.now()));
        return PageResponse.of(page.map(ContractListResponse::of));
    }

    @Transactional
    public ContractDetailResponse getContract(Long id) {
        Contract contract = contractRepository.findByIdWithDetails(id)
                .orElseThrow(ContractNotFoundException::new);
        contract.refreshStatus(LocalDate.now());
        return ContractDetailResponse.of(contract);
    }

    @Transactional
    public ContractDetailResponse cancelContract(Long id) {
        Contract contract = contractRepository.findByIdWithDetails(id)
                .orElseThrow(ContractNotFoundException::new);
        contract.cancel(LocalDate.now());
        return ContractDetailResponse.of(contract);
    }

    @Transactional
    public ContractDetailResponse createContract(ContractCreateRequest request) {
        var company = companyRepository.findById(request.companyId())
                .orElseThrow(CompanyNotFoundException::new);
        var product = productRepository.findById(request.productId())
                .orElseThrow(ProductNotFoundException::new);

        if (contractRepository.existsOverlapping(
                request.companyId(), request.productId(),
                request.startDate(), request.endDate())) {
            throw new DuplicateContractException();
        }

        LocalDate today = LocalDate.now();
        long todayCount = contractRepository.countByCompanyAndContractDate(company, today);
        if (todayCount >= 999) {
            throw new InvalidContractException(ErrorCode.CONTRACT_NUMBER_LIMIT_EXCEEDED);
        }
        String contractNumber = "AD-" + today.format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-" + String.format("%03d", todayCount + 1);

        Contract contract = Contract.create(
                company, product,
                request.startDate(), request.endDate(),
                request.amount(), today, contractNumber
        );

        return ContractDetailResponse.of(contractRepository.save(contract));
    }
}
