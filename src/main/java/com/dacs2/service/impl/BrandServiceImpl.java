package com.dacs2.service.impl;

import com.dacs2.model.Brand;
import com.dacs2.repository.BrandRepository;
import com.dacs2.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    public Boolean existBrand(String name) {
        return brandRepository.existsByName(name);
    }

    @Override
    public List<Brand> getAllBrand() {
        return brandRepository.findAll();
    }

    @Override
    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id).get();
        brandRepository.delete(brand);
    }

    @Override
    public Brand getBrandById(Integer id) {
        return brandRepository.findById(id).get();
    }

    @Override
    public List<Brand> getAllBrandIsActive() {
        return brandRepository.findByStatus(true);
    }
}
