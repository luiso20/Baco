package es.puntocomaapps.baco;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FiltroFragment extends Fragment {

    private FirebaseRecyclerAdapter mAdapter;
    private String municipio;
    private ProgressBar pBarFiltro;

    public FiltroFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_filtro, container, false);

        pBarFiltro = view.findViewById(R.id.pBarFiltro);

        int mNoOfColumns = Utility.calculateNoOfColumns(view.getContext(), 140);
        final RecyclerView rvListadoFiltro = view.findViewById(R.id.rvListadoFiltro);
        rvListadoFiltro.setLayoutManager(new GridLayoutManager(view.getContext(), mNoOfColumns));

        if (getArguments() != null) {
            municipio = getArguments().getString("municipio");
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        reference.keepSynced(true);

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fechaActual = dateFormat.format(date);

        final Query query = reference.child("evento").orderByChild("ordenMunYFecha").startAt(municipio + "/" + fechaActual).endAt(municipio + "/" + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FirebaseRecyclerOptions<Evento> options =
                            new FirebaseRecyclerOptions.Builder<Evento>()
                                    .setQuery( query, Evento.class )
                                    .build();

                    mAdapter = new FirebaseRecyclerAdapter<Evento, EventoHolder>(options) {

                        @NonNull
                        @Override
                        public EventoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                            View view = LayoutInflater.from( viewGroup.getContext() )
                                    .inflate( R.layout.item_lista, viewGroup, false );
                            return new EventoHolder( view );
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull EventoHolder eventoHolder, int position, @NonNull final Evento evento) {
                            eventoHolder.setDetails(getContext(), evento.getFoto(), evento.getTitulo(),
                                    evento.getFecha(), evento.getMunicipio());

                            eventoHolder.setOnClickListener( new EventoHolder.ClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Intent intent = new Intent(getContext(), EventosActivity.class);
                                    intent.putExtra( "ficha", evento );
                                    startActivity( intent );

                                }
                            } );

                            pBarFiltro.setVisibility(View.INVISIBLE);
                        }
                    };

                    mAdapter.startListening();
                    rvListadoFiltro.setAdapter(mAdapter);
                } else {
                    pBarFiltro.setVisibility(View.INVISIBLE);
                    TextView tvSinEventos = view.findViewById(R.id.tvSinEventos);
                    TextView tvPruebaManana = view.findViewById(R.id.tvPruebaManana);
                    TextView tvEmailEventos = view.findViewById(R.id.tvEmailEventos);
                    tvSinEventos.setVisibility(View.VISIBLE);
                    tvPruebaManana.setVisibility(View.VISIBLE);
                    tvEmailEventos.setVisibility(View.VISIBLE);
                    AdView adViewSinEventos = view.findViewById(R.id.adViewSinEventos);
                    adViewSinEventos.setVisibility(View.VISIBLE);
                    tvEmailEventos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enviarEmail();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void enviarEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:?subject=" + "Sugerencia de evento" + "&body=" + "Sé de un evento que se va a realizar en mi municipio, los datos son: " +
                "&to=" + "events@puntocomaapps.es");
        emailIntent.setData(data);
        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "No encuentro ningún cliente de correo instalado", Toast.LENGTH_LONG).show();
        }
    }
}
