```
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class MyControllerTest {
  
  @Mock
  private SecurityContext securityContext;

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Test
  public void testMethod() throws Exception {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("user_read"));
    
    UserDetails userDetails = new User("TestUser", "TestPassword", authorities);
    when(securityContext.getAuthentication().getPrincipal()).thenReturn(userDetails);
    SecurityContextHolder.setContext(securityContext);

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
        .apply(springSecurity())
        .build();

    this.mockMvc.perform(get("/my-url"))
        .andExpect(status().isOk())
        .andExpect(result -> {
          // Assert the response body
        });
  }
}



import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class MyControllerTest {
  
  @Mock
  private SecurityContext securityContext;

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Test
  public void testMethod() throws Exception {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("user_read"));
    
    when(securityContext.getAuthentication().getAuthorities()).thenReturn(authorities);
    SecurityContextHolder.setContext(securityContext);

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
        .apply(springSecurity())
        .build();

    this.mockMvc.perform(get("/my-url"))
        .andExpect(status().isOk())
        .andExpect(result -> {
          // Assert the response body
        });
  }
}
