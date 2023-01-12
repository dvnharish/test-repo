package com.test.app.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JWTFilter extends OncePerRequestFilter {


    public static final String INVALID_TOKEN = "Invalid token.";
    public static final String BEARER_ = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private final Logger logger = LoggerFactory.getLogger(JWTFilter.class);
    private final List<String> excludedEndpoints = List.of("/users/token");
    private final Map<String, List<Long>> requestMap = new HashMap<>();
    private final int maxRequests = 5;
    private final long timeInterval = 10000;
    @Value("${jwt.secret}")
    private String secretKey;


    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (excludedEndpoints.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
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
            if (!checkForRateLimit(request, response)) {
                return;
            }
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            e.printStackTrace();
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


}

