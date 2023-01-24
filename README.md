First, you will need to fetch the public keys from the AD Discovery keys URL. You can do this by making an HTTP GET request to the URL and parsing the JSON response to extract the public keys.
Copy code
    public static Map<String, PublicKey> fetchPublicKeys() throws Exception {
        String keysUrl = "https://your-domain.com/discovert/v2.0/keys";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(keysUrl)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonString = response.body();
        //parse json and extract public keys
        //return a map of keys with kid as key and public key as value
        return keys;
    }
Next, you will need to create a JwtVerifier object for each public key. The JwtVerifier class is provided by Spring Security and can be used to verify the signature of a JWT using a specific public key.
Copy code
    public static JwtVerifier createJwtVerifier(PublicKey key) {
        Algorithm algorithm = Algorithm.RSA256(key);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        return new JwtVerifier(verifier);
    }
In the Spring Security configuration, you can use the JwtVerifier objects to configure a JwtVerifierAuthenticationProvider. This provider will be used to authenticate requests that contain JWTs in the Authorization header.
Copy code
    public static class JwtVerifierAuthenticationProvider implements AuthenticationProvider {
        private final Map<String, JwtVerifier> jwtVerifiers;
        public JwtVerifierAuthenticationProvider(Map<String, JwtVerifier> jwtVerifiers) {
            this.jwtVerifiers = jwtVerifiers;
        }
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            String kid = jwtAuth.getKid();
            if(!jwtVerifiers.containsKey(kid))
                throw new BadCredentialsException("Invalid JWT, Invalid Key Id");
            JwtVerifier jwtVerifier = jwtVerifiers.get(kid);
            try {
                DecodedJWT jwt = jwtVerifier.verify(jwtAuth.getToken());
                // Extract claims from JWT
                // create a new Authentication
                return new JwtAuthenticationToken(jwt, jwtAuth.getAuthorities());
            } catch (JWTVerificationException ex) {
                throw new BadCredentialsException("Invalid JWT", ex);
            }
        }
        @Override
        public boolean supports(Class<?> authentication) {
            return JwtAuthenticationToken.class.isAssignableFrom(authentication);
        }
    }
Finally, you can configure Spring Security to use the



Venkat Naga Harish Duddupudi
add this in the filter for onceper request
You can configure the JwtAuthenticationFilter to be executed once per request by adding the @Component annotation to the class and overriding the doFilter method.

Copy code
    @Component
    public class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtVerifierAuthenticationProvider jwtVerifierAuthenticationProvider;
        public JwtAuthenticationFilter(JwtVerifierAuthenticationProvider jwtVerifierAuthenticationProvider) {
            this.jwtVerifierAuthenticationProvider = jwtVerifierAuthenticationProvider;
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
                // create a new JwtAuthenticationToken with the kid and token
                JwtAuthenticationToken auth = new JwtAuthenticationToken(token, kid);
                Authentication authResult = jwtVerifierAuthenticationProvider.authenticate(auth);
                SecurityContextHolder.getContext().setAuthentication(authResult);
            } catch (AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                // handle the exception
            }
            filterChain.doFilter(request, response);
        }
    }
You have to make sure to configure the JwtAuthenticationFilter in the configure(HttpSecurity http) method of the WebSecurityConfigurerAdapter class so it will be applied to the requests.

Copy code
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
It's also important to keep in mind that JWT validation should be done on the server side, to ensure the authenticity and integrity of the token, and that the public keys should be rotated regularly.
