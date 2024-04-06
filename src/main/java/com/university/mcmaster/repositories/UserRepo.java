package com.university.mcmaster.repositories;

import com.university.mcmaster.models.entities.User;

import java.util.Map;

public interface UserRepo {
    User findUserByEmail(String username);
    User findById(String id);
    boolean save(User user);
    void update(String userId,Map<String, Object> updateMap);
}
