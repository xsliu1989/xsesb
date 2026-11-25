package com.xs.admin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    //日志log
    private final static Logger log = LoggerFactory.getLogger(JwtUtil.class);


    @Value("${esb.admin.jwt.secret}")
    private String secret;

    @Value("${esb.admin.jwt.expire-hours:12}")
    private long expireHours;

    public String generate(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        Date now = new Date();
        Date exp = new Date(now.getTime() + expireHours * 3600 * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
    }

    public String username(String token) {
        return parse(token).getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
