```

String fakeToken = JWT.create()
                .withIssuer("https://sts.windows.net/{tenantid}/")
                .withSubject("user@example.com")
                .withArrayClaim("roles", new String[]{"admin"})
                .withClaim("authorities", "ADMIN")
                .withExpiresAt(new Date(System.currentTimeMillis() + 864000000))
                .sign(Algorithm.RSA256(fakePublicKey));
