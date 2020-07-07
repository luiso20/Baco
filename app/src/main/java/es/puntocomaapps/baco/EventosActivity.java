package es.puntocomaapps.baco;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventosActivity extends AppCompatActivity {

    private String titulo;
    private String lugar;
    private String fecha;
    private String hora;
    private String descripcion;
    private String foto;
    private String id;
    private String municipio;
    private String precio;
    private String url;
    private ToggleButton tbFavs;
    FirebaseAuth mAuth;
    private FirebaseUser user;
    private int horaEvento;
    private Uri shortLink;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

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

        TextView tvTitulo = findViewById(R.id.tvTituloEvento);
        TextView tvLugar = findViewById(R.id.tvLugarEvento);
        TextView tvMunicipio = findViewById(R.id.tvMunicipio);
        TextView tvFecha = findViewById(R.id.tvFechaEvento);
        TextView tvHora = findViewById(R.id.tvHoraEvento);
        TextView tvDescripcion = findViewById(R.id.tvDescripcionEvento);
        final ImageView ivImagen = findViewById(R.id.ivImagenEvento);
        TextView tvPrecio = findViewById(R.id.tvPrecio);
        ImageButton btnAddCalendario = findViewById(R.id.btnAddCal);
        ImageButton btnPlace = findViewById(R.id.btnPlace);
        ImageButton btnShare = findViewById(R.id.btnShare);
        ImageButton btnStar = findViewById(R.id.btnStar);
        ImageButton btnTicket = findViewById(R.id.btnTicket);
        tbFavs = findViewById(R.id.tbFavs);
        Boolean toggleState = tbFavs.isChecked();
        mAuth = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Evento evento = (Evento) extras.getSerializable("ficha");
            assert evento != null;
            titulo = evento.getTitulo();
            lugar = evento.getUbicacion();
            municipio = evento.getMunicipio();
            fecha = evento.getFecha();
            hora = evento.getHora();
            descripcion = evento.getDescripcion();
            foto = evento.getFoto();
            precio = evento.getPrecio();
            url = evento.getUrl();
            id = evento.getId();
        }

        if ((url == null) || (url.isEmpty())) {
            btnTicket.setVisibility(View.GONE);
        }

        Glide.with(this)
                .load(foto)
                .into(ivImagen);

        ivImagen.setOnClickListener(v -> {
            Intent intent = new Intent(EventosActivity.this, EventoImagen.class);
            intent.putExtra("imagenCompleta", foto);
            startActivity(intent);
        });

        tvTitulo.setText(titulo);
        tvLugar.setText(lugar);
        tvMunicipio.setText(municipio);
        try {
            if (!hora.equals("")) {
                tvHora.setText(hora);
            } else {
                tvHora.setText(R.string.hora_no_definida);
            }
        } catch (NullPointerException e) {
            e.getMessage();
        }
        tvDescripcion.setText(descripcion);
        String precio_evento = getString(R.string.precio_del_evento) + " " + precio;
        tvPrecio.setText(precio_evento);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(fecha);
        } catch (ParseException ex) {
            ex.getStackTrace();
        }

        SimpleDateFormat formato = new SimpleDateFormat("dd 'de' MMMM',' yyyy");
        assert date != null;
        final String fechaFormato = formato.format(date);
        tvFecha.setText(fechaFormato);

        user = mAuth.getCurrentUser();
        if (user != null) {
            compruebaFavorito(user.getUid(), id);
        }

        tbFavs.setOnClickListener(view -> {
            if (user != null) {
                guardarFavorito(user.getUid(), id);
            } else {
                Toast.makeText(getApplicationContext(), "Inicie sesión para marcar favoritos", Toast.LENGTH_LONG).show();
                tbFavs.setChecked(false);
            }
        });

        btnPlace.setOnClickListener(v -> {
            String lugarEncoded = lugar.replace(" ", "+");
            Uri uri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lugarEncoded);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        });

        final String dia, mes, anyo;
        dia = new SimpleDateFormat("dd").format(date);
        mes = new SimpleDateFormat("MM").format(date);
        anyo = new SimpleDateFormat("yyyy").format(date);

        if (!hora.equals("")) {
            String[] toMillis = hora.split(":");
            horaEvento = Integer.parseInt(toMillis[1]) * 60000 + Integer.parseInt(toMillis[0]) * 3600000;
        }

        btnAddCalendario.setOnClickListener(v -> {
            Intent calIntent = new Intent(Intent.ACTION_INSERT);
            calIntent.setType("vnd.android.cursor.item/event");
            calIntent.putExtra(CalendarContract.Events.TITLE, titulo);
            calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, lugar);
            calIntent.putExtra(CalendarContract.Events.DESCRIPTION, descripcion);

            GregorianCalendar calDate = new GregorianCalendar(Integer.parseInt(anyo), Integer.parseInt(mes) - 1, Integer.parseInt(dia));
            long eventoInMillis = calDate.getTimeInMillis() + horaEvento;
            calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventoInMillis);

            startActivity(calIntent);
        });

        final Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(generateContentLink(id, titulo, foto))
                .buildShortDynamicLink()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        shortLink = Objects.requireNonNull(task.getResult()).getShortLink();
                        Uri flowChartLink = task.getResult().getPreviewLink();
                        Log.e("EventosActivity", shortLink.toString());
                    } else {
                        Log.e("EventosActivity"," error " + task.getException());
                    }
                });

        btnShare.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);

            String msg = "¡Mira el evento que he encontrado!: " + shortLink;
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            intent.setType("text/plain");

            startActivity(Intent.createChooser(intent, "Compartir evento"));
        });

        btnStar.setOnClickListener(view -> {
            Intent intent = new Intent(EventosActivity.this, ComentariosActivity.class);
            intent.putExtra("evento", id);
            startActivity(intent);
        });

        btnTicket.setOnClickListener(v -> {
            Uri uri = Uri.parse(url);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        });

        // Barra inferior - Menú
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    Intent i0 = new Intent(EventosActivity.this, MainActivity.class);
                    startActivity(i0);
                    break;
                case R.id.filter:
                    Intent i1 = new Intent(EventosActivity.this, FilterActivity.class);
                    startActivity(i1);
                    break;
                case R.id.profile:
                    Intent i2 = new Intent(EventosActivity.this, ProfileActivity.class);
                    startActivity(i2);
                    break;
            }
            return true;
        });
        // Fin barra inferior
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void guardarFavorito(final String user, final String id_evento) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference reference = mDatabase.child("usuario");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user).child("favoritos").child(id).exists()){
                    DatabaseReference dbRefBorraFavorito = reference.child(user).child("favoritos").child(id);
                    dbRefBorraFavorito.removeValue();
                } else {
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put(user+"/favoritos/"+id, true);
                    reference.updateChildren(userUpdates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void compruebaFavorito(final String user, final String id_evento) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference reference = mDatabase.child("usuario");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user).child("favoritos").child(id).exists()){
                    tbFavs.setChecked(true);
                } else {
                    tbFavs.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static Uri generateContentLink(String id, String titulo, String imagen) {

        Uri baseUrl = Uri.parse("https://www.puntocomaapps.es/?id:" + id );
        String domain = "https://baco.puntocomaapps.es/";

        DynamicLink link = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(baseUrl)
                .setDomainUriPrefix(domain)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("baco.puntocomaapps.es")
                        .setMinimumVersion(11)
                        .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(titulo)
                                .setDescription("¡Entra e infórmate de este evento!")
                                .setImageUrl(Uri.parse(imagen))
                                .build())
                .buildDynamicLink();

        return link.getUri();
    }
}
