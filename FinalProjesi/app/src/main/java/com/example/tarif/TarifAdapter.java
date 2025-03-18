package com.example.tarif;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class TarifAdapter extends RecyclerView.Adapter<TarifAdapter.TarifViewHolder> implements Filterable {
    private final Context context;
    private final List<Tarif> tarifList;
    private final List<Tarif> tarifListFull;
    private final OnItemClickListener listener;

    // OnItemClickListener interface'i
    public interface OnItemClickListener {
        void onItemClick(Tarif tarif);
    }

    public TarifAdapter(Context context, List<Tarif> tarifList, OnItemClickListener listener) {
        this.context = context;
        this.tarifList = tarifList;
        this.tarifListFull = new ArrayList<>(tarifList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TarifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tarif, parent, false);
        return new TarifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TarifViewHolder holder, int position) {
        Tarif tarif = tarifList.get(position);
        holder.textViewIsim.setText(tarif.getIsim());
        holder.textViewAciklama.setText(tarif.getAciklama());
        Glide.with(context).load(tarif.getResimId()).into(holder.imageViewResim);

        // Tıklama olayını ekle
        holder.itemView.setOnClickListener(v -> listener.onItemClick(tarif));
    }

    @Override
    public int getItemCount() {
        return tarifList.size();
    }

    @Override
    public Filter getFilter() {
        return tarifFilter;
    }

    public static class TarifViewHolder extends RecyclerView.ViewHolder {
        TextView textViewIsim, textViewAciklama;
        ImageView imageViewResim;

        public TarifViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewIsim = itemView.findViewById(R.id.textViewIsim);
            textViewAciklama = itemView.findViewById(R.id.textViewAciklama);
            imageViewResim = itemView.findViewById(R.id.imageViewResim);
        }
    }

    private final Filter tarifFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Tarif> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(tarifListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Tarif tarif : tarifListFull) {
                    if (tarif.getIsim().toLowerCase().contains(filterPattern)) {
                        filteredList.add(tarif);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tarifList.clear();
            tarifList.addAll((List<Tarif>) results.values);
            notifyDataSetChanged();
        }
    };

}
