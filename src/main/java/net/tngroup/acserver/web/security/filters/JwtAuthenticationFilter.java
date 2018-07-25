package net.tngroup.acserver.web.security.filters;

import net.tngroup.acserver.web.security.services.TokenAuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService) {
        super();
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        Authentication authentication = TokenAuthenticationService.getAuthentication((HttpServletRequest) request, userDetailsService);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            Long expiration = (Long) authentication.getDetails();
            Long currentTime = System.currentTimeMillis();
            if (expiration - currentTime < TokenAuthenticationService.EXPIRATION_TIME * 0.8) {
                Optional<String> authority = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst();
                TokenAuthenticationService.addAuthentication((HttpServletResponse) response, authentication.getName(), authority.orElse(null));
            }
        } catch (NullPointerException e) {
            // Skip after null exception
        }

        filterChain.doFilter(request, response);
    }
}