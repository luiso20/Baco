package es.puntocomaapps.baco;

import androidx.biometric.BiometricPrompt;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.concurrent.Executor;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class FingerprintFragment extends Fragment {

    private Handler handler = new Handler();

    private Executor executor = new Executor() {
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    };

    private String TAG = "FingerprintFragment";

    public FingerprintFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fingerprint, container, false);

        ImageButton biometricLoginButton = view.findViewById(R.id.ibBiometricLogin);
        biometricLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBiometricPrompt();
            }

            private void showBiometricPrompt() {
                BiometricPrompt.PromptInfo promptInfo =
                        new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Huella dactilar")
                                .setSubtitle("Inicie sesión usando sus credenciales biométricas")
                                .setNegativeButtonText("Cancelar")
                                .build();

                BiometricPrompt biometricPrompt = new BiometricPrompt(FingerprintFragment.this,
                        executor, new BiometricPrompt.AuthenticationCallback() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                                .show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        BiometricPrompt.CryptoObject authenticatedCryptoObject =
                                result.getCryptoObject();
                        // User has verified the signature, cipher, or message
                        // authentication code (MAC) associated with the crypto object,
                        // so you can use it in your app's crypto-driven workflows.
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

                // Displays the "log in" prompt.
                biometricPrompt.authenticate(promptInfo);

            }
        });

        return view;
    }
}
