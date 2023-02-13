```

String fakeToken = JWT.create()
                .withIssuer("https://sts.windows.net/{tenantid}/")
                .withSubject("user@example.com")
                .withArrayClaim("roles", new String[]{"admin"})
                .withClaim("authorities", "ADMIN")
                .withExpiresAt(new Date(System.currentTimeMillis() + 864000000))
                .sign(Algorithm.RSA256(fakePublicKey));



import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PortfolioCommentsResourceTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;

  private String token;

  @Before
  public void setup() throws NoSuchAlgorithmException, InvalidKeySpecException {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();

    // Generate a mock public key
    SecureRandom secureRandom = new SecureRandom();
    byte[] fakeKeyBytes = new byte[1024];
    secureRandom.nextBytes(fakeKeyBytes);

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(fakeKeyBytes);
    PublicKey fakePublicKey = keyFactory.generatePublic(keySpec);

    // Create a mock JWT token
    Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) fakePublicKey);
    token = JWT.create()
        .withIssuer("auth0")
        .withClaim("userId", "12345")
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 60)))
        .sign(algorithm);
  }

  @Test
  public void givenToken_whenPostPortfolioComment_
