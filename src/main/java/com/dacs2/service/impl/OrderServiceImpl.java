package com.dacs2.service.impl;

import com.dacs2.model.*;
import com.dacs2.repository.CartRepository;
import com.dacs2.repository.OrderRepository;
import com.dacs2.repository.ProductOrderRepository;
import com.dacs2.repository.UserRepository;
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
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException {

        UserDtls userDtls = userRepository.findById(userId).get();
        List<Cart> carts = cartRepository.findByUserId(userId);

        String orderId = UUID.randomUUID().toString();
        String orderName = "";
        Double totalPrice = 0.0;
        OrderAddress address = new OrderAddress();
        address.setFullName(orderRequest.getFullName());
        address.setAddress(orderRequest.getAddress());
        address.setCity(orderRequest.getCity());
        address.setPrefecture(orderRequest.getPrefecture());
        address.setWard(orderRequest.getWard());
        address.setPhoneNumber(orderRequest.getPhoneNumber());

        for (Cart cart : carts) {

            ProductOrder productOrder = new ProductOrder();
            productOrder.setOrderId(orderId);

            productOrder.setProduct(cart.getProduct());
            productOrder.setPrice(cart.getProduct().getGiasale());

            productOrder.setQuantity(cart.getQuantity());

            orderName += cart.getProduct().getTen() + ": " + cart.getQuantity() + " x " + cart.getProduct().getGiaSaleFormatted() + "\n";
            totalPrice += cart.getProduct().getGiasale() * cart.getQuantity();

            productOrderRepository.save(productOrder);

        }

        Orders order = new Orders();
        order.setOrderId(orderId);
        order.setOrderName(orderName);
        order.setOrderDate(new Date());
        order.setOrderAddress(address);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.IN_PROGRESS.getName());
        order.setUser(userDtls);
        order.setPaymentType(orderRequest.getPaymentType());
        if (orderRequest.getPaymentType().equals("ONLINE")) {
            order.setIsPaid(true);
        } else {
            order.setIsPaid(false);
        }

        commonUtil.sendMailForOrder(order, order.getStatus());

        orderRepository.save(order);

    }

    @Override
    public List<Orders> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Orders updateOrderStatus(Integer id, String status) throws MessagingException, UnsupportedEncodingException {
        Optional<Orders> order = orderRepository.findById(id);
        if (order.isPresent()) {
            Orders orderP = order.get();
            orderP.setStatus(status);

            commonUtil.sendMailForOrder(orderP, status);

            return orderRepository.save(orderP);
        }
        return null;
    }

    @Override
    public Page<Orders> getAllOrdersPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Orders> searchOrderByOrderIdPagination(Integer pageNumber, Integer pageSize, String orderId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return orderRepository.findByOrderIdContainingIgnoreCase(pageable, orderId);
    }
}
