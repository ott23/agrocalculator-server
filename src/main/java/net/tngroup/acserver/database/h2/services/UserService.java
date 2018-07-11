package net.tngroup.acserver.database.h2.services;

import net.tngroup.acserver.database.h2.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAll();

    List<User> getAllByUsername(String username);

    User getByUsername(String username);

    User getById(int id);

    void save(User user);

    void deleteById(int id);

}
