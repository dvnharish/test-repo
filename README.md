private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
    // Fetch the JWKS from Azure AD
    String jsonWebKeySet = restTemplate.getForObject(jwksUrl, String.class);
    // Parse the JWKS and extract the public key
    JsonNode jsonNode = new ObjectMapper().readTree(jsonWebKeySet);
    JsonNode keyNode = jsonNode.get("keys").get(0);
    String publicKey = keyNode.get("x5c").get(0).textValue();
    // Use the public key to construct a PublicKey object
    byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(keySpec);
}
