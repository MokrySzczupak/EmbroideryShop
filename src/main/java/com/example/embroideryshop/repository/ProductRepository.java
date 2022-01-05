package com.example.embroideryshop.repository;

import com.example.embroideryshop.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByNameLikeIgnoreCase(String name, Pageable pageable);
    List<Product> findAllByCategory_CategoryId(long id, Pageable pageable);
}
