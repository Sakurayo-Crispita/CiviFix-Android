package com.example.project_caxfix.security;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Cada cuenta usa una sal aleatoria. SQLite nunca recibe la contraseña en texto plano.
public final class PasswordHasher {
    public static final int ITERATIONS = 600000;
    private PasswordHasher() {}
    public static String newSalt() {
        byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt); return hex(salt);
    }
    public static String hash(String password, String salt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), unhex(salt), ITERATIONS, 256);
        try { return hex(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded()); }
        catch (Exception e) { throw new IllegalStateException("No se pudo proteger la contraseña", e); }
        finally { spec.clearPassword(); }
    }
    public static boolean verify(String password, String salt, String expected) {
        return MessageDigest.isEqual(unhex(hash(password, salt)), unhex(expected));
    }
    private static String hex(byte[] bytes) {
        StringBuilder out = new StringBuilder();
        for (byte b : bytes) out.append(String.format(java.util.Locale.ROOT, "%02x", b & 255));
        return out.toString();
    }
    private static byte[] unhex(String text) {
        byte[] out = new byte[text.length() / 2];
        for (int i = 0; i < out.length; i++) out[i] = (byte) Integer.parseInt(text.substring(i * 2, i * 2 + 2), 16);
        return out;
    }
}
