package com.example.tarif.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarif.R;

import java.util.List;
import java.util.Map;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ItemViewHolder> {

    public interface OnCheckedChangeListener {
        void onCheckedChanged(String item, boolean isChecked);
    }

    public interface OnItemLabelChangedListener {
        void onItemLabelChanged(String oldItem, String newItem);
    }

    private List<String> itemList;
    private Map<String, Boolean> itemStates;
    private OnCheckedChangeListener checkedListener;
    private OnItemLabelChangedListener labelChangeListener;

    public ShoppingItemAdapter(List<String> itemList, Map<String, Boolean> itemStates,
                               OnCheckedChangeListener checkedListener,
                               OnItemLabelChangedListener labelChangeListener) {
        this.itemList = itemList;
        this.itemStates = itemStates;
        this.checkedListener = checkedListener;
        this.labelChangeListener = labelChangeListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        String item = itemList.get(position);
        holder.checkBox.setChecked(itemStates.get(item) != null && itemStates.get(item));
        holder.editText.setText(item);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkedListener.onCheckedChanged(item, isChecked);
        });

        holder.editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newLabel = holder.editText.getText().toString().trim();
                if (!newLabel.equals(item)) {
                    labelChangeListener.onItemLabelChanged(item, newLabel);
                } else if (newLabel.isEmpty()) {
                    labelChangeListener.onItemLabelChanged(item, "");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        EditText editText;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkShopping);
            editText = itemView.findViewById(R.id.editShoppingItem);
        }
    }
}
