package com.hungnln.vleague.utils;

import com.hungnln.vleague.entity.CustomUserDetails;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider{
    // Đoạn JWT_SECRET này là bí mật, chỉ có phía server biết
    @Value("${springbootwebfluxjjwt.jjwt.secret}")
    private  String JWT_SECRET;

    //Thời gian có hiệu lực của chuỗi jwt
    @Value("${springbootwebfluxjjwt.jjwt.expiration}")
    private long JWT_EXPIRATION;
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).get("username").toString();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    public String generateToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        // Tạo chuỗi json web token từ id của user.
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("authorities","USER")
                .claim("username",userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }
    public String generateAdminToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        // Tạo chuỗi json web token từ id của user.
        return Jwts.builder()
                .setSubject(user.getUsername())
//                .claim("role",user.getAuthorities())
                .claim("authorities","ADMIN")
                .claim("username",user.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }
    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    public boolean validateToken(String authToken) {
        try {
            System.out.println(authToken);
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token",ex);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token",ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token",ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.",ex);
        } catch (SignatureException ex){
            log.error("Signature JWT does not match",ex);
        }
        return false;
    }

//    public static String buildJWT(Authentication authenticate, Account accountAuthenticated, SecretKey secretKey, JWTConfig jwtConfig) {
//        String token = Jwts.builder().setSubject(authenticate.getName())
//                .claim("authorities", authenticate.getAuthorities())
//                .claim("email", accountAuthenticated.getEmail())
//                .claim("accountId", accountAuthenticated.getId())
//                .setSubject(accountAuthenticated.getEmail())
//                .setIssuedAt(new Date())
//                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
//                .signWith(secretKey).compact();
//        return token;
//    }
}
