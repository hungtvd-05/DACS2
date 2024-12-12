package com.dacs2.service.impl;

import com.dacs2.model.ProductOrder;
import com.dacs2.repository.ProductOrderRepository;
import com.dacs2.service.ProductOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Override
    public List<ProductOrder> getProductOrdersIsRated() {
        return productOrderRepository.findByRatingIsNotNull().reversed();
    }
}
