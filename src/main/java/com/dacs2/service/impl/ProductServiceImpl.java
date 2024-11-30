package com.dacs2.service.impl;

import com.dacs2.model.Product;
import com.dacs2.repository.CategoryRepository;
import com.dacs2.repository.ProductRepository;
import com.dacs2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Boolean deleteProduct(int id) {
        Product product = productRepository.findById(id).orElse(null);

        if (!ObjectUtils.isEmpty(product)) {
            productRepository.delete(product);
            return true;
        }

        return false;
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getProductByDanhMuc(String danhmuc) {
        return productRepository.findByDanhmucAndTrangthai(categoryRepository.findByName(danhmuc), true);
    }

    public static Integer convertToNumber(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public Page<Product> searchProdcutOnAdmin(Integer pageNumber, Integer pageSize, String ch) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return productRepository.findByKeywordForAdmin(pageable, convertToNumber(ch), ch);
    }

    @Override
    public Page<Product> getAllProductsForHomePagination(Integer pageNumber, Integer pageSize, String danhmuc, String thuonghieu) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        if (!ObjectUtils.isEmpty(danhmuc) && !ObjectUtils.isEmpty(thuonghieu)) {
            return productRepository.findByDanhmuc_NameAndBrand_NameAndTrangthaiTrue(pageable, danhmuc, thuonghieu);
        } else if (!ObjectUtils.isEmpty(danhmuc)) {
            return productRepository.findByDanhmuc_NameAndTrangthaiTrue(pageable, danhmuc);
        } else if (!ObjectUtils.isEmpty(thuonghieu)) {
            return productRepository.findByBrand_NameAndTrangthaiTrue(pageable, thuonghieu);
        } else {
            return productRepository.findByDanhmuc_IsActiveTrueAndBrand_StatusTrueAndTrangthaiTrue(pageable);
        }
    }

    @Override
    public Page<Product> searchProductPagination(Integer pageNumber, Integer pageSize, String ch) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> pageProduct = null;
        if (ObjectUtils.isEmpty(ch)) {
            pageProduct = productRepository.findByDanhmuc_IsActiveTrueAndTrangthaiTrue(pageable);
        } else {
            pageProduct = productRepository.findByKeywordForHome(pageable, ch);
        }
        return pageProduct;
    }

    @Override
    public Page<Product> getAllProductsPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> getProductForView(Integer id) {
        return productRepository.getProductForView(id);
    }
}
