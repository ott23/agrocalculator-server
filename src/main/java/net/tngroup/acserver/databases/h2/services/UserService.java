package net.tngroup.acserver.databases.h2.services;

import net.tngroup.acserver.databases.h2.models.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    List<User> getAllByUsername(String username);

    User getByUsername(String username);

    User getById(int id);

    void save(User user);

    void deleteById(int id);

}
