package net.tngroup.acserver.repositories;

import net.tngroup.acserver.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserById(int id);

    User findUserByUsername(String username);
}
