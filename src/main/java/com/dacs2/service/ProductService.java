package com.dacs2.service;

import com.dacs2.model.Category;
import com.dacs2.model.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public Page<Product> getAllProductsPagination(Integer pageNumber, Integer pageSize, String sapxep);

    public void deleteProduct(int id);

    public Product getProductById(Integer id);

    public List<Product> getProductByDanhMuc(Category danhmuc);

    public Page<Product> searchProdcutOnAdmin(Integer pageNumber, Integer pageSize, String ch, String sapxep);

    public Page<Product> getAllProductsForHomePagination(Integer pageNumber, Integer pageSize, String danhmuc, String thuonghieu, String sapxep, String danhGia, Double minPrice, Double maxPrice);

    public Page<Product> searchProductPagination(Integer pageNumber, Integer pageSize, String ch, String danhmuc, String thuonghieu, String sapxep, String danhGia, Double minPrice, Double maxPrice);

    public List<Product> getProductForView(Integer id);

    public List<Product> getProductTop();

}
