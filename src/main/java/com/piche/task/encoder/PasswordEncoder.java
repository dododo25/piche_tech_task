package com.piche.task.encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {

    public static final String SALT = "-salt";

    private final MessageDigest digest;

    public PasswordEncoder() throws NoSuchAlgorithmException {
        digest = MessageDigest.getInstance("SHA-256");
    }

    public String encode(String text) {
        return bytesToHex(digest.digest((text + SALT).getBytes()));
    }

    private static String bytesToHex(byte[] arr) {
        StringBuilder hexString = new StringBuilder(2 * arr.length);

        for (byte b : arr) {
            String hex = Integer.toHexString(0xff & b);

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }
}
