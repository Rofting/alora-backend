package com.alora.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6";

    // Método para generar el Token (La tarjeta)
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // ¿Para quién es la tarjeta? (Email)
                .setIssuedAt(new Date(System.currentTimeMillis())) // ¿Cuándo se hizo?
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // Caduca en 24 horas
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firmar digitalmente
                .compact();
    }

    // Método auxiliar para decodificar la clave secreta
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Convierte el texto en bytes reales
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // Método para sacar el usuario (subject) del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Método para validar el token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // El token es válido si el usuario coincide Y no ha expirado
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Método auxiliar para ver si ha expirado
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Método auxiliar para sacar la fecha de expiración
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}