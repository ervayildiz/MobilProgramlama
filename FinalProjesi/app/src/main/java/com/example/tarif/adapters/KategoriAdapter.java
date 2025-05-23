package com.example.tarif.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.tarif.R;
import com.example.tarif.models.Kategori;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {
    private final List<Kategori> kategoriList;
    private final OnKategoriClickListener listener;

    // Constructor
    public KategoriAdapter(List<Kategori> kategoriList, OnKategoriClickListener listener) {
        this.kategoriList = kategoriList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kategori, parent, false);
        return new KategoriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriViewHolder holder, int position) {
        Kategori kategori = kategoriList.get(position);
        holder.textViewIsim.setText(kategori.getIsim());
        holder.imageViewIkon.setImageResource(kategori.getIkonResmi());

        // Kategoriye tıklama olayı
        holder.itemView.setOnClickListener(v -> listener.onKategoriClick(kategori));
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    // ViewHolder sınıfı
    public static class KategoriViewHolder extends RecyclerView.ViewHolder {
        TextView textViewIsim;
        ImageView imageViewIkon;

        public KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewIsim = itemView.findViewById(R.id.textViewIsim);
            imageViewIkon = itemView.findViewById(R.id.imageViewIkon);
        }
    }

    // Kategori tıklama olayı için interface
    public interface OnKategoriClickListener {
        void onKategoriClick(Kategori kategori);
    }
}