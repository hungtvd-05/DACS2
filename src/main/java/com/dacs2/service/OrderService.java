package com.dacs2.service;

import com.dacs2.model.OrderRequest;
import com.dacs2.model.ProductOrder;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException;

    public List<ProductOrder> getOrdersByUserId(Integer userId);

    public ProductOrder updateOrderStatus(Integer id, String status) throws MessagingException, UnsupportedEncodingException;

    public Page<ProductOrder> getAllOrdersPagination(Integer pageNumber, Integer pageSize);

    public Page<ProductOrder> searchOrderByOrderIdPagination(Integer pageNumber, Integer pageSize, String orderId);

}
