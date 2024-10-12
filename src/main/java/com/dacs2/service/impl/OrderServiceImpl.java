package com.dacs2.service.impl;

import com.dacs2.model.Cart;
import com.dacs2.model.OrderAddress;
import com.dacs2.model.OrderRequest;
import com.dacs2.model.ProductOrder;
import com.dacs2.repository.CartRepository;
import com.dacs2.repository.ProductOrderRepository;
import com.dacs2.service.OrderService;
import com.dacs2.util.CommonUtil;
import com.dacs2.util.OrderStatus;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException {

        List<Cart> carts = cartRepository.findByUserId(userId);

        for (Cart cart : carts) {

            ProductOrder order = new ProductOrder();
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(new Date());

            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getGiasale());

            order.setQuantity(cart.getQuantity());
            order.setUser(cart.getUser());

            order.setStatus(OrderStatus.IN_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            OrderAddress address = new OrderAddress();
            address.setFullName(orderRequest.getFullName());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setPrefecture(orderRequest.getPrefecture());
            address.setWard(orderRequest.getWard());
            address.setPhoneNumber(orderRequest.getPhoneNumber());

            order.setOrderAddress(address);

            commonUtil.sendMailForProductOrder(order, order.getStatus());

            orderRepository.save(order);

        }

    }

    @Override
    public List<ProductOrder> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) throws MessagingException, UnsupportedEncodingException {
        Optional<ProductOrder> order = orderRepository.findById(id);
        if (order.isPresent()) {
            ProductOrder productOrder = order.get();
            productOrder.setStatus(status);

            commonUtil.sendMailForProductOrder(productOrder, status);

            return orderRepository.save(productOrder);
        }
        return null;
    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<ProductOrder> searchOrderByOrderIdPagination(Integer pageNumber, Integer pageSize, String orderId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return orderRepository.findByOrderIdContainingIgnoreCase(pageable, orderId);
    }
}
