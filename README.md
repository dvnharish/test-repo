```
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Test
public void testPostPortfolioComment() throws Exception {
    String accessToken = getAccessToken();

    String requestBody = "{\"comment\":\"Test Comment\"}";
    mockMvc.perform(post("/portfolio/comments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .with(user("admin").roles("ADMIN"))
            .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isCreated());
}

private String getAccessToken() throws Exception {
    // Use the JWTAuthFilter mock to return a valid access token for the test user
    String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    when(jwtAuthFilter.getAccessToken(any(HttpServletRequest.class))).thenReturn(accessToken);
    return accessToken;
}
