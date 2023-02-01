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
      .oauth2ResourceServer().jwt();
  }
}
