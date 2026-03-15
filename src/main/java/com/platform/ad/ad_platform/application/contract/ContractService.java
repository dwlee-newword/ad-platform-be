package com.platform.ad.ad_platform.application.contract;

import com.platform.ad.ad_platform.application.contract.dto.ContractDetailResponse;
import com.platform.ad.ad_platform.application.contract.dto.ContractListResponse;
import com.platform.ad.ad_platform.common.exception.ContractNotFoundException;
import com.platform.ad.ad_platform.domain.contract.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContractService {

    private final ContractRepository contractRepository;

    public List<ContractListResponse> getContracts() {
        return contractRepository.findAllWithDetails().stream()
                .map(ContractListResponse::of)
                .toList();
    }

    public ContractDetailResponse getContract(Long id) {
        return contractRepository.findByIdWithDetails(id)
                .map(ContractDetailResponse::of)
                .orElseThrow(ContractNotFoundException::new);
    }
}
