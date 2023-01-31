```
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestClass {

  private FilterChain filterChain;

  @Before
  public void setup() {
    if (this.getClass().isAnnotationPresent(WithMockJwtAuth.class)) {
      Mockito.reset(filterChain);
      filterChain = new DefaultFilterChain();
      // add other filters that are required
    }
  }

  @Test
  @WithMockJwtAuth
  public void testCaseWithoutJwtAuthFilter() {
    // test code here
  }

  @Test
  public void testCaseWithJwtAuthFilter() {
    // test code here
  }
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@interface WithMockJwtAuth {
}





@RunWith(MockitoJUnitRunner.class)
public class YourControllerTest {
  
  @Mock
  private JwtAuthFilter jwtAuthFilter;
  
  @InjectMocks
  private YourController yourController;
  
  @Test
  public void testEndpoint() {
    // Arrange
    doNothing().when(jwtAuthFilter).doFilter(any(ServletRequest.class), any(ServletResponse.class), any(FilterChain.class));
    
    // Act
    ResponseEntity<YourResponse> response = yourController.yourEndpoint(yourRequest);
    
    // Assert
    // Add your assertions here
  }
}

