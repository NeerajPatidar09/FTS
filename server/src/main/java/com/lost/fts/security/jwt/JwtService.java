// package com.lost.fts.security.jwt;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.io.Decoders;
// import io.jsonwebtoken.security.Keys;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Service;
// import javax.crypto.SecretKey;
// import java.util.Date;
// import java.util.function.Function;

// @Service
// public class JwtService {

//     @Value("${app.jwt.secret}")
//     private String secret;

//     @Value("${app.jwt.access-token-expiration}")
//     private long expiration;

//     private SecretKey getSigningKey() {
//         byte[] keyBytes = Decoders.BASE64.decode(secret);
//         return Keys.hmacShaKeyFor(keyBytes);
//     }

//     public String generateToken(UserDetails userDetails,String sessionReference) {
//         Date issuedAt = new Date();
//         Date expirationDate = new Date(issuedAt.getTime() + expiration);
//         return Jwts.builder().subject(userDetails.getUsername()).claim("sid", sessionReference).issuedAt(issuedAt).expiration(expirationDate).signWith(getSigningKey()).compact();
//     }

//     public String extractUsername(String token) {
//         return extractClaim(token, Claims::getSubject);
//     }

//     public Claims extractAllClaims(String token){
//         return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
//     }

//     private <T> T extractClaim(String token, Function<Claims, T> resolver) {
//         Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
//         return resolver.apply(claims);
//     }

//     public boolean isTokenValid(String token, UserDetails userDetails) {
//         try {
//             String username = extractUsername(token);
//             return username.equals(userDetails.getUsername())
//                     && userDetails.isEnabled()
//                     && userDetails.isAccountNonLocked();

//         } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
//             return false;
//         }
//     }

//     public long getExpiration() {
//         return expiration;
//     }
// }




package com.lost.fts.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-token-expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(
            UserDetails userDetails,
            String sessionReference) {

        if (sessionReference == null || sessionReference.isBlank()) {
            throw new IllegalArgumentException(
                    "Session reference cannot be empty"
            );
        }

        Date issuedAt = new Date();
        Date expirationDate =
                new Date(issuedAt.getTime() + expiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("sid", sessionReference)
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractSessionReference(String token) {
        return extractClaim(
                token,
                claims -> claims.get("sid", String.class)
        );
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> resolver) {

        return resolver.apply(extractAllClaims(token));
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        try {
            String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && userDetails.isEnabled()
                    && userDetails.isAccountNonLocked()
                    && extractSessionReference(token) != null;

        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public long getExpiration() {
        return expiration;
    }
}
