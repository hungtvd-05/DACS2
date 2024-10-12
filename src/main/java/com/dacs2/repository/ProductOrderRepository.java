package com.dacs2.repository;

import com.dacs2.model.Product;
import com.dacs2.model.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    public List<ProductOrder> findByUserId(int userId);

    public List<ProductOrder> findByOrderIdContainingIgnoreCase(String orderId);

    public Page<ProductOrder> findByOrderIdContainingIgnoreCase(Pageable pageable, String orderId);

}
