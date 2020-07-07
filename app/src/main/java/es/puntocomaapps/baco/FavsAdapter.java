package es.puntocomaapps.baco;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FavsAdapter extends ArrayAdapter<Evento> {

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public FavsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    FavsAdapter(@NonNull Context context, ArrayList<Evento> eventos) {
        super(context, 0, eventos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Evento evento = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_favoritos, parent, false);
        }

        TextView tvTituloFavorito = convertView.findViewById(R.id.tvTituloFavorito);
        TextView tvFechaFavorito = convertView.findViewById(R.id.tvFechaFavorito);

        assert evento != null;
        tvTituloFavorito.setText(evento.getTitulo());
        String fecha = evento.getFecha();

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
        tvFechaFavorito.setText(fechaFormato);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.dialog_message_delete_Fav);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("usuario").child(user.getUid()).child("favoritos");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals(evento.getId())) {
                                reference.child(snapshot.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EventosActivity.class);
                intent.putExtra("ficha", evento);
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
