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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class FiltroFragment extends Fragment {

    public static final int TYPE_POST = 0;
    public static final int TYPE_AD = 1;

    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3170770616008852/3051289383";
    private static final String ADMOB_AD_UNIT_ID_TEST = "ca-app-pub-3940256099942544/2247696110";

    private UnifiedNativeAd nativeAd;

    private FirebaseRecyclerAdapter<Evento, RecyclerView.ViewHolder> mAdapter;
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

        MobileAds.initialize(getContext(), initializationStatus -> {});

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

                    mAdapter = new FirebaseRecyclerAdapter<Evento, RecyclerView.ViewHolder>(options) {

                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                            if (i == TYPE_POST) {
                                View eventView = LayoutInflater.from(viewGroup.getContext())
                                        .inflate(R.layout.item_lista, viewGroup, false);
                                return new EventoHolder(eventView);
                            } else {
                                UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(viewGroup.getContext())
                                        .inflate(R.layout.ad_unified, viewGroup, false);

                                AdLoader.Builder builder = new AdLoader.Builder(Objects.requireNonNull(getContext()), ADMOB_AD_UNIT_ID);

                                builder.forUnifiedNativeAd(unifiedNativeAd -> {
                                    // You must call destroy on old ads when you are done with them,
                                    // otherwise you will have a memory leak.
                                    if (nativeAd != null) {
                                        nativeAd.destroy();
                                    }
                                    nativeAd = unifiedNativeAd;

                                    PopulateUnifiedNativeAdView populateUnifiedNativeAdView = new PopulateUnifiedNativeAdView(unifiedNativeAd, adView);

                                });

                                VideoOptions videoOptions = new VideoOptions.Builder()
                                        .build();

                                NativeAdOptions adOptions = new NativeAdOptions.Builder()
                                        .setVideoOptions(videoOptions)
                                        .build();

                                builder.withNativeAdOptions(adOptions);

                                AdLoader adLoader = builder.withAdListener(new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(int i) {
                                        Toast.makeText(getContext(), R.string.dialog_error_native_ad
                                                + i, Toast.LENGTH_SHORT).show();
                                    }
                                }).build();

                                adLoader.loadAd(new AdRequest.Builder().build());

                                return new AdHolder(adView);
                            }
                        }

                        @Override
                        public int getItemViewType(int position) {
                            if ((position % 8) == 0) {
                                return TYPE_AD;
                            } else {
                                return TYPE_POST;
                            }
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position, @NonNull final Evento evento) {
                            if (getItemViewType(position) == TYPE_POST) {
                                EventoHolder eventoHolder = (EventoHolder) holder;
                                eventoHolder.setDetails(getContext(), evento.getFoto(), evento.getTitulo(),
                                        evento.getFecha(), evento.getMunicipio());

                                eventoHolder.setOnClickListener((view1, position1) -> {
                                    Intent intent = new Intent(getContext(), EventosActivity.class);
                                    intent.putExtra( "ficha", evento );
                                    startActivity( intent );
                                });
                            }
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
                    tvEmailEventos.setOnClickListener(v -> enviarEmail());
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
            Toast.makeText(getContext(), R.string.dialog_email_not_found, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroyView();
    }
}
