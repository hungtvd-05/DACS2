package com.dacs2.repository;

import com.dacs2.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Boolean existsByName(String name);

    public List<Category> findByIsActive(Boolean isActive);

    public Category findByName(String name);

}
