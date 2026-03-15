package com.platform.ad.ad_platform.domain.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    // TODO: LAZY
    // TODO: 테스트 API 완료 후 JOIN FETCH 필요할때만 사용
    @Query("SELECT c FROM Contract c JOIN FETCH c.company JOIN FETCH c.product")
    List<Contract> findAllWithDetails();

    @Query("SELECT c FROM Contract c JOIN FETCH c.company JOIN FETCH c.product WHERE c.id = :id")
    Optional<Contract> findByIdWithDetails(@Param("id") Long id);
}
