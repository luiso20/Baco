package es.puntocomaapps.baco;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventoHolder extends RecyclerView.ViewHolder {

    private View mView;
    Context context;

    public EventoHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        } );

        mView = itemView;
        context = itemView.getContext();
    }

    private EventoHolder.ClickListener mClickListener;

    public interface ClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnClickListener(EventoHolder.ClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setDetails(Context context, String foto, String titulo, String fecha, String municipio){

        ImageView ivImagen = mView.findViewById(R.id.ivEventoModelo);
        TextView txTitulo = mView.findViewById(R.id.lblTituloModelo);
        TextView txFecha = mView.findViewById(R.id.lblFechaModelo);
        TextView txMunicipio = mView.findViewById(R.id.lblMunicipioModelo);

        Glide.with(context)
                .load(foto)
                .into(ivImagen);
        txTitulo.setText(titulo);
        txMunicipio.setText(municipio);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(fecha);
        } catch (ParseException ex) {
            ex.getStackTrace();
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        assert date != null;
        String fechaFormato = formato.format(date);
        txFecha.setText(fechaFormato);

    }
}
