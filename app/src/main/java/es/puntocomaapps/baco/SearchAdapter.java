package es.puntocomaapps.baco;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    ArrayList<Evento> eventList;

    public SearchAdapter(ArrayList<Evento> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(eventList.get(position).getFoto())
                .into(holder.ivImagen);
        holder.txTitulo.setText(eventList.get(position).getTitulo());
        holder.txMunicipio.setText(eventList.get(position).getMunicipio());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(eventList.get(position).getFecha());
        } catch (ParseException ex) {
            ex.getStackTrace();
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        String fechaFormato = formato.format(date);
        holder.txFecha.setText(fechaFormato);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImagen = itemView.findViewById(R.id.ivEventoModelo);
        TextView txTitulo = itemView.findViewById(R.id.lblTituloModelo);
        TextView txFecha = itemView.findViewById(R.id.lblFechaModelo);
        TextView txMunicipio = itemView.findViewById(R.id.lblMunicipioModelo);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EventosActivity.class);
                    intent.putExtra("ficha", eventList.get(getAdapterPosition()));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
