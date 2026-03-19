package com.platform.ad.ad_platform.domain.contract;

import com.platform.ad.ad_platform.domain.company.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query(value = "SELECT c FROM Contract c JOIN FETCH c.company JOIN FETCH c.product",
           countQuery = "SELECT COUNT(c) FROM Contract c")
    Page<Contract> findAllWithDetails(Pageable pageable);

    @Query("SELECT c FROM Contract c JOIN FETCH c.company JOIN FETCH c.product")
    List<Contract> findAllWithDetails();

    @Query("SELECT c FROM Contract c JOIN FETCH c.company JOIN FETCH c.product WHERE c.id = :id")
    Optional<Contract> findByIdWithDetails(@Param("id") Long id);

    @Query(value = """
            SELECT c FROM Contract c JOIN FETCH c.company JOIN FETCH c.product
            WHERE (:companyId IS NULL OR c.company.id = :companyId)
              AND (:startDate IS NULL OR c.endDate >= :startDate)
              AND (:endDate IS NULL OR c.startDate <= :endDate)
              AND (c.status IN :statuses)
            """,
           countQuery = """
            SELECT COUNT(c) FROM Contract c
            WHERE (:companyId IS NULL OR c.company.id = :companyId)
              AND (:startDate IS NULL OR c.endDate >= :startDate)
              AND (:endDate IS NULL OR c.startDate <= :endDate)
              AND (c.status IN :statuses)
            """)
    Page<Contract> findAllWithFilters(
            @Param("companyId") Long companyId,
            @Param("statuses") List<ContractStatus> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    long countByCompanyAndContractDate(Company company, LocalDate contractDate);

    @Query("""
            SELECT COUNT(c) > 0 FROM Contract c
            WHERE c.company.id = :companyId
              AND c.product.id = :productId
              AND c.status <> com.platform.ad.ad_platform.domain.contract.ContractStatus.CANCELLED
              AND c.startDate < :endDate
              AND c.endDate > :startDate
            """)
    boolean existsOverlapping(@Param("companyId") Long companyId,
                              @Param("productId") Long productId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate);
}
