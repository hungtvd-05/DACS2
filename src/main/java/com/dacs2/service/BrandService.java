package com.dacs2.service;

import com.dacs2.model.Brand;

import java.util.List;

public interface BrandService {

    public Brand save(Brand brand);

    public Boolean existBrand(String name);

    public List<Brand> getAllBrand();

    public void deleteBrand(Integer id);

    public Brand getBrandById(Integer id);

    public List<Brand> getAllBrandIsActive();

}
