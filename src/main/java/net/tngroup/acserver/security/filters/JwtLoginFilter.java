package net.tngroup.acserver.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.tngroup.acserver.security.models.Credentials;
import net.tngroup.acserver.security.services.TokenAuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    public JwtLoginFilter(String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res)
            throws AuthenticationException, IOException {
        Credentials credentials = new ObjectMapper().readValue(req.getInputStream(), Credentials.class);
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        credentials.getPassword(),
                        Collections.emptyList()
                )
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {

        String authority = "";
        Optional<String> optional = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst();
        if (optional.isPresent()) {
            authority = optional.get();
        }
        TokenAuthenticationService.addAuthentication(res, auth.getName(), authority);
    }
}