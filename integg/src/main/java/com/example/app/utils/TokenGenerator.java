package com.example.app.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    
    public static String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return encoder.encodeToString(randomBytes);
    }
    
    public static String generateCode(int length) {
        String numbers = "0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(numbers.charAt(secureRandom.nextInt(numbers.length())));
        }
        return code.toString();
    }
}