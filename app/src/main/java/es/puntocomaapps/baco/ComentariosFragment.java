package es.puntocomaapps.baco;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ComentariosFragment extends Fragment {

    private RatingBar rbComentarios;
    private EditText etComentarios;
    private FirebaseUser user;

    public ComentariosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comentarios, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rbComentarios = view.findViewById(R.id.rbComentarios);

        etComentarios = view.findViewById(R.id.etComentarios);
        Button btnEnviarComentarios = view.findViewById(R.id.btnEnviarComentarios);

        final Date fecha = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY'-'MM'-'dd");
        final String fechaHoy = formatter.format(fecha);

        final String id_evento = Objects.requireNonNull(Objects.requireNonNull(getActivity()). getIntent().getExtras()).getString("evento");

        final String[] usuario = {""};

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("usuario");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuario[0] = Objects.requireNonNull(dataSnapshot.child(user.getUid()).child("nombre").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnEnviarComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etComentarios.getText().toString().isEmpty()) {
                    insertarComentario(etComentarios.getText().toString(), fechaHoy, usuario[0], id_evento, String.valueOf(rbComentarios.getRating()));
                    Toast.makeText(view.getContext(), R.string.thanks_comment, Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(getActivity()).onBackPressed();
                } else {
                    Toast.makeText(getContext(), R.string.event_rate, Toast.LENGTH_LONG).show();
                }

            }
        });
        return view;
    }

    private void insertarComentario(String comentario, String fecha, String usuario, String id_evento, String valoracion) {
        Comentario comentarios = new Comentario(comentario, fecha, usuario, id_evento, valoracion);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference reference = mDatabase.child("evento_comentario").push();
        reference.setValue(comentarios);
    }
}
