package com.onyshkiv.libraryspring.util;

import com.onyshkiv.libraryspring.entity.Role;
import com.onyshkiv.libraryspring.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${secret_key}")
    private String secret_key ;
    private long accessTokenValidity = 60 * 60 * 1000;




    public String extractUsername(String token) {
//        return extractClaim(token,Claims::getSubject); або так
        return extractAllClaims(token).getSubject();// бо запихаю в сабджект юсернейм
    }


    //todo що за магія
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(getSecretKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            System.out.printf("Invalid JWT signature: {%s}\n", e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.printf("Invalid JWT token: {%s}\n", e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.printf("JWT token is expired: {%s}\n", e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.printf("JWT token is unsupported: {%s}\n", e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.printf("JWT claims string is empty: {%s}\n", e.getMessage());
        }
        System.out.println("--------------------");
        System.out.println("--------------------");
        System.out.println("--------------------");
        System.out.println("--------------------");
        System.out.println("--------------------");
        return false;
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        //extractExpiration можна так
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    private SecretKey getSecretKey() {
        byte[] bytes = Decoders.BASE64.decode(secret_key);
        return Keys.hmacShaKeyFor(bytes);
    }



}
