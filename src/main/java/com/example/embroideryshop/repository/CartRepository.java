package com.example.embroideryshop.repository;

import com.example.embroideryshop.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c")
    List<Cart> getAllCarts();

    @Query("UPDATE Cart c SET c.completed = true WHERE c.id = ?1")
    @Modifying
    void setCartCompleted(Long id);
}
