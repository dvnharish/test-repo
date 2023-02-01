```
@OpenAPIDefinition(
  security = {
    @SecurityRequirement(name = "bearerAuth")
  }
)
@SecurityScheme(
  name = "bearerAuth",
  type = SecuritySchemeType.HTTP,
  scheme = "bearer",
  bearerFormat = "JWT"
)
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtFilter jwtFilter;

  public SecurityConfig(JwtFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .cors().and()
      .csrf().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .authorizeRequests()
      .antMatchers("/v3/api-docs/**").permitAll()
      .antMatchers("/swagger-ui.html").permitAll()
      .antMatchers("/webjars/**").permitAll()
      .antMatchers("/swagger-resources/**").permitAll()
      .anyRequest().authenticated()
      .and()
      .oauth2Login().authorizationEndpoint().baseUri("/oauth2/authorize")
      .and()
      .oauth2Client().clientId("client_id")
      .clientSecret("client_secret")
      .accessTokenUri("https://login.microsoftonline.com/{tenant_id}/oauth2/v2.0/token")
      .scope("openid profile email offline_access user.read")
      .and()
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
