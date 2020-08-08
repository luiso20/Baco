package es.puntocomaapps.baco;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordFragment extends Fragment {

    private EditText etEmailReset;
    private String email = "";

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_reset_password, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(getContext());

        etEmailReset = view.findViewById(R.id.etEmailReset);
        Button btnReset = view.findViewById(R.id.btnReset);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = etEmailReset.getText().toString();

                if(!email.isEmpty()) {
                    mDialog.setMessage(Objects.requireNonNull(getContext()).getText(R.string.dialog_wait_a_moment));
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    resetPassword();
                } else {
                    Toast.makeText(getContext(), R.string.dialog_wait_a_moment, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void resetPassword() {

        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {
                    Toast.makeText(getContext(), R.string.dialog_send_email_password, Toast.LENGTH_SHORT).show();
                    try {
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.profileContainer, new LoginFragment())
                                .addToBackStack(null)
                                .commit();
                    } catch (NullPointerException e) {
                        e.getMessage();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.dialog_not_send_password, Toast.LENGTH_SHORT).show();
                }

                mDialog.dismiss();
            }
        });
    }
}
