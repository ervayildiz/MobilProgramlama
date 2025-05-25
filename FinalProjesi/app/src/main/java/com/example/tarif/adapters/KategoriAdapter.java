package com.example.tarif.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarif.R;
import com.example.tarif.activities.TumTariflerActivity;
import com.example.tarif.models.Kategori;

import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {

    private final List<Kategori> kategoriList;
    private final OnKategoriClickListener listener;
    private final Context context;

    public KategoriAdapter(Context context, List<Kategori> kategoriList, OnKategoriClickListener listener) {
        this.context = context;
        this.kategoriList = kategoriList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kategori, parent, false);
        return new KategoriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriViewHolder holder, int position) {
        Kategori kategori = kategoriList.get(position);
        holder.textViewIsim.setText(kategori.getIsim());
        holder.imageViewIkon.setImageResource(kategori.getIkonResmi());

        holder.itemView.setOnClickListener(v -> {
            if (position == 0 && "Tüm Tarifler".equals(kategori.getIsim())) {
                context.startActivity(new Intent(context, TumTariflerActivity.class));
            } else {
                listener.onKategoriClick(kategori);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    public static class KategoriViewHolder extends RecyclerView.ViewHolder {
        TextView textViewIsim;
        ImageView imageViewIkon;

        public KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewIsim = itemView.findViewById(R.id.textViewIsim);
            imageViewIkon = itemView.findViewById(R.id.imageViewIkon);
        }
    }

    public interface OnKategoriClickListener {
        void onKategoriClick(Kategori kategori);
    }
}
