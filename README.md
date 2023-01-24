```
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class JwtAuthFilter {

    private final String azureAdTenant = "your tenant"; 
    private final String jwksUrl = "https://login.microsoftonline.com/"+azureAdTenant+"/discovery/v2.0/keys";
    private final RestTemplate restTemplate = new RestTemplate();
    private final String RSA = "RSA";
    private final String AES = "AES";
    private final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    private final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    private final String UTF_8 = "UTF-8";
    private final String HMACSHA256 = "HmacSHA256";

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.replace("Bearer ", "");
        try {
            Claims claims = Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            List<String> roles = (List<String>) claims.get("roles");
            List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
// Set authentication in security context
Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
SecurityContextHolder.getContext().setAuthentication(auth);
} catch (SignatureException e) {
SecurityContextHolder.clearContext();
}
filterChain.doFilter(request, response);
}

private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
    // Fetch the JWKS from Azure AD
    String jsonWebKeySet = restTemplate.getForObject(jwksUrl, String.class);
    // Parse the JWKS and extract the public key
    JsonNode jsonNode = new ObjectMapper().readTree(jsonWebKeySet);
    JsonNode keyNode = jsonNode.get("keys").get(0);
    String kty = keyNode.get("kty").asText();
    String n = keyNode.get("n").asText();
    String e = keyNode.get("e").asText();
    String kid = keyNode.get("kid").asText();
    String alg = keyNode.get("alg").asText();
    byte[] decodedN = Base64.getUrlDecoder().decode(n);
    byte[] decodedE = Base64.getUrlDecoder().decode(e);
    KeyFactory kf = KeyFactory.getInstance(RSA);
    RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(decodedN), new BigInteger(decodedE));
RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(publicKeySpec);
return publicKey;
}
}


``
