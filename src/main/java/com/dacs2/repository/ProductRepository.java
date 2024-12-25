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

    List<Product> findByDanhmucAndBrand_StatusTrueAndTrangthaiTrueOrderByIdDesc(Category category);

    Page<Product> findAllByOrderByIdDesc(Pageable pageable);

    Page<Product> findAllByOrderBySoluongDaBanDesc(Pageable pageable);

    Page<Product> findAllByOrderByTenAsc(Pageable pageable);

    Page<Product> findAllByOrderByTenDesc(Pageable pageable);

    Page<Product> findAllByOrderByIdAsc(Pageable pageable);

    @Query(value = "SELECT p FROM Product p " +
            "WHERE (:danhmuc = '' OR p.danhmuc.name = :danhmuc) " +
            "AND (:thuonghieu = '' OR p.brand.name = :thuonghieu) " +
            "AND (:rating = 0 OR (p.soluongDanhgia > 0 AND (p.tongsoSao / p.soluongDanhgia) >= :rating)) " +
            "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR " +
            "(:minPrice IS NULL AND p.giasale <= :maxPrice) OR " +
            "(:maxPrice IS NULL AND p.giasale >= :minPrice) OR " +
            "(p.giasale BETWEEN :minPrice AND :maxPrice)) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'Từ A->Z' THEN p.ten ELSE null END ASC, " +
            "CASE WHEN :sortBy = 'Từ Z->A' THEN p.ten ELSE null END DESC, " +
            "CASE WHEN :sortBy = 'Sản phẩm cũ' THEN p.id ELSE null END ASC, " +
            "CASE WHEN :sortBy = 'Sản phẩm bán chạy' THEN p.soluongDaBan ELSE null END DESC, " +
            "CASE WHEN :sortBy = '' OR :sortBy = 'Sản phẩm mới' OR :sortBy IS NULL THEN p.id ELSE null END DESC",
            countQuery = "SELECT COUNT(p) FROM Product p " +
                    "WHERE (:danhmuc = '' OR p.danhmuc.name = :danhmuc) " +
                    "AND (:thuonghieu = '' OR p.brand.name = :thuonghieu) " +
                    "AND (:rating = 0 OR (p.soluongDanhgia > 0 AND (p.tongsoSao / p.soluongDanhgia) >= :rating)) " +
                    "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR " +
                    "(:minPrice IS NULL AND p.giasale <= :maxPrice) OR " +
                    "(:maxPrice IS NULL AND p.giasale >= :minPrice) OR " +
                    "(p.giasale BETWEEN :minPrice AND :maxPrice))")
    Page<Product> findProductsForAdmin(Pageable pageable,
                               @Param("danhmuc") String danhmuc,
                               @Param("thuonghieu") String thuonghieu,
                               @Param("rating") Integer rating,
                               @Param("sortBy") String sortBy,
                               @Param("minPrice") Double minPrice,
                               @Param("maxPrice") Double maxPrice);

    @Query(value = "SELECT p FROM Product p " +
            "WHERE p.danhmuc.isActive = true AND p.brand.status = true AND p.trangthai = true " +
            "AND (:danhmuc = '' OR p.danhmuc.name = :danhmuc) " +
            "AND (:thuonghieu = '' OR p.brand.name = :thuonghieu) " +
            "AND (:rating = 0 OR (p.soluongDanhgia > 0 AND (p.tongsoSao / p.soluongDanhgia) >= :rating)) " +
            "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR " +
            "(:minPrice IS NULL AND p.giasale <= :maxPrice) OR " +
            "(:maxPrice IS NULL AND p.giasale >= :minPrice) OR " +
            "(p.giasale BETWEEN :minPrice AND :maxPrice)) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'Từ A->Z' THEN p.ten ELSE null END ASC, " +
            "CASE WHEN :sortBy = 'Từ Z->A' THEN p.ten ELSE null END DESC, " +
            "CASE WHEN :sortBy = 'Sản phẩm cũ' THEN p.id ELSE null END ASC, " +
            "CASE WHEN :sortBy = 'Sản phẩm bán chạy' THEN p.soluongDaBan ELSE null END DESC, " +
            "CASE WHEN :sortBy = '' OR :sortBy = 'Sản phẩm mới' OR :sortBy IS NULL THEN p.id ELSE null END DESC",
            countQuery = "SELECT COUNT(p) FROM Product p " +
                    "WHERE p.danhmuc.isActive = true AND p.brand.status = true AND p.trangthai = true " +
                    "AND (:danhmuc = '' OR p.danhmuc.name = :danhmuc) " +
                    "AND (:thuonghieu = '' OR p.brand.name = :thuonghieu) " +
                    "AND (:rating = 0 OR (p.soluongDanhgia > 0 AND (p.tongsoSao / p.soluongDanhgia) >= :rating)) " +
                    "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR " +
                    "(:minPrice IS NULL AND p.giasale <= :maxPrice) OR " +
                    "(:maxPrice IS NULL AND p.giasale >= :minPrice) OR " +
                    "(p.giasale BETWEEN :minPrice AND :maxPrice))")
    Page<Product> findProductsForHome(Pageable pageable,
                                      @Param("danhmuc") String danhmuc,
                                      @Param("thuonghieu") String thuonghieu,
                                      @Param("rating") Integer rating,
                                      @Param("sortBy") String sortBy,
                                      @Param("minPrice") Double minPrice,
                                      @Param("maxPrice") Double maxPrice);

    @Query(value = "SELECT p\n" +
            "FROM Product p\n" +
            "WHERE p.danhmuc.isActive = true AND p.brand.status = true AND p.trangthai = true\n" +
            "AND (p.ten LIKE LOWER(CONCAT('%', :keyword, '%')) OR p.danhmuc.name LIKE LOWER(CONCAT('%', :keyword, '%')) OR p.brand.name LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:danhmuc = '' OR p.danhmuc.name = :danhmuc) " +
            "AND (:thuonghieu = '' OR p.brand.name = :thuonghieu) " +
            "AND (:rating = 0 OR (p.soluongDanhgia > 0 AND (p.tongsoSao / p.soluongDanhgia) >= :rating)) " +
            "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR " +
            "(:minPrice IS NULL AND p.giasale <= :maxPrice) OR " +
            "(:maxPrice IS NULL AND p.giasale >= :minPrice) OR " +
            "(p.giasale BETWEEN :minPrice AND :maxPrice)) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'Từ A->Z' THEN p.ten ELSE null END ASC, " +
            "CASE WHEN :sortBy = 'Từ Z->A' THEN p.ten ELSE null END DESC, " +
            "CASE WHEN :sortBy = 'Sản phẩm cũ' THEN p.id ELSE null END ASC, " +
            "CASE WHEN :sortBy = 'Sản phẩm bán chạy' THEN p.soluongDaBan ELSE null END DESC, " +
            "CASE WHEN :sortBy = '' OR :sortBy = 'Sản phẩm mới' OR :sortBy IS NULL THEN p.id ELSE null END DESC",
            countQuery = "SELECT COUNT(p) FROM Product p " +
                    "WHERE p.danhmuc.isActive = true AND p.brand.status = true AND p.trangthai = true " +
                    "AND (:keyword = '' OR (LOWER(p.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "    OR LOWER(p.danhmuc.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "    OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
                    "AND (:danhmuc = '' OR p.danhmuc.name = :danhmuc) " +
                    "AND (:thuonghieu = '' OR p.brand.name = :thuonghieu) " +
                    "AND (:rating = 0 OR (p.soluongDanhgia > 0 AND (p.tongsoSao / p.soluongDanhgia) >= :rating)) " +
                    "AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR " +
                    "(:minPrice IS NULL AND p.giasale <= :maxPrice) OR " +
                    "(:maxPrice IS NULL AND p.giasale >= :minPrice) OR " +
                    "(p.giasale BETWEEN :minPrice AND :maxPrice))")
    Page<Product> findByKeywordForHome(Pageable pageable,
                                       @Param("keyword") String keyword,
                                       @Param("danhmuc") String danhmuc,
                                       @Param("thuonghieu") String thuonghieu,
                                       @Param("rating") Integer rating,
                                       @Param("sortBy") String sortBy,
                                       @Param("minPrice") Double minPrice,
                                       @Param("maxPrice") Double maxPrice);

    @Query("SELECT p\n" +
            "FROM Product p\n" +
            "WHERE (p.id = :id OR p.ten LIKE LOWER(CONCAT('%', :keyword, '%')) OR p.danhmuc.name LIKE LOWER(CONCAT('%', :keyword, '%')) OR p.brand.name LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "ORDER BY " +
            "CASE WHEN :sortBy = '' THEN p.id END DESC, " +
            "CASE WHEN :sortBy = 'Từ A->Z' THEN p.ten END ASC, " +
            "CASE WHEN :sortBy = 'Từ Z->A' THEN p.ten END DESC, " +
            "CASE WHEN :sortBy = 'Sản phẩm mới' THEN p.id END DESC," +
            "CASE WHEN :sortBy = 'Sản phẩm cũ' THEN p.id END ASC, " +
            "CASE WHEN :sortBy = 'Sản phẩm bán chạy' THEN p.soluongDaBan END DESC")
    Page<Product> findByKeywordForAdmin(Pageable pageable,
                                        @Param("id") Integer id,
                                        @Param("keyword") String keyword,
                                        @Param("sortBy") String sortBy);

    @Query("SELECT p FROM Product p WHERE p.id != :keyword AND p.brand.status = true AND p.danhmuc.isActive = true ORDER BY p.id DESC LIMIT 6")
    List<Product> getProductForView(@Param("keyword") Integer keyword);

    void deleteAllByBrand(Brand brand);

    void deleteAllByDanhmuc(Category category);

    List<Product> findByDanhmuc_IsActiveTrueAndBrand_StatusTrueAndTrangthaiTrueOrderBySoluongDaBanDesc();

}
