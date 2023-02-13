```
SecureRandom secureRandom = new SecureRandom();
byte[] fakeKeyBytes = new byte[512 / 8];
secureRandom.nextBytes(fakeKeyBytes);

KeyFactory keyFactory = KeyFactory.getInstance("RSA");
X509EncodedKeySpec keySpec = new X509EncodedKeySpec(fakeKeyBytes);
PublicKey fakePublicKey = keyFactory.generatePublic(keySpec);
