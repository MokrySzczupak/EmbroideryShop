package com.example.embroideryshop.repository;

import com.example.embroideryshop.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByNameLike(String name);
}
