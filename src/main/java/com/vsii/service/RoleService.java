package com.vsii.service;
import com.vsii.model.Role;
import com.vsii.model.RoleName;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(RoleName roleName);
}
