package com.dacs2.service.impl;

import com.dacs2.model.*;
import com.dacs2.repository.*;
import com.dacs2.service.CartService;
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
import org.springframework.util.ObjectUtils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public Orders createOrder(Integer userId, OrderRequest orderRequest) throws UnsupportedEncodingException, MessagingException {
        UserDtls userDtls = userRepository.findById(userId).get();
        List<Cart> carts = cartRepository.findByUserId(userId);
        List<ProductOrder> productOrders = new ArrayList<>();

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
            productOrders.add(productOrder);

            orderName += cart.getProduct().getTen() + ": " + cart.getQuantity() + " x " + cart.getProduct().getGiaSaleFormatted() + "\n";
            totalPrice += cart.getProduct().getGiasale() * cart.getQuantity();

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
        order.setProductOrders(productOrders);
        order.setProcessed(false);
        order.setIsPaid(false);
        orderRepository.save(order);

        return order;
    }

    @Override
    public void saveOrder(Orders order) throws MessagingException, UnsupportedEncodingException {

        order.setProcessed(true);
        order.setOrderDate(new Date());
        if (order.getPaymentType().equals("ONLINE")) {
            order.setIsPaid(true);
        } else {
            order.setIsPaid(false);
        }

        orderRepository.save(order);
        cartService.clearCartByUserId(order.getUser().getId());

        commonUtil.sendMailForOrder(order, order.getStatus());

    }

    @Override
    public List<Orders> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserIdAndProcessed(userId, true).reversed();
    }

    @Override
    public Orders updateOrderStatus(Integer id, String status) throws MessagingException, UnsupportedEncodingException {
        Optional<Orders> order = orderRepository.findById(id);
        if (order.isPresent()) {
            Orders orderP = order.get();
            orderP.setStatus(status);

            if (status.equals("Đã vận chuyển thành công!")) {
                orderP.setIsPaid(true);
                List<Product> products = new ArrayList<>();
                List<ProductOrder> productOrders = productOrderRepository.findByOrderId(order.get().getOrderId());
                for (ProductOrder productOrder : productOrders) {
                    Product product = productOrder.getProduct();
                    product.setSoluong(product.getSoluong() - productOrder.getQuantity());
                    product.setSoluongDaBan(product.getSoluongDaBan() + productOrder.getQuantity());
                    products.add(product);
                }
                productRepository.saveAll(products);
                commonUtil.sendMailForOrder(orderP, status);
            }

            return orderRepository.save(orderP);
        }
        return null;
    }

    @Override
    public Page<Orders> getAllOrdersPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return orderRepository.findByProcessed(true, pageable);
    }

    @Override
    public Page<Orders> searchOrderByOrderIdPagination(Integer pageNumber, Integer pageSize, String orderId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        return orderRepository.findByOrderIdContainingIgnoreCaseAndProcessed(orderId, true, pageable);
    }

    @Override
    public Boolean deleteOrder(Orders order) {

        if (!ObjectUtils.isEmpty(order)) {
            orderRepository.delete(order);
            return true;
        }

        return false;
    }

    @Override
    public List<ProductOrder> getProductOrdersByOrderId(String orderId) {
        return productOrderRepository.findByOrderId(orderId);
    }

    @Override
    public Orders getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public ProductOrder ratingProduct(Integer userId, Integer productOrderId, String textComment, Integer rating) {
        ProductOrder productOrder = productOrderRepository.findById(productOrderId).get();

        Comment comment = new Comment();
        comment.setContent(textComment);
        comment.setUser(userRepository.findById(userId).get());
        comment.setProduct(productOrder.getProduct());
        comment.setCreatedAt(LocalDateTime.now());

        Rating ratingP = new Rating();
        ratingP.setRating(rating);
        ratingP.setComment(commentRepository.save(comment));
        ratingP.setProduct(productOrder.getProduct());

        productOrder.getProduct().setSoluongDanhgia(productOrder.getProduct().getSoluongDanhgia() + 1);
        productOrder.getProduct().setTongsoSao(productOrder.getProduct().getTongsoSao() + rating);
        productOrder.setRating(ratingRepository.save(ratingP));

        return productOrderRepository.save(productOrder);
    }
}
