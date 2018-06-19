package net.tngroup.acserver.services;

import net.tngroup.acserver.models.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(int id);

    User getByUsername(String username);

    void add(User user);

    void deleteById(int id);

}
