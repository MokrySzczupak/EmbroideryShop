package com.example.embroideryshop.repository;

import com.example.embroideryshop.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByNameLikeIgnoreCase(String name, Pageable pageable);
    List<Product> findAllByCategory_CategoryId(long id, Pageable pageable);
    @Query("SELECT p FROM Product p")
    List<Product> findAllProducts(Pageable pageable);
    int countProductBy();
    int countProductByCategory_CategoryId(long id);
    int countProductByNameLikeIgnoreCase(String name);
}
