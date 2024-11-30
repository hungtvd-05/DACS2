package com.dacs2.repository;

import com.dacs2.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Boolean existsByName(String name);

    List<Brand> findByStatus(Boolean status);
}
