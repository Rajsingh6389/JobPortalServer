package jobportalapplication.jobportalapplication.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.security.Key;

@Component
public class JwtUtil {

    private final Key key;
    private final long jwtExpirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long jwtExpirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
    }

    // --------------------------
    //   GENERATE TOKEN WITH ROLE
    // --------------------------
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // ⭐ STORE ROLE IN TOKEN
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // --------------------------
    //   EXTRACT EMAIL (subject)
    // --------------------------
    public String getEmail(String token) {
        return getAllClaims(token).getSubject();
    }

    // --------------------------
    //   EXTRACT ROLE
    // --------------------------
    public String getRole(String token) {
        return (String) getAllClaims(token).get("role");
    }

    // --------------------------
    //   PARSE TOKEN INTERNAL
    // --------------------------
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --------------------------
    //   VALIDATE TOKEN
    // --------------------------
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }
}
