package com.dacs2.service.impl;

import com.dacs2.model.Product;
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

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
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
    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public int getMaxProductId() {
        return productRepository.findMaxId();
    }

    @Override
    public List<Product> getProductByDanhMuc(String danhmuc) {
        return productRepository.findByDanhmucAndTrangthai(danhmuc, true);
    }

    @Override
    public List<Product> getAllProductsForHome(String danhmuc) {
        List<Product> products = null;
        if (ObjectUtils.isEmpty(danhmuc)) {
            products = productRepository.findAllProductsForHome();
        } else {
            products = productRepository.findByDanhmucAndTrangthai(danhmuc, true);
        }
        return products;
    }

    @Override
    public List<Product> searchProduct(String ch) {
        return productRepository.findByTenContainingIgnoreCaseOrDanhmucContainingIgnoreCase(ch, ch);
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
        return productRepository.findByIdOrTenContainingIgnoreCaseOrDanhmucContainingIgnoreCase(pageable, convertToNumber(ch), ch, ch);
    }

    @Override
    public Page<Product> getAllProductsForHomePagination(Integer pageNumber, Integer pageSize, String danhmuc) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> pageProduct = null;
        if (ObjectUtils.isEmpty(danhmuc)) {
            pageProduct = productRepository.findAllProductsForHome(pageable);
        } else {
            pageProduct = productRepository.findByDanhmucAndTrangthai(pageable, danhmuc, true);
        }

        return pageProduct;
    }

    @Override
    public Page<Product> searchProductPagination(Integer pageNumber, Integer pageSize, String ch) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> pageProduct = null;
        if (ObjectUtils.isEmpty(ch)) {
            pageProduct = productRepository.findAllProductsForHome(pageable);
        } else {
//            pageProduct = productRepository.findByTenContainingIgnoreCaseOrDanhmucContainingIgnoreCase(pageable, ch, ch);
            pageProduct = productRepository.findByKeyword(pageable, ch);
        }
        return pageProduct;
    }

    @Override
    public Page<Product> getAllProductsPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return productRepository.findAll(pageable);
    }
}
