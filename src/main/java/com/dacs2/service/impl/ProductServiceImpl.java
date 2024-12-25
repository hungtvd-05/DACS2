package com.dacs2.service.impl;

import com.dacs2.model.Category;
import com.dacs2.model.Product;
import com.dacs2.repository.CartRepository;
import com.dacs2.repository.ProductRepository;
import com.dacs2.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProduct(int id) {
        Product product = productRepository.findById(id).get();

        if (!ObjectUtils.isEmpty(product)) {
            cartRepository.deleteAllByProduct(product);
            productRepository.delete(product);
        }

    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getProductByDanhMuc(Category danhmuc) {
        return productRepository.findByDanhmucAndBrand_StatusTrueAndTrangthaiTrueOrderByIdDesc(danhmuc);
    }

    public static Integer convertToNumber(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public Page<Product> searchProdcutOnAdmin(Integer pageNumber, Integer pageSize, String ch, String sapxep) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return productRepository.findByKeywordForAdmin(pageable, convertToNumber(ch), ch, sapxep);
    }

    @Override
    public Page<Product> getAllProductsForHomePagination(Integer pageNumber, Integer pageSize, String danhmuc, String thuonghieu, String sapxep, String danhGia, Double minPrice, Double maxPrice) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Integer rating = 0;

        switch (danhGia) {
            case "Từ 1 sao":
                rating = 1;
                break;
            case "Từ 2 sao":
                rating = 2;
                break;
            case "Từ 3 sao":
                rating = 3;
                break;
            case "Từ 4 sao":
                rating = 4;
                break;
            case "Từ 5 sao":
                rating = 5;
                break;
        }

        return productRepository.findProductsForHome(pageable, danhmuc, thuonghieu, rating, sapxep, minPrice, maxPrice);
    }

    @Override
    public Page<Product> searchProductPagination(Integer pageNumber, Integer pageSize, String ch, String danhmuc, String thuonghieu, String sapxep, String danhGia, Double minPrice, Double maxPrice) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Integer rating = 0;

        switch (danhGia) {
            case "Từ 1 sao":
                rating = 1;
                break;
            case "Từ 2 sao":
                rating = 2;
                break;
            case "Từ 3 sao":
                rating = 3;
                break;
            case "Từ 4 sao":
                rating = 4;
                break;
            case "Từ 5 sao":
                rating = 5;
                break;
        }

        return productRepository.findByKeywordForHome(pageable, ch, danhmuc, thuonghieu, rating, sapxep, minPrice, maxPrice);
    }

    @Override
    public Page<Product> getAllProductsPagination(Integer pageNumber, Integer pageSize, String sapxep) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return productRepository.findProductsForAdmin(pageable, "", "", 0, sapxep, null, null);
    }

    @Override
    public List<Product> getProductForView(Integer id) {
        return productRepository.getProductForView(id);
    }

    @Override
    public List<Product> getProductTop() {
        return productRepository.findByDanhmuc_IsActiveTrueAndBrand_StatusTrueAndTrangthaiTrueOrderBySoluongDaBanDesc().stream().limit(6).collect(Collectors.toList());
    }
}
