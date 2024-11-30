package com.dacs2.service.impl;

import com.dacs2.model.Product;
import com.dacs2.model.Rating;
import com.dacs2.repository.RatingRepository;
import com.dacs2.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public List<Rating> getRatingsByProductId(Product product) {
        return ratingRepository.findByProduct(product);
    }
    
    @Override
    public Integer count(Product product, Integer rating) {
        return ratingRepository.countByProductAndRating(product, rating);
    }
}
