/*
package com.test.app.filter;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTInterceptor implements HandlerInterceptor {

    public static final String INVALID_TOKEN = "Invalid token.";
    public static final String BEARER_ = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private final List<String> excludedEndpoints = List.of("/users/token");
    private final Map<String, List<Long>> requestMap = new HashMap<>();
    private final int maxRequests = 5;
    private final long timeInterval = 10000;
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (excludedEndpoints.contains(request.getRequestURI())) {
            return true;
        }
        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (header == null || !header.startsWith(BEARER_)) {
            throw new SignatureException(INVALID_TOKEN);
        }
        String token = header.substring(BEARER_.length());
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token.replace(BEARER_, ""));
            request.setAttribute("username", claims.getBody()
                    .getSubject());
            return checkForRateLimit(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            throw new SignatureException(INVALID_TOKEN);
        }
    }

    public boolean checkForRateLimit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String remoteAddr = request.getRemoteAddr();
        List<Long> requestTimestamps = requestMap.getOrDefault(remoteAddr, new ArrayList<>());
        long currentTime = System.currentTimeMillis();
        requestTimestamps.add(currentTime);
        requestMap.put(remoteAddr, requestTimestamps);
        requestTimestamps.removeIf(time -> (currentTime - time) > timeInterval);
        if (requestTimestamps.size() > maxRequests) {
            response.sendError(429, "Too many requests. Please try again later.");
            return false;
        }
        return true;
    }

}*/
