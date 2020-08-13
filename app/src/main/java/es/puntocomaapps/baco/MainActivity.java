package es.puntocomaapps.baco;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int TYPE_POST = 0;
    public static final int TYPE_AD = 1;

    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3170770616008852/3051289383";
    //private static final String ADMOB_AD_UNIT_ID_TEST = "ca-app-pub-3940256099942544/2247696110";

    private UnifiedNativeAd nativeAd;

    FirebaseRecyclerAdapter<Evento, RecyclerView.ViewHolder> mAdapter;
    private ProgressBar pBarMain;
    String TAG = "main";
    Query queryEventos;
    RecyclerView rvEventos;
    RecyclerView.Adapter<SearchAdapter.ViewHolder> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, initializationStatus -> {});

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.e("newToken", newToken);

        });

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                    }

                    if (deepLink != null) {
                        String cadena = deepLink.toString();
                        final String id = cadena.substring(cadena.indexOf("?id:")+4);
                        Log.e(TAG, "deepLink: " + cadena);
                        BaseDatosFirebase bdFirebase = new BaseDatosFirebase();
                        final DatabaseReference reference = bdFirebase.getDbReference().child("evento");

                        final Evento evento = new Evento();

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot item : dataSnapshot.getChildren()) {
                                    if (Objects.equals(item.getKey(), id)) {
                                        evento.setTitulo(Objects.requireNonNull(item.child("titulo").getValue()).toString());
                                        evento.setUbicacion(Objects.requireNonNull(item.child("ubicacion").getValue()).toString());
                                        evento.setMunicipio(Objects.requireNonNull(item.child("municipio").getValue()).toString());
                                        evento.setFecha(Objects.requireNonNull(item.child("fecha").getValue()).toString());
                                        evento.setHora(Objects.requireNonNull(item.child("hora").getValue()).toString());
                                        evento.setDescripcion(Objects.requireNonNull(item.child("descripcion").getValue()).toString());
                                        evento.setFoto(Objects.requireNonNull(item.child("foto").getValue()).toString());
                                        evento.setPrecio(Objects.requireNonNull(item.child("precio").getValue()).toString());
                                        evento.setUrl(Objects.requireNonNull(item.child("url").getValue()).toString());
                                        evento.setId(Objects.requireNonNull(item.child("id").getValue()).toString());

                                        Intent intent = new Intent(MainActivity.this, EventosActivity.class);
                                        intent.putExtra("ficha", evento);
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "getDynamicLink : OnFailure", e));

        Utility.lockOrientation(this);

        // Barra superior - Título
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String fuenteTitulo = "fonts/Selima.otf";
        Typeface selima = Typeface.createFromAsset(getAssets(), fuenteTitulo);

        TextView tvAppName = findViewById(R.id.tvAppName);
        tvAppName.setTextSize(50);
        tvAppName.setTypeface(selima);
        // Fin barra superior

        // Contenido

        pBarMain = findViewById(R.id.pBarMain);

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fechaActual = "";
        try {
            fechaActual = dateFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + date);
        }

        BaseDatosFirebase bdFirebase = new BaseDatosFirebase();
        queryEventos = bdFirebase.getDbReference().child("evento").orderByChild("fecha").startAt(fechaActual);
        queryEventos.keepSynced(true);
        showEvents(queryEventos);

        // Fin Contenido

        // Barra inferior - Menú
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    break;
                case R.id.filter:
                    Intent i1 = new Intent(MainActivity.this, FilterActivity.class);
                    startActivity(i1);
                    break;
                case R.id.profile:
                    Intent i2 = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(i2);
                    break;
            }
            return true;
        });
        // Fin barra inferior
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_superior, menu);

        MenuItem mSearch = menu.findItem(R.id.new_search);

        SearchView searchView = (SearchView) mSearch.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void showEvents(Query query){

        FirebaseRecyclerOptions<Evento> options =
                new FirebaseRecyclerOptions.Builder<Evento>()
                        .setQuery(query, Evento.class)
                        .build();

        rvEventos = findViewById(R.id.lstEventos);
        int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(), 140);
        rvEventos.setLayoutManager(new GridLayoutManager(getApplicationContext(), mNoOfColumns));

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

                    AdLoader.Builder builder = new AdLoader.Builder(getApplicationContext(), ADMOB_AD_UNIT_ID);

                    builder.forUnifiedNativeAd(unifiedNativeAd -> {
                        // You must call destroy on old ads when you are done with them,
                        // otherwise you will have a memory leak.
                        if (nativeAd != null) {
                            nativeAd.destroy();
                        }
                        nativeAd = unifiedNativeAd;

                        PopulateUnifiedNativeAdView populateUnifiedNativeAdView = new PopulateUnifiedNativeAdView(unifiedNativeAd, adView);
                        //populateUnifiedNativeAdView(unifiedNativeAd, adView);

                    });

                    VideoOptions videoOptions = new VideoOptions.Builder()
                            .build();

                    NativeAdOptions adOptions = new NativeAdOptions.Builder()
                            .setVideoOptions(videoOptions)
                            .build();

                    builder.withNativeAdOptions(adOptions);

                    AdLoader adLoader = builder.withAdListener(new AdListener() {
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
                    eventoHolder.setDetails(getApplicationContext(), evento.getFoto(), evento.getTitulo(),
                            evento.getFecha(), evento.getMunicipio());

                    eventoHolder.setOnClickListener((view, position1) -> {
                        Intent intent = new Intent(MainActivity.this, EventosActivity.class);
                        intent.putExtra("ficha", evento);
                        startActivity(intent);
                    });
                }
                pBarMain.setVisibility(View.INVISIBLE);
            }
        };

        mAdapter.startListening();
        rvEventos.setAdapter(mAdapter);
    }

    private void firebaseSearch(String searchText){

        String query = searchText.toLowerCase();
        ArrayList<Evento> eventList = new ArrayList<>();

        listAdapter = new SearchAdapter(eventList);
        rvEventos = findViewById(R.id.lstEventos);
        rvEventos.setHasFixedSize(true);
        int mNoOfColumns = Utility.calculateNoOfColumns(getApplicationContext(), 140);
        rvEventos.setLayoutManager(new GridLayoutManager(getApplicationContext(), mNoOfColumns));
        rvEventos.setAdapter(listAdapter);

        BaseDatosFirebase bdFirebase = new BaseDatosFirebase();
        DatabaseReference reference = bdFirebase.getDbReference().child("evento");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Evento evento = snapshot.getValue(Evento.class);
                        assert evento != null;

                        if (evento.getKeywords() != null) {
                            if (evento.getKeywords().contains(query)) {
                                eventList.add(evento);
                                Log.e(TAG, evento.toString());
                                listAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getVersionActual(Context ctx){
        try {
            return ctx.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    class updateApplication extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String newVersion = null;
            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName()  + "&hl=en")
                        .timeout(30000)
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Log.d("updateAndroid", "Document: " + document);
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElements = ele.siblingElements();
                            for (Element sibElement : sibElements) {
                                newVersion = sibElement.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;

        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            String currentVersion = getVersionActual(getApplicationContext());
            Log.d("updateAndroid", "Current version: " + currentVersion + " PlayStore version: " + onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if(isUpdateRequired(currentVersion, onlineVersion)){
                    Log.d("updateAndroid", "¡Hay una actualización! versión actual: " + currentVersion + " Versión en la PlayStore: " + onlineVersion);
                    openPlayStore(getApplicationContext()); //Open PlayStore
                }else{
                    Log.d("updateAndroid", "No hay actualizaciones");
                }
            }

        }

        private void openPlayStore(Context ctx){
            final String appPackageName = ctx.getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }

        boolean isUpdateRequired(String versionActual, String versionNueva) {
            boolean result = false;
            int[] versiones = new int[6];
            int i, anterior = 0, orden = 0;
            if(versionActual != null && versionNueva != null){
                try{
                    for(i = 0; i < 6; i++){
                        versiones[i] = 0;
                    }
                    do{
                        i = versionActual.indexOf('.', anterior);
                        if(i > 0){
                            versiones[orden] = Integer.parseInt(versionActual.substring(anterior, i));
                        }else{
                            versiones[orden] = Integer.parseInt(versionActual.substring(anterior));
                        }
                        anterior = i + 1;
                        orden++;
                    }while(i != -1);
                    anterior = 0;
                    orden = 3;
                    do{
                        i = versionNueva.indexOf('.', anterior);
                        if(i > 0){
                            versiones[orden] = Integer.parseInt(versionNueva.substring(anterior, i));
                        }else{
                            versiones[orden] = Integer.parseInt(versionNueva.substring(anterior));
                        }
                        anterior = i + 1;
                        orden++;
                    }while(i != -1 && orden < 6);
                    if(versiones[0] < versiones[3]){
                        result = true;
                    }else if(versiones[1] < versiones[4] && versiones[0] == versiones[3]){
                        result = true;
                    }else if(versiones[2] < versiones[5] && versiones[0] == versiones[3] && versiones[1] == versiones[4]){
                        result = true;
                    }
                }catch (NumberFormatException e){
                    Log.e("updateApp", "NFE " + e.getMessage() + " parsing versionAct " + versionActual + " and versionNew " + versionNueva);
                }catch (Exception e){
                    Log.e("updateApp", "Ex " + e.getMessage() + " parsing versionAct " + versionActual + " and versionNew " + versionNueva);
                }
            }
            return result;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        new updateApplication().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }
}
