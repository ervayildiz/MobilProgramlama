package com.example.tarif;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TarifAdapter extends RecyclerView.Adapter<TarifAdapter.TarifViewHolder> {
    private List<Tarif> tarifList;
    private final OnTarifClickListener listener;

    public TarifAdapter(List<Tarif> tarifList, OnTarifClickListener listener) {
        this.tarifList = tarifList;
        this.listener = listener;
    }

    public void updateList(List<Tarif> newList) {
        tarifList = new ArrayList<>();
        tarifList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TarifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarif, parent, false);
        return new TarifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TarifViewHolder holder, int position) {
        Tarif tarif = tarifList.get(position);
        holder.textViewAd.setText(tarif.getAd());

        // Resmi yükle (resimId drawable adı olarak kullanılıyorsa)
        try {
            int resId = holder.itemView.getContext().getResources()
                    .getIdentifier(tarif.getResimId(), "drawable",
                            holder.itemView.getContext().getPackageName());
            holder.imageView.setImageResource(resId);
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.default_resim);
        }

        holder.itemView.setOnClickListener(v -> listener.onTarifClick(tarif));
    }

    @Override
    public int getItemCount() {
        return tarifList.size();
    }

    static class TarifViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAd;
        ImageView imageView;

        public TarifViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAd = itemView.findViewById(R.id.textViewIsim);
            imageView = itemView.findViewById(R.id.imageViewResim);
        }
    }

    public interface OnTarifClickListener {
        void onTarifClick(Tarif tarif);
    }
}