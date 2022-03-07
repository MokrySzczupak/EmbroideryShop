package com.example.embroideryshop.service;

import com.example.embroideryshop.model.Role;
import com.example.embroideryshop.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getRoleByName(String name) {
        return roleRepository.findByNameLike(name);
    }
}
