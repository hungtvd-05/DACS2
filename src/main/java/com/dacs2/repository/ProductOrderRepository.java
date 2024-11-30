package com.dacs2.repository;

import com.dacs2.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    public List<ProductOrder> findByOrderId(String orderId);

    public List<ProductOrder> findByRatingIsNotNull();

}
