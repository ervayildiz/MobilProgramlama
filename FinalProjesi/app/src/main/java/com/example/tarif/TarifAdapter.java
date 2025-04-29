package com.example.tarif;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.List;

public class TarifAdapter extends RecyclerView.Adapter<TarifAdapter.TarifViewHolder> {
    private final List<Tarif> tarifList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tarif tarif);
    }

    public TarifAdapter(List<Tarif> tarifList, OnItemClickListener listener) {
        this.tarifList = tarifList;
        this.listener = listener;
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

        holder.textViewIsim.setText(tarif.getAd());
        holder.textViewKategori.setText(tarif.getKategori());

        int imageResId = holder.itemView.getContext().getResources()
                .getIdentifier(tarif.getResimId(), "drawable", holder.itemView.getContext().getPackageName());
        if (imageResId != 0) {
            holder.imageViewResim.setImageResource(imageResId);
        } else {
            holder.imageViewResim.setImageResource(R.drawable.default_resim);  // 🔥 Yedek resim
        }


        // 🔥 Bookmark Senkronizasyonu
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String userId = currentUser.getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("favoriler")
                .whereEqualTo("tarifId", tarif.getTarifId())
                .get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        holder.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                    } else {
                        holder.btnBookmark.setImageResource(R.drawable.ic_bookmark_border);
                    }
                });

        // 🔥 Tıklamalar
        holder.itemView.setOnClickListener(v -> listener.onItemClick(tarif));
    }

    @Override
    public int getItemCount() {
        return tarifList.size();
    }
    public void updateList(List<Tarif> newList) {
        tarifList.clear();
        tarifList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class TarifViewHolder extends RecyclerView.ViewHolder {
        TextView textViewIsim, textViewKategori, textViewAciklama;
        ImageView imageViewResim;
        ImageButton btnBookmark;

        public TarifViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewIsim = itemView.findViewById(R.id.textViewIsim);
            textViewKategori = itemView.findViewById(R.id.textViewKategori);
            imageViewResim = itemView.findViewById(R.id.imageViewResim);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
        }
    }
}
