package com.dacs2.service;

import com.dacs2.model.Product;
import com.dacs2.model.Rating;

import java.util.List;

public interface RatingService {

    public List<Rating> getRatingsByProductId(Product product);

    public Integer count(Product product, Integer rating);

}
