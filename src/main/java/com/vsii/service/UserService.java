package com.vsii.service;

import com.vsii.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);

    Optional<User> findById(Long id);

    void save(User user);

    Iterable<User> findAll();

    void delete(Long id);

    Iterable<User> findUsersByNameContaining(String user_name);
}
