package com.dacs2.service;

import com.dacs2.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public List<Product> getAllProducts();

    public Page<Product> getAllProductsPagination(Integer pageNumber, Integer pageSize);

    public Boolean deleteProduct(int id);

    public Product getProductById(int id);

    public int getMaxProductId();

    public List<Product> getProductByDanhMuc(String danhmuc);

    public List<Product> getAllProductsForHome(String danhmuc);

    public List<Product> searchProduct(String ch);

    public Page<Product> searchProdcutOnAdmin(Integer pageNumber, Integer pageSize, String ch);

    public Page<Product> getAllProductsForHomePagination(Integer pageNumber, Integer pageSize, String danhmuc);

    public Page<Product> searchProductPagination(Integer pageNumber, Integer pageSize, String ch);

}
