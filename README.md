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
