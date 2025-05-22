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
import com.example.tarif.Tarif;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.List;

public class TarifAdapter extends RecyclerView.Adapter<TarifAdapter.TarifViewHolder> {
    private final List<Tarif> tarifList;
    private final OnItemClickListener listener;
    private final FavoriToggleListener toggleListener;

    public interface OnItemClickListener {
        void onItemClick(Tarif tarif);
    }

    public interface FavoriToggleListener {
        void onFavoriToggled(Tarif tarif, boolean yeniDurum);
    }

    public TarifAdapter(List<Tarif> tarifList, OnItemClickListener listener, FavoriToggleListener toggleListener) {
        this.tarifList = tarifList;
        this.listener = listener;
        this.toggleListener = toggleListener;
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

        String resimAdi = tarif.getResimId();
        if (resimAdi != null && !resimAdi.isEmpty()) {
            int imageResId = holder.itemView.getContext().getResources()
                    .getIdentifier(resimAdi, "drawable", holder.itemView.getContext().getPackageName());

            if (imageResId != 0) {
                holder.imageViewResim.setImageResource(imageResId);
            } else {
                holder.imageViewResim.setImageResource(R.drawable.default_resim);
            }
        } else {
            holder.imageViewResim.setImageResource(R.drawable.default_resim);
        }



        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("favoriler")
                    .whereEqualTo("tarifId", tarif.getTarifId())
                    .get(Source.SERVER)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            tarif.setFavori(true);
                            holder.btnBookmark.setImageResource(R.drawable.ic_bookmark_filled);
                        } else {
                            tarif.setFavori(false);
                            holder.btnBookmark.setImageResource(R.drawable.ic_bookmark_border);
                        }
                    });
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(tarif));

        holder.btnBookmark.setOnClickListener(v -> {
            boolean yeniDurum = !tarif.isFavori();
            tarif.setFavori(yeniDurum);
            notifyItemChanged(holder.getAdapterPosition());
            toggleListener.onFavoriToggled(tarif, yeniDurum);
        });
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
