package com.shahid.nid.Utils;

import android.content.SharedPreferences;
import android.util.Base64;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Salted PIN verifier with one-time migration of the old plaintext preference. */
public final class PinStore {
    private PinStore() {}
    private static byte[] derive(String pin, byte[] salt) throws java.security.GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(pin.toCharArray(), salt, 120000, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded(); }
        finally { spec.clearPassword(); }
    }
    public static void save(SharedPreferences prefs, String pin) {
        try {
            byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
            String hash = Base64.encodeToString(derive(pin, salt), Base64.NO_WRAP);
            if (!prefs.edit().putString("pin_hash", hash).putString("pin_salt", Base64.encodeToString(salt, Base64.NO_WRAP))
                    .remove("password").remove("pin_failures").remove("pin_retry_after").commit())
                throw new IllegalStateException("Could not save PIN");
        } catch (java.security.GeneralSecurityException e) { throw new IllegalStateException("PIN verification unavailable", e); }
    }
    public static boolean verify(SharedPreferences prefs, String pin) {
        long now = System.currentTimeMillis();
        if (now < prefs.getLong("pin_retry_after", 0)) return false;
        boolean matches = false;
        try {
            String hash = prefs.getString("pin_hash", null);
            if (hash != null) {
                String salt = prefs.getString("pin_salt", "");
                matches = MessageDigest.isEqual(Base64.decode(hash, Base64.NO_WRAP), derive(pin, Base64.decode(salt, Base64.NO_WRAP)));
            } else {
                String old = prefs.getString("password", null);
                matches = old != null && !old.isEmpty() && old.equals(pin);
                if (matches) save(prefs, pin);
            }
        } catch (java.security.GeneralSecurityException | IllegalArgumentException e) { return false; }
        if (matches) prefs.edit().remove("pin_failures").remove("pin_retry_after").apply();
        else {
            int failures = prefs.getInt("pin_failures", 0) + 1;
            prefs.edit().putInt("pin_failures", failures >= 5 ? 0 : failures)
                    .putLong("pin_retry_after", failures >= 5 ? now + 30000 : 0).apply();
        }
        return matches;
    }
}
