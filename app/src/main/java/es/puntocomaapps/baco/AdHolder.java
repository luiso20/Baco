package es.puntocomaapps.baco;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class AdHolder extends RecyclerView.ViewHolder {

    private View mView;
    Context context;

    public AdHolder(View itemView) {
        super(itemView);

        mView = itemView;
        context = itemView.getContext();
    }
}
