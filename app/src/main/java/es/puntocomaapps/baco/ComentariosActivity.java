package es.puntocomaapps.baco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Query;

public class ComentariosActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        Utility.lockOrientation(this);
        // Barra superior - Título
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String fuenteTitulo = "fonts/Selima.otf";
        Typeface selima = Typeface.createFromAsset(getAssets(), fuenteTitulo);

        TextView tvAppName = findViewById(R.id.tvAppName);
        tvAppName.setTextSize(50);
        tvAppName.setTypeface(selima);
        // Fin barra superior

        TextView tvLogin = findViewById(R.id.tvLogin);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        final String id_evento = bundle.getString("evento");

        if (user != null) {
            tvLogin.setVisibility(View.GONE);
            ComentariosFragment fragment = new ComentariosFragment();
            Bundle data = new Bundle();
            data.putString("evento", id_evento);
            fragment.setArguments(data);

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.CommentContainer, fragment);
            ft.commit();
        } else {
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ComentariosActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        }

        RecyclerView rvComentariosEventos = findViewById(R.id.rvComentariosEventos);
        rvComentariosEventos.setLayoutManager(new LinearLayoutManager(this));

        BaseDatosFirebase bdFirebase = new BaseDatosFirebase();
        Query query = bdFirebase.getDbReference().child( "evento_comentario" ).orderByChild("id_evento").equalTo(id_evento);

        FirebaseRecyclerOptions<Comentario> options =
                new FirebaseRecyclerOptions.Builder<Comentario>()
                        .setQuery( query, Comentario.class )
                        .build();

        mAdapter = new FirebaseRecyclerAdapter<Comentario, ComentarioAdapter>(options) {
            @NonNull
            @Override
            public ComentarioAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentarios, parent, false);
                return new ComentarioAdapter(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ComentarioAdapter holder, int position, @NonNull Comentario model) {
                holder.setDetails(getApplicationContext(), model.getUsuario(), model.getValoracion(), model.getComentario());
            }
        };

        rvComentariosEventos.setAdapter( mAdapter );

        // Barra inferior - Menú
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        Intent i0 = new Intent(ComentariosActivity.this, MainActivity.class);
                        startActivity(i0);
                        break;
                    case R.id.filter:
                        Intent i1 = new Intent(ComentariosActivity.this, FilterActivity.class);
                        startActivity(i1);
                        break;
                    case R.id.profile:
                        Intent i2 = new Intent(ComentariosActivity.this, ProfileActivity.class);
                        startActivity(i2);
                        break;
                }
                return true;
            }
        } );
        // Fin barra inferior
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}
