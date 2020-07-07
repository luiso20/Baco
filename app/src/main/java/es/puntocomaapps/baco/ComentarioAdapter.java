package es.puntocomaapps.baco;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class ComentarioAdapter extends RecyclerView.ViewHolder {

    ComentarioAdapter(@NonNull View itemView) {
        super(itemView);
    }

    void setDetails(Context context, String usuario, String stars, String comentarios) {

        TextView tvUser = itemView.findViewById(R.id.tvUsuario);
        TextView tvValoracion = itemView.findViewById(R.id.tvValoracion);
        TextView tvComentarios = itemView.findViewById(R.id.tvComentarios);

        tvUser.setText(usuario);
        tvValoracion.setText(stars);
        tvComentarios.setText(comentarios);
    }
}
