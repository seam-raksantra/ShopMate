package com.example.shopmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.model.GroceryItem;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {

    private List<GroceryItem> list;
    private Context context;

    // Callback for checkbox changes
    private OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged();
    }

    // New constructor (with callback)
    public GroceryAdapter(Context context, List<GroceryItem> list, OnSelectionChangedListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    // OLD constructor kept for compatibility (optional)
    public GroceryAdapter(Context context, List<GroceryItem> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grocery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GroceryItem item = list.get(position);

        holder.txtName.setText(item.getName());

        // Load image from drawable
        int resId = context.getResources().getIdentifier(item.getImage(), "drawable", context.getPackageName());
        holder.imgIcon.setImageResource(resId);

        // --- Reset listener before binding state ---
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isChecked());

        // --- Checkbox listener ---
        holder.checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
            item.setChecked(isChecked);
            if (listener != null) listener.onSelectionChanged();
        });

        // --- Whole item click toggles checkbox ---
        holder.itemLayout.setOnClickListener(v -> {
            boolean newState = !holder.checkBox.isChecked();
            holder.checkBox.setChecked(newState);
            item.setChecked(newState);

            if (listener != null) listener.onSelectionChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName;
        ImageView imgIcon;
        CheckBox checkBox;
        LinearLayout itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            checkBox = itemView.findViewById(R.id.checkboxItem);
            itemLayout = itemView.findViewById(R.id.itemLayout);
        }
    }
}
