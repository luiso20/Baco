package es.puntocomaapps.baco;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.SyncStateContract;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroFragment extends Fragment {

    static final String URL_TERMS = "http://www.puntocomaapps.es/termsandprivacy.html";

    public RegistroFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final EditText etNombreRegistro = view.findViewById(R.id.etNombreRegistro);
        final EditText etApellidosRegistro = view.findViewById(R.id.etApellidosRegistro);
        final EditText etEmailRegistro = view.findViewById(R.id.etEmailRegistro);
        final EditText etPasswordRegistro = view.findViewById(R.id.etPasswordRegistro);
        final EditText etPassword2Registro = view.findViewById(R.id.etPassword2Registro);
        final CheckBox cbTerms = view.findViewById(R.id.cbTerms);
        final TextView tvTerms = view.findViewById(R.id.tvTerms);

        Pattern pattern = Pattern.compile(getString(R.string.terms_and_privacy));
        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };

        Linkify.addLinks(tvTerms, pattern, URL_TERMS, null, transformFilter);

        Button btnCrearUsuario = view.findViewById(R.id.btnCrearUsuario);

        cbTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbTerms.isChecked()) {
                    btnCrearUsuario.setEnabled(true);
                    btnCrearUsuario.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.boton_redondeado));
                } else {
                    btnCrearUsuario.setEnabled(false);
                    btnCrearUsuario.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.button_disabled));
                }
            }
        });

        btnCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etNombreRegistro.getText().toString().equals("") &&
                        !etApellidosRegistro.getText().toString().equals("") &&
                        !etEmailRegistro.getText().toString().equals("") &&
                        !etPasswordRegistro.getText().toString().equals("")) {

                    if(etPasswordRegistro.getText().toString().equals(etPassword2Registro.getText().toString())) {

                        if (etPasswordRegistro.length() >= 8) {

                                mAuth.createUserWithEmailAndPassword(etEmailRegistro.getText().toString(), etPasswordRegistro.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(!task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Algo ha fallado. Por favor, inténtelo de nuevo", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Usuario creado con éxito!", Toast.LENGTH_LONG).show();
                                                    mAuth.signInWithEmailAndPassword(etEmailRegistro.getText().toString(), etPasswordRegistro.getText().toString());

                                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                                    DatabaseReference reference = mDatabase.child("usuario");
                                                    HashMap<String, String> userValues = new HashMap<>();
                                                    userValues.put("id", mAuth.getUid());
                                                    userValues.put("nombre", etNombreRegistro.getText().toString());
                                                    userValues.put("apellidos", etApellidosRegistro.getText().toString());
                                                    userValues.put("email", etEmailRegistro.getText().toString());
                                                    reference.child(Objects.requireNonNull(mAuth.getUid())).setValue(userValues);

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
                                        });

                        } else {
                            Toast.makeText(getContext(), "Introduzca una contraseña de 8 o más caracteres", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Por favor, rellene todos los campos", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView tvAcceder = view.findViewById(R.id.tvAcceder);
        tvAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.profileContainer, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}
