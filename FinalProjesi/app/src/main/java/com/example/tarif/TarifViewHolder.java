package com.example.tarif;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

public class TarifViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvIsim;
    private final TextView tvKategori;

    private final ImageView ivResim;

    public TarifViewHolder(@NonNull View itemView) {
        super(itemView);
        tvIsim = itemView.findViewById(R.id.textViewIsim);
        tvKategori = itemView.findViewById(R.id.textViewKategori);
        ivResim = itemView.findViewById(R.id.imageViewResim);
    }

    public void bind(Tarif tarif) {
        tvIsim.setText(tarif.getAd());
        tvKategori.setText(tarif.getKategori());

        Glide.with(itemView.getContext())
                .load(tarif.getResimId())
                .into(ivResim);
    }
}