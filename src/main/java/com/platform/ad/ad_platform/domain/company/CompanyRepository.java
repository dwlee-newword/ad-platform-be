package com.platform.ad.ad_platform.domain.company;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByNameContainingIgnoreCase(String name);
    List<Company> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
