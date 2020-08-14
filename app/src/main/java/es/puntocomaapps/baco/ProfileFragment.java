package es.puntocomaapps.baco;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private DatabaseReference mDatabase;
    private ListView mListView;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mListView = view.findViewById(R.id.lvMisFavoritos);

        assert user != null;
        user.reload();
        if (!user.isEmailVerified()) {
            TextView tvUsuario = view.findViewById(R.id.tvUsuario);
            TextView tvUsuarioFirebase = view.findViewById(R.id.tvUsuarioFirebase);
            ImageView ivDivider2 = view.findViewById(R.id.ivDivider2);
            TextView tvEmail = view.findViewById(R.id.tvEmail);
            TextView tvEmailFirebase = view.findViewById(R.id.tvEmailFirebase);
            ImageView ivDivider3 = view.findViewById(R.id.ivDivider3);
            TextView tvMisFavoritos = view.findViewById(R.id.tvMisFavoritos);
            Button btnSignOut = view.findViewById(R.id.btnSignOut);

            tvUsuario.setVisibility(View.GONE);
            tvUsuarioFirebase.setVisibility(View.GONE);
            ivDivider2.setVisibility(View.GONE);
            tvEmail.setVisibility(View.GONE);
            tvEmailFirebase.setVisibility(View.GONE);
            ivDivider3.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            tvMisFavoritos.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.GONE);

            TextView tvCheckEmail = view.findViewById(R.id.tvCheckEmail);
            tvCheckEmail.setVisibility(View.VISIBLE);
            tvCheckEmail.setOnClickListener(view12 -> {
                user.sendEmailVerification();
                FirebaseAuth.getInstance().signOut();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.profileContainer, new LoginFragment())
                        .commit();
                Toast.makeText(getContext(), R.string.dialog_check_email, Toast.LENGTH_SHORT).show();
            });
        } else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("usuario");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TextView tvUsuarioFirebase = view.findViewById(R.id.tvUsuarioFirebase);

                    tvUsuarioFirebase.setText(Objects.requireNonNull(dataSnapshot.child(user.getUid()).child("nombre").getValue()).toString());
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (Objects.equals(ds.getKey(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {

                            DatabaseReference favoritoRef = mDatabase.child(ds.getKey()).child("favoritos");
                            favoritoRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        final ArrayList<Evento> array = new ArrayList<>();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                            DatabaseReference eventoRef = FirebaseDatabase.getInstance().getReference().child("evento").child(Objects.requireNonNull(snapshot.getKey()));
                                            eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        showData(getContext(), dataSnapshot, array);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    } else {
                                        TextView tvSinFavoritos = view.findViewById(R.id.tvSinFavoritos);
                                        tvSinFavoritos.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            String emailUsuario = user.getEmail();

            TextView tvEmailFirebase = view.findViewById(R.id.tvEmailFirebase);
            tvEmailFirebase.setText(emailUsuario);

            Button btnSignOut = view.findViewById(R.id.btnSignOut);
            btnSignOut.setOnClickListener(view1 -> {
                FirebaseAuth.getInstance().signOut();
                Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.profileContainer, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
                Toast.makeText(getContext(), R.string.dialog_logout_success, Toast.LENGTH_LONG).show();
            });
        }

        return view;
    }

    private void showData(Context context, DataSnapshot dataSnapshot, ArrayList<Evento> array) {
        final Evento e = new Evento();
        e.setId(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getId());
        e.setTitulo(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getTitulo());
        e.setFecha(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getFecha());
        e.setFoto(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getFoto());
        e.setDescripcion(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getDescripcion());
        e.setHora(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getHora());
        e.setMunicipio(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getMunicipio());
        e.setPrecio(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getPrecio());
        e.setUrl(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getUrl());
        e.setUbicacion(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getUbicacion());
        e.setKeywords(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getKeywords());
        e.setOrdenMunYFecha(Objects.requireNonNull(dataSnapshot.getValue(Evento.class)).getOrdenMunYFecha());

        array.add(e);
        FavsAdapter adapter = new FavsAdapter(context, array);
        mListView.setAdapter(adapter);
    }
}
