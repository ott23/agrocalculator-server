package net.tngroup.acserver.security.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class TokenAuthenticationService {
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    private static final String SECRET = "DevelopmentSecret";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String REQUEST_HEADER_STRING = "Authorization";
    private static final String TOKEN_HEADER_STRING = "X-Token";

    public static void addAuthentication(HttpServletResponse res, String username, String authority) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode subjectJson = mapper.createObjectNode();
        subjectJson.put("username", username);
        subjectJson.put("authority", authority);
        String subject = subjectJson.toString();

        String JWT = Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET)
                .compact();

        res.addHeader(TOKEN_HEADER_STRING, JWT);
    }


    public static Authentication getAuthentication(HttpServletRequest request, UserDetailsService userDetailsService) {

        String token = request.getHeader(REQUEST_HEADER_STRING);

        if (token != null) {

            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();

            String subject = claims.getSubject();
            Long expiration = claims.getExpiration().getTime();

            UserDetails user;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String username = objectMapper.readTree(subject).get("username").textValue();
                user = userDetailsService.loadUserByUsername(username);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            if (user == null) return null;
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
            ((UsernamePasswordAuthenticationToken) authentication).setDetails(expiration);
            return authentication;
        }
        return null;
    }
}