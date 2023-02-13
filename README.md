```
SecureRandom secureRandom = new SecureRandom();
byte[] fakeKeyBytes = new byte[1024];
secureRandom.nextBytes(fakeKeyBytes);

KeyFactory keyFactory = KeyFactory.getInstance("RSA");
X509EncodedKeySpec keySpec = new X509EncodedKeySpec(fakeKeyBytes);
PublicKey fakePublicKey = keyFactory.generatePublic(keySpec);
