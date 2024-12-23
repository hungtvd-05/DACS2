package com.dacs2.repository;

import com.dacs2.model.Cart;
import com.dacs2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    public Cart findByProductIdAndUserId(Integer productId, Integer userId);

    public Integer countByUserId(Integer userId);

    public List<Cart> findByUserId(Integer userId);

    @Query("SELECT SUM(c.product.giasale * c.quantity) FROM Cart c WHERE c.user.id = :userId")
    public Double sumPriceByUserId(Integer userId);

    void deleteAllByProduct(Product product);
}
