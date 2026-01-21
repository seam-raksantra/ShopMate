package com.example.shopmate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.model.GroceryItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private final List<GroceryItem> originalList;
    private final List<GroceryItem> filteredList;
    private final SelectionListener listener;

    public interface SelectionListener {
        void onSelectionChanged();
    }

    // Main constructor (with selection listener)
    public GroceryAdapter(Context context, List<GroceryItem> list, SelectionListener listener) {
        this.context = context;
        this.originalList = (list != null) ? new ArrayList<>(list) : new ArrayList<>();
        this.filteredList = new ArrayList<>(this.originalList);
        this.listener = listener;
    }

    // Convenience constructor (no selection callback)
    public GroceryAdapter(Context context, List<GroceryItem> list) {
        this(context, list, null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_grocery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        GroceryItem item = filteredList.get(position);

        // Name
        h.txtName.setText(item.getName());

        // Image from drawable name in GroceryItem.getImage()
        String imageName = item.getImage(); // e.g. "carrot"
        if (imageName != null && !imageName.isEmpty()) {
            int resId = context.getResources()
                    .getIdentifier(imageName, "drawable", context.getPackageName());
            if (resId != 0) {
                h.imgIcon.setImageResource(resId);
            } else {
                h.imgIcon.setImageResource(R.drawable.ic_launcher_foreground);
            }
        } else {
            h.imgIcon.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Date label (used in history; harmless elsewhere)
        if (h.txtDate != null) {
            long ts = item.getDateSaved();
            if (ts > 0) {
                h.txtDate.setText(formatRelativeDate(ts));
            } else {
                h.txtDate.setText("");
            }
        }

        // Checkbox
        h.checkBox.setOnCheckedChangeListener(null);
        h.checkBox.setChecked(item.isChecked());

        h.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            if (listener != null) listener.onSelectionChanged();
        });

        // Row click toggles checkbox
        h.itemView.setOnClickListener(v ->
                h.checkBox.setChecked(!h.checkBox.isChecked())
        );
    }

    @Override
    public int getItemCount() {
        return filteredList != null ? filteredList.size() : 0;
    }

    public List<GroceryItem> getCurrentList() {
        return filteredList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        CheckBox checkBox;
        ImageView imgIcon;
        TextView txtDate; // date label

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            checkBox = itemView.findViewById(R.id.checkboxItem);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }

    // update adapter data from activity
    public void updateData(List<GroceryItem> newItems) {
        originalList.clear();
        if (newItems != null) {
            originalList.addAll(newItems);
        }

        filteredList.clear();
        filteredList.addAll(originalList);

        notifyDataSetChanged();
        if (listener != null) listener.onSelectionChanged();
    }

    // ---- Filterable (search) ----
    @Override
    public Filter getFilter() {
        return groceryFilter;
    }

    private final Filter groceryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<GroceryItem> filtered = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(originalList);
            } else {
                String pattern = constraint.toString().toLowerCase().trim();
                for (GroceryItem item : originalList) {
                    if (item.getName() != null &&
                            item.getName().toLowerCase().contains(pattern)) {
                        filtered.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtered;
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList.clear();
            filteredList.addAll((List<GroceryItem>) results.values);
            notifyDataSetChanged();
            if (listener != null) listener.onSelectionChanged();
        }
    };

    // ---- Date formatting: Today / Yesterday / dd MMM yyyy ----
    private String formatRelativeDate(long timeMillis) {
        Calendar saved = Calendar.getInstance();
        saved.setTimeInMillis(timeMillis);

        Calendar now = Calendar.getInstance();

        for (Calendar c : new Calendar[]{saved, now}) {
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }

        long diffDays = (now.getTimeInMillis() - saved.getTimeInMillis())
                / (24L * 60L * 60L * 1000L);

        if (diffDays == 0) {
            return "Today";
        } else if (diffDays == 1) {
            return "Yesterday";
        } else {
            return new SimpleDateFormat("dd MMM yyyy")
                    .format(new Date(timeMillis));
        }
    }
}
