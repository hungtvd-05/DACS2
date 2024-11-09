package com.dacs2.service;

import com.dacs2.model.Orders;
import com.dacs2.model.OrderRequest;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException;

    public List<Orders> getOrdersByUserId(Integer userId);

    public Orders updateOrderStatus(Integer id, String status) throws MessagingException, UnsupportedEncodingException;

    public Page<Orders> getAllOrdersPagination(Integer pageNumber, Integer pageSize);

    public Page<Orders> searchOrderByOrderIdPagination(Integer pageNumber, Integer pageSize, String orderId);

}
