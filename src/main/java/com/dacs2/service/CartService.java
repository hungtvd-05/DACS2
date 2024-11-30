package com.dacs2.service;

import com.dacs2.model.Cart;

import java.util.List;

public interface CartService {

    public Cart saveCart(Integer productId, Integer userId);

    public List<Cart> getCartByUser(Integer userId);

    public Integer getCountCart(Integer userId);

    public Boolean updateQuantity(Integer id, Integer quantity);

    public Boolean deleteCart(Integer id);

    public Double getTotalPrice(Integer userId);

    public Boolean clearCartByUserId(Integer userId);

}
