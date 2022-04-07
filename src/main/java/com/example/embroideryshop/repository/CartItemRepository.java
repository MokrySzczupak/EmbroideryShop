package com.example.embroideryshop.repository;

import com.example.embroideryshop.model.CartItem;
import com.example.embroideryshop.model.Product;
import com.example.embroideryshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c WHERE c.sold=false AND c.user=?1")
    List<CartItem> findByUser(User user);
    @Query("SELECT c FROM CartItem c WHERE c.user.id = ?1 AND c.product.id = ?2 AND c.sold = false")
    CartItem findByUserAndProduct(long userId, long productId);

    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.product.id = ?2 AND c.user.id = ?3 AND c.sold = false")
    @Modifying
    void updateQuantity(int quantity, long productId, long userId);

    @Query("DELETE FROM CartItem c WHERE c.user.id = ?1 AND c.product.id = ?2 AND c.sold = false")
    @Modifying
    void removeByUserAndProduct(long userId, long productId);
}
