package net.tngroup.rest.services;

import net.tngroup.rest.domain.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserAuthenticationService {

    Map<String, User> users = new HashMap<>();

    public Optional<String> login(final String username, final String password) {
        final String token = UUID.randomUUID().toString();
        final User user = User
                .builder()
                .id(1)
                .username(username)
                .password(password)
                .token(token)
                .build();

        users.put(token, user);
        return Optional.of(token);
    }

    public Optional<User> findByToken(final String token) {
        return Optional.ofNullable(users.get(token));
    }

    public void logout(final User user) {
        users.remove(user.getToken());
    }

}
