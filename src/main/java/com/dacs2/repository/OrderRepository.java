package com.dacs2.repository;

import com.dacs2.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Integer> {

    public List<Orders> findByUserId(int userId);

    public Page<Orders> findByOrderIdContainingIgnoreCase(Pageable pageable, String orderId);

}
