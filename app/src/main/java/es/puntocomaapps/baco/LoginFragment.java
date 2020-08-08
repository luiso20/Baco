package es.puntocomaapps.baco;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private EditText etEmailLogin, etPasswordLogin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmailLogin = view.findViewById(R.id.etEmailLogin);
        etPasswordLogin = view.findViewById(R.id.etPasswordLogin);
        Button btnEntrar = view.findViewById(R.id.btnEntrar);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuario");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null){
                    try {
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.profileContainer, new ProfileFragment())
                                .addToBackStack(null)
                                .commit();
                    } catch (NullPointerException e) {
                        e.getMessage();
                    }
                }
            }
        };

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailLogin.getText().toString();
                String pwd = etPasswordLogin.getText().toString();
                if (!email.equals("") && !pwd.equals("")) {
                    mAuth.signInWithEmailAndPassword(etEmailLogin.getText().toString(), etPasswordLogin.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), R.string.dialog_session_start, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), R.string.dialog_user_not_found, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else if(email.equals("")) {
                    etEmailLogin.setError(Objects.requireNonNull(getContext()).getText(R.string.dialog_email_not_typed));
                    etEmailLogin.requestFocus();
                } else {
                    etPasswordLogin.setError(Objects.requireNonNull(getContext()).getText(R.string.dialog_set_password));
                    etPasswordLogin.requestFocus();
                }
            }
        });

        Button btnHuella = view.findViewById(R.id.btnHuella);
        btnHuella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.profileContainer, new FingerprintFragment())
                            .addToBackStack(null)
                            .commit();
                } catch (NullPointerException e) {
                    e.getMessage();
                }
            }
        });


        TextView tvCrear = view.findViewById(R.id.tvCrear);
        tvCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.profileContainer, new RegistroFragment())
                            .addToBackStack(null)
                            .commit();
                } catch (NullPointerException e) {
                    e.getMessage();
                }
            }
        });

        TextView tvReset = view.findViewById(R.id.tvReset);
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.profileContainer, new ResetPasswordFragment())
                            .addToBackStack(null)
                            .commit();
                } catch (NullPointerException e) {
                    e.getMessage();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
