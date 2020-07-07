package es.puntocomaapps.baco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FilterActivity extends AppCompatActivity {

    Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String fechaActual = dateFormat.format(date);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

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

        MobileAds.initialize(this, initializationStatus -> {});

        final AdView adViewFiltro = findViewById(R.id.adViewFiltro);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewFiltro.setAdListener(new AdListener());
        adViewFiltro.loadAd(adRequest);

        Spinner spMunicipios = findViewById(R.id.spinnerMun);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.municipios_array,
                android.R.layout.simple_spinner_dropdown_item );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spMunicipios.setAdapter( adapter );

        spMunicipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String mun = parent.getItemAtPosition( position ).toString();

                    FiltroFragment filtroFragment = new FiltroFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("municipio", mun);
                    filtroFragment.setArguments(bundle);

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.contenedor, filtroFragment);
                    ft.commit();

                    TextView tvInstrucciones = findViewById(R.id.tvInstrucciones);
                    tvInstrucciones.setText("");
                    ImageView ivArrow = findViewById(R.id.ivArrow);
                    ivArrow.setVisibility(View.GONE);
                    adViewFiltro.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        // Fin contenido


        // Barra inferior - Menú

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    Intent i1 = new Intent(FilterActivity.this, MainActivity.class);
                    startActivity(i1);
                    break;
                case R.id.filter:
                    break;
                case R.id.profile:
                    Intent i2 = new Intent(FilterActivity.this, ProfileActivity.class);
                    startActivity(i2);
                    break;
            }
            return true;
        });
        // Fin barra inferior
    }
}
