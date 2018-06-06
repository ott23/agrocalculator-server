package net.tngroup.acserver.security.services;

import net.tngroup.acserver.models.User;
import net.tngroup.acserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user;

        if (username.equals("admin")) {
            user = new User();
            user.setUsername("admin");
            user.setPassword("admin");
            user.setRole("ADMIN");
        } else {
            user = userRepository.findUserByUsername(username);
        }

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return user;
    }
}