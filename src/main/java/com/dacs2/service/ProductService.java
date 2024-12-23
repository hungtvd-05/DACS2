package com.dacs2.service;

import com.dacs2.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public Page<Product> getAllProductsPagination(Integer pageNumber, Integer pageSize);

    public void deleteProduct(int id);

    public Product getProductById(Integer id);

    public List<Product> getProductByDanhMuc(String danhmuc);

    public Page<Product> searchProdcutOnAdmin(Integer pageNumber, Integer pageSize, String ch);

    public Page<Product> getAllProductsForHomePagination(Integer pageNumber, Integer pageSize, String danhmuc, String thuonghieu);

    public Page<Product> searchProductPagination(Integer pageNumber, Integer pageSize, String ch);

    public List<Product> getProductForView(Integer id);

}
