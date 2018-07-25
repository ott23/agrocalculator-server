package net.tngroup.acserver.web.security.services;

import net.tngroup.acserver.databases.h2.models.User;
import net.tngroup.acserver.databases.h2.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user;

        if (username.equals("admin")) {
            user = new User();
            user.setUsername("admin");
            user.setPassword("admin");
            user.setRole("ROLE_ADMIN");
        } else {
            user = userService.getByUsername(username);
        }

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return user;
    }
}