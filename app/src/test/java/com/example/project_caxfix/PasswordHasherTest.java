package com.example.project_caxfix;
import com.example.project_caxfix.security.PasswordHasher;
import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordHasherTest {
    @Test public void passwordVerificationRejectsWrongPasswordAndDifferentSalt() {
        String salt = PasswordHasher.newSalt();
        String hash = PasswordHasher.hash("CiviFix123!", salt);
        assertTrue(PasswordHasher.verify("CiviFix123!", salt, hash));
        assertFalse(PasswordHasher.verify("Incorrecta123", salt, hash));
        assertFalse(PasswordHasher.verify("CiviFix123!", PasswordHasher.newSalt(), hash));
        assertNotEquals("CiviFix123!", hash);
    }
    @Test public void generatedSaltsHaveExpectedLengthAndAreUnique() {
        String first = PasswordHasher.newSalt(), second = PasswordHasher.newSalt();
        assertEquals(32, first.length()); assertNotEquals(first, second);
    }
}
