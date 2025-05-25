// NoteAdapter.java
package com.example.tarif.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarif.R;
import com.example.tarif.models.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private String type; // "alisveris" veya "not"

    public NoteAdapter(List<Note> noteList, String type) {
        this.noteList = noteList;
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
        notifyDataSetChanged();
    }

    public void updateList(List<Note> newList) {
        this.noteList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.txtNote.setText(note.getText());

        if ("alisveris".equals(type)) {
            holder.itemView.setBackgroundResource(R.color.soft_pink);
        } else {
            holder.itemView.setBackgroundResource(R.color.soft_green);
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNote = itemView.findViewById(R.id.txtNote);
        }
    }
}
