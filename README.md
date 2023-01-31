@SecurityScheme(
  name = "OAuth2",
  type = SecurityScheme.Type.OAUTH2,
  flows = @OAuthFlows(
    implicit = @OAuthFlow(
      authorizationUrl = "https://example.com/oauth/authorize",
      scopes = {
        "read:user",
        "write:user"
      }
    )
  )
)
