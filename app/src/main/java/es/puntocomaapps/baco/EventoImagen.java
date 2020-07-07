package es.puntocomaapps.baco;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class EventoImagen extends AppCompatActivity {

    private PhotoView imagen;
    private String foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utility.lockOrientation(this);
        setContentView(R.layout.activity_evento_imagen);

        imagen = findViewById(R.id.ivImagenCompleta);
        foto = getIntent().getStringExtra("imagenCompleta");
        Glide.with( this )
                .load(foto)
                .into(imagen);
    }

}
