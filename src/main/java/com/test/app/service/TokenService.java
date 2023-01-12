package com.test.app.service;

import io.jsonwebtoken.*;
import io.netty.handler.codec.base64.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(String subject) {

        return Jwts.builder()
                .setSubject(subject)
                .setIssuer("Rogers")
                .setAudience("USERS")
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

}
