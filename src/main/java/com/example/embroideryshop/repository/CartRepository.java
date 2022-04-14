package com.example.embroideryshop.repository;

import com.example.embroideryshop.model.Cart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c")
    List<Cart> getAllCarts(Pageable pageable);

    @Query("UPDATE Cart c SET c.completed = true WHERE c.id = ?1")
    @Modifying
    void setCartCompleted(Long cartId);

    @Query("SELECT c FROM Cart c WHERE c.id = ?1")
    Cart getSingleCartById(Long cartId);

    @Query("SELECT c FROM Cart c WHERE c.user.id = ?1 AND c.paid=false")
    Cart getCartByUser(Long userId);

    @Query("SELECT c FROM Cart c WHERE c.user.id = ?1")
    List<Cart> getAllCartsForUser(Long userId);

    @Query("SELECT COUNT(c.id) FROM Cart c WHERE c.status NOT LIKE 'requires_payment_method'")
    int countFinalizedCarts();
}
