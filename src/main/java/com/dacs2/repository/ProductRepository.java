package com.dacs2.repository;

import com.dacs2.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT COALESCE(MAX(id), 0) FROM Product")
    int findMaxId();

    List<Product> findByDanhmuc(String danhmuc);

    @Query("SELECT p\n" +
            "FROM Product p\n" +
            "INNER JOIN Category c ON p.danhmuc = c.name\n" +
            "WHERE c.isActive = true AND p.trangthai = true")
    List<Product> findAllProductsForHome();

    @Query("SELECT p\n" +
            "FROM Product p\n" +
            "INNER JOIN Category c ON p.danhmuc = c.name\n" +
            "WHERE c.isActive = true AND p.trangthai = true")
    Page<Product> findAllProductsForHome(Pageable pageable);


    List<Product> findByDanhmucAndTrangthai(String danhmuc, Boolean trangthai);

    Page<Product> findByDanhmucAndTrangthai(Pageable pageable, String danhmuc, Boolean trangthai);

    List<Product> findByTenContainingIgnoreCaseOrDanhmucContainingIgnoreCase(String ch, String ch2);

    Page<Product> findByTenContainingIgnoreCaseOrDanhmucContainingIgnoreCase(Pageable pageable, String ch, String ch2);

    @Query("SELECT p\n" +
            "FROM Product p\n" +
            "INNER JOIN Category c ON p.danhmuc = c.name\n" +
            "WHERE c.isActive = true AND p.trangthai = true\n" +
            "AND (p.ten LIKE CONCAT('%', :keyword, '%') OR c.name LIKE CONCAT('%', :keyword, '%'))")
    Page<Product> findByKeyword(Pageable pageable, @Param("keyword") String keyword);

    Page<Product> findByIdOrTenContainingIgnoreCaseOrDanhmucContainingIgnoreCase(Pageable pageable, int id, String ch, String ch2);

}
