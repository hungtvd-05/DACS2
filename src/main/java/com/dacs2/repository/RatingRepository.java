package com.dacs2.repository;

import com.dacs2.model.Product;
import com.dacs2.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProduct(Product product);
    Integer countByProductAndRating(Product product, Integer rating);
}
