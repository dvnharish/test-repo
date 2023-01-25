```
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;


public class JwtVerificationFilter extends OncePerRequestFilter {

    private final HttpClient httpClient;
    private final Map<String, Set<String>> roleMapping;

    public JwtVerificationFilter(HttpClient httpClient, Map<String, Set<String>> roleMapping) {
        this.httpClient = httpClient;
        this.roleMapping = roleMapping;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(7);
        try {
            // extract the kid from the token
            String kid = JWT.decode(token).getKeyId();
            PublicKey publicKey = getPublicKeyFromAD(kid);
            // verify the token with the public key

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, RSASSAProvider.JavaCrypto);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            // Extract claims from JWT
            String role = jwt.getClaim("role").asString();
            if(!roleMapping.containsKey(role)){
                throw new AccessDeniedException("Access Denied for Role: " + role);
            }
            // create a new Authentication
            List<GrantedAuthority> authorities = new ArrayList<>();
            for(String authority : roleMapping.get(role)){
                authorities.add(new SimpleGrantedAuthority(authority));
            }
            Authentication auth = new JwtAuthenticationToken(jwt, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JWTVerificationException | ExecutionException | InterruptedException ex) {
            SecurityContextHolder.clearContext();
            throw new BadCredentialsException("Invalid JWT", ex);
        }
        filterChain.doFilter(request, response);
    }

    private PublicKey getPublicKeyFromAD(String kid) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://your-domain.com/discovert/v2.0/keys")).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonString = response.body();
        // parse the json and extract the public key corresponding to the kid
        JsonNode json = new ObjectMapper().readTree(jsonString);
        JsonNode keys = json.get("keys");
        for(JsonNode key : keys) {
            if(kid.equals(key.get("kid").asText())) {
                String x5c = key.get("x5c").asText();
                return readPublicKey(x5c);
            }
        }
        throw new IllegalArgumentException("Invalid Key Id: " + kid);
    }
    
    
    private PublicKey readPublicKey(String x5c) {
    try {
        byte[] data = Base64.getDecoder().decode(x5c);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
        throw new IllegalArgumentException("Invalid Key", ex);
    }
}


}
``
