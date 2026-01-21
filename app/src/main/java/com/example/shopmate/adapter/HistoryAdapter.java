package com.example.shopmate.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopmate.R;
import com.example.shopmate.model.HistoryDay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.DayViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(HistoryDay day);
    }

    public interface OnDeleteClickListener {
        void onDelete(String listName);
    }

    public interface OnEditClickListener {
        void onEdit(String oldName, String newName);
    }

    private List<HistoryDay> days;
    private OnDayClickListener listener;
    private OnDeleteClickListener deleteListener;
    private OnEditClickListener editListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

    public HistoryAdapter(List<HistoryDay> days, OnDayClickListener listener, OnDeleteClickListener deleteListener, OnEditClickListener editListener) {
        this.days = days;
        this.listener = listener;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_record, parent, false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        HistoryDay day = days.get(position);

        holder.txtTitle.setText(day.getTitle());
        holder.txtDate.setText(dateFormat.format(new Date(day.getDayStartMillis())));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDayClick(day);
        });

        holder.btnEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle("Edit List Name");

            final EditText input = new EditText(holder.itemView.getContext());
            input.setText(day.getTitle());
            builder.setView(input);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    if (editListener != null) {
                        editListener.onEdit(day.getTitle(), newName);
                    }
                    day.setTitle(newName);
                    notifyItemChanged(position);
                    Toast.makeText(holder.itemView.getContext(), "List name updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(day.getTitle());
            }
        });
    }

    @Override
    public int getItemCount() { return days.size(); }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDate, btnEdit, btnDelete;
        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtHistoryTitle);
            txtDate = itemView.findViewById(R.id.txtHistoryDate);
            btnEdit = itemView.findViewById(R.id.btnEditRecord);
            btnDelete = itemView.findViewById(R.id.btnDeleteRecord);
        }
    }
}
