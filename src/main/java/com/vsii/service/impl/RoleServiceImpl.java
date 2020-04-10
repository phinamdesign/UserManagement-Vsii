package com.vsii.service.impl;

import com.vsii.model.Role;
import com.vsii.model.RoleName;
import com.vsii.repository.RoleRepository;
import com.vsii.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(RoleName roleName) {
        return roleRepository.findByName(roleName);
    }
}
