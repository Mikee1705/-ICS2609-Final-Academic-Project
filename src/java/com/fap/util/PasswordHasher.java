package com.fap.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * PasswordHasher
 *
 * Salted SHA-256 password hashing utility.
 * Format stored in DB: "saltHex:hashHex"
 *
 * Usage:
 *   String stored = PasswordHasher.hash("plain-text");
 *   boolean ok    = PasswordHasher.verify("plain-text", stored);
 */
public class PasswordHasher {

    private static final int SALT_BYTES = 16;
    private static final SecureRandom RNG = new SecureRandom();

    /** Hash a plain-text password with a random salt. */
    public static String hash(String plain) {
        byte[] salt = new byte[SALT_BYTES];
        RNG.nextBytes(salt);
        byte[] digest = sha256(salt, plain);
        return toHex(salt) + ":" + toHex(digest);
    }

    /** Verify a plain-text password against a stored salt:hash. */
    public static boolean verify(String plain, String stored) {
        if (stored == null || !stored.contains(":")) return false;
        String[] parts = stored.split(":", 2);
        byte[] salt   = fromHex(parts[0]);
        byte[] hash   = fromHex(parts[1]);
        byte[] check  = sha256(salt, plain);

        if (hash.length != check.length) return false;
        int diff = 0;
        for (int i = 0; i < hash.length; i++) diff |= hash[i] ^ check[i];
        return diff == 0;
    }

    // ----------------------------------------------------------------
    // INTERNAL
    // ----------------------------------------------------------------

    private static byte[] sha256(byte[] salt, String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return md.digest(plain.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Hash failure", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return out;
    }
}
