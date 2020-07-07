package es.puntocomaapps.baco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        //Contenido

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            ProfileFragment profileFragment = new ProfileFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.profileContainer, profileFragment);
            ft.commit();
        } else {
            RegistroFragment registroFragment = new RegistroFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.profileContainer, registroFragment);
            ft.commit();
        }

        //Fin contenido

        // Barra inferior - Menú
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        Intent i1 = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(i1);
                        break;
                    case R.id.filter:
                        Intent i2 = new Intent(ProfileActivity.this, FilterActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.profile:
                        break;
                }
                return true;
            }
        } );
        // Fin barra inferior
    }
}
