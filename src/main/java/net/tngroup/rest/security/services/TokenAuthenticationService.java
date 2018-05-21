package net.tngroup.rest.security.services;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Date;

public class TokenAuthenticationService {
    static final long EXPIRATION_TIME = 864_000_000; // 10 days
    static final String SECRET = "ThisIsASecret";
    static final String TOKEN_PREFIX = "Bearer";
    static final String REQUEST_HEADER_STRING = "Authorization";
    static final String TOKEN_HEADER_STRING = "X-Token";

    public static void addAuthentication(HttpServletResponse res, String username) {
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET)
                .compact();
        res.addHeader(TOKEN_HEADER_STRING, JWT);
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(REQUEST_HEADER_STRING);
        if (token != null) {
            String user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

            return user != null ? new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList()) : null;
        }
        return null;
    }
}