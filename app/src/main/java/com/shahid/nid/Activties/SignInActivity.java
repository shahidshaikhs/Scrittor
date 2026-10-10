package com.shahid.nid.Activties;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricManager;
import androidx.core.content.ContextCompat;
import com.shahid.nid.NID;
import com.shahid.nid.R;

public class SignInActivity extends BaseActivity {
    private boolean completed;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_sign_in);
        boolean fingerprint = "enable".equals(getSharedPreferences(getString(R.string.MY_PREFS_FINGERPRINT), MODE_PRIVATE)
                .getString("fingerprint_check", null));
        findViewById(R.id.fingerprint_authentication_box).setVisibility(fingerprint ? View.VISIBLE : View.GONE);
        findViewById(R.id.pin_authentication_box).setVisibility(fingerprint ? View.GONE : View.VISIBLE);
        if (fingerprint) {
            BiometricPrompt prompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this),
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) { unlock(); }
                        @Override public void onAuthenticationError(int code, @NonNull CharSequence message) {
                            if (!completed) { setResult(RESULT_CANCELED); finish(); }
                        }
                    });
            prompt.authenticate(new BiometricPrompt.PromptInfo.Builder().setTitle("Unlock Scrittor")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    .build());
        } else {
            EditText input = findViewById(R.id.app_password_edit_text);
            input.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 4) return;
                    android.content.SharedPreferences prefs = getSharedPreferences(getString(R.string.MY_PREFS_PASSWORD), MODE_PRIVATE);
                    if (com.shahid.nid.Utils.PinStore.verify(prefs, s.toString())) unlock();
                    else input.setError(prefs.getLong("pin_retry_after", 0) > System.currentTimeMillis()
                            ? "Too many attempts. Try again in 30 seconds." : "Incorrect PIN");
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }
    private void unlock() {
        if (completed) return;
        completed = true;
        ((NID) getApplication()).unlock();
        setResult(RESULT_OK);
        finish();
    }
}
