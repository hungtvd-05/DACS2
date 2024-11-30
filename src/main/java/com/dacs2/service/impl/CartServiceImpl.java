package com.dacs2.service.impl;

import com.dacs2.model.Cart;
import com.dacs2.model.Product;
import com.dacs2.model.UserDtls;
import com.dacs2.repository.CartRepository;
import com.dacs2.repository.ProductRepository;
import com.dacs2.repository.UserRepository;
import com.dacs2.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Cart> getCartByUser(Integer userId) {
        List<Cart> cartList = cartRepository.findByUserId(userId);

        List<Cart> updateCarts = new ArrayList<>();
        for (Cart cart : cartList) {
            cart.setTotalPrice(cart.getProduct().getGiasale() * cart.getQuantity());
            updateCarts.add(cart);
        }

        return cartList;
    }

    @Override
    public Cart saveCart(Integer productId, Integer userId) {

        UserDtls userDtls = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        Cart cart = cartRepository.findByProductIdAndUserId(productId, userId);

        if (ObjectUtils.isEmpty(cart)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);
            cart.setTotalPrice(product.getGiasale());
        } else {
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(product.getGiasale() * cart.getQuantity());
        }

        return cartRepository.save(cart);
    }

    @Override
    public Integer getCountCart(Integer userId) {

        return cartRepository.countByUserId(userId);

    }

    @Override
    public Boolean updateQuantity(Integer id, Integer quantity) {

        Cart cart = cartRepository.findById(id).get();

        cart.setQuantity(quantity);

        if (!ObjectUtils.isEmpty(cartRepository.save(cart))) {
            return true;
        }

        return false;
    }

    @Override
    public Boolean deleteCart(Integer id) {

        Cart cart = cartRepository.findById(id).get();

        if (!ObjectUtils.isEmpty(cart)) {
            cartRepository.delete(cart);
            return true;
        }

        return false;
    }

    @Override
    public Double getTotalPrice(Integer userId) {
        return cartRepository.sumPriceByUserId(userId);
    }

    @Override
    public Boolean clearCartByUserId(Integer userId) {

        List<Cart> cartList = cartRepository.findByUserId(userId);

        if (!ObjectUtils.isEmpty(cartList)) {
            cartRepository.deleteAll(cartList);
            return true;
        }

        return false;
    }
}
