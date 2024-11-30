package com.dacs2.service.impl;

import com.dacs2.model.Category;
import com.dacs2.repository.CategoryRepository;
import com.dacs2.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryReposity;

    @Override
    public Category saveCategory(Category category) {
        return categoryReposity.save(category);
    }

    @Override
    public Boolean existCategory(String name) {
        return categoryReposity.existsByName(name);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryReposity.findAll();
    }

    @Override
    public Boolean deleteCategory(Long id) {
        Category category = categoryReposity.findById(id).orElse(null);

        if (!ObjectUtils.isEmpty(category)) {
            categoryReposity.delete(category);
            return true;
        }
        return false;
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryReposity.findById(id).orElse(null);
    }

    @Override
    public List<Category> getCategoryByIsActive() {
        return categoryReposity.findByIsActive(true);
    }
}
