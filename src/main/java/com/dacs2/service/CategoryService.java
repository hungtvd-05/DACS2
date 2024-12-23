package com.dacs2.service;

import com.dacs2.model.Category;

import java.util.List;


public interface CategoryService {

    public Category saveCategory(Category category);

    public Boolean existCategory(String name);

    public List<Category> getAllCategory();

    public void deleteCategory(Long id);

    public Category getCategoryById(Long id);

    public List<Category> getCategoryByIsActive();

}
