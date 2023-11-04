package com.onyshkiv.libraryspring.util;

import com.onyshkiv.libraryspring.entity.Role;
import com.onyshkiv.libraryspring.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private final String secret_key = "javainuse-secret-key";
    private long accessTokenValidity = 60 * 60 * 1000;

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil() {
        this.jwtParser = Jwts.parser().verifyWith(getSecretKey()).build();
    }

    private SecretKey getSecretKey() {

        byte[] bytes = Decoders.BASE64.decode(secret_key);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(User user) {
        Claims claims = Jwts.claims().subject(user.getLogin()).build();
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("email", user.getEmail());
        claims.put("phone", user.getPhone());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        return Jwts.builder()
                .claims(claims)
                .expiration(tokenValidity)
                .signWith(getSecretKey())
                .compact();
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getLogin(Claims claims) {
        return claims.getSubject();
    }
//todo
    private Role getRoles(Claims claims) {
        return (Role) claims.get("roles");
    }

}
