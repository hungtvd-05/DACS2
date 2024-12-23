package com.dacs2.repository;

import com.dacs2.model.Brand;
import com.dacs2.model.Category;
import com.dacs2.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Page<Product> findByDanhmuc_IsActiveTrueAndTrangthaiTrue(Pageable pageable);

    Page<Product> findByDanhmuc_IsActiveTrueAndBrand_StatusTrueAndTrangthaiTrue(Pageable pageable);

    List<Product> findByDanhmucAndTrangthai(Category category, Boolean trangthai);

    Page<Product> findByDanhmuc_NameAndTrangthaiTrue(Pageable pageable, String name);

    Page<Product> findByBrand_NameAndTrangthaiTrue(Pageable pageable, String name);

    Page<Product> findByDanhmuc_NameAndBrand_NameAndTrangthaiTrue(Pageable pageable, String danhmucName, String brandName);

    @Query("SELECT p\n" +
            "FROM Product p\n" +
            "WHERE p.danhmuc.isActive = true AND p.brand.status = true AND p.trangthai = true\n" +
            "AND (p.ten LIKE LOWER(CONCAT('%', :keyword, '%')) OR p.danhmuc.name LIKE LOWER(CONCAT('%', :keyword, '%')) OR p.brand.name LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findByKeywordForHome(Pageable pageable, @Param("keyword") String keyword);

    @Query("SELECT p\n" +
            "FROM Product p\n" +
            "WHERE (p.id = :id OR p.ten LIKE LOWER(CONCAT('%', :keyword, '%')) OR p.danhmuc.name LIKE LOWER(CONCAT('%', :keyword, '%')) OR p.brand.name LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findByKeywordForAdmin(Pageable pageable, @Param("id") Integer id, @Param("keyword") String keyword);

    @Query(value = "SELECT * FROM product WHERE id != :keyword ORDER BY id DESC LIMIT 6", nativeQuery = true)
    List<Product> getProductForView(@Param("keyword") Integer keyword);

    void deleteAllByBrand(Brand brand);

    void deleteAllByDanhmuc(Category category);

}
