package com.example.shopmate.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shopmate.R;
import com.example.shopmate.model.GroceryItem;

import java.text.DecimalFormat;
import java.util.List;

public class HistoryProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM   = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    public enum Currency {
        USD, KHR
    }

    public interface OnDeleteClickListener {
        void onDelete(GroceryItem item);
    }

    private final List<GroceryItem> items;
    private final OnDeleteClickListener deleteListener;
    final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    private Currency selectedCurrency = Currency.USD;     // default one
    private static final double USD_TO_KHR = 4100.0;      // example rate

    public HistoryProductAdapter(List<GroceryItem> items,
                                 OnDeleteClickListener deleteListener) {
        this.items = items;
        this.deleteListener = deleteListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() - 1 ? VIEW_TYPE_FOOTER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_FOOTER) {
            View v = inflater.inflate(R.layout.layout_total_amount, parent, false);
            return new FooterVH(v);
        } else {
            View v = inflater.inflate(R.layout.item_history_product_row, parent, false);
            return new ProductVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterVH) {
            FooterVH f = (FooterVH) holder;
            double totalUsd = calculateTotal();

            // show based on selected currency
            if (selectedCurrency == Currency.USD) {
                f.tvTotalAmount.setText("Total: $" + decimalFormat.format(totalUsd));
                f.ivUsd.setAlpha(1.0f);
                f.ivKhr.setAlpha(0.4f);
            } else {
                double totalKhr = totalUsd * USD_TO_KHR;
                f.tvTotalAmount.setText("Total: ៛" + decimalFormat.format(totalKhr));
                f.ivUsd.setAlpha(0.4f);
                f.ivKhr.setAlpha(1.0f);
            }

            // change currency when clicking icons
            f.ivUsd.setOnClickListener(v -> {
                if (selectedCurrency != Currency.USD) {
                    selectedCurrency = Currency.USD;
                    notifyItemChanged(getItemCount() - 1);
                }
            });

            f.ivKhr.setOnClickListener(v -> {
                if (selectedCurrency != Currency.KHR) {
                    selectedCurrency = Currency.KHR;
                    notifyItemChanged(getItemCount() - 1);
                }
            });

        } else if (holder instanceof ProductVH) {
            ProductVH h = (ProductVH) holder;
            GroceryItem item = items.get(position);

            // detach old watcher
            h.edtPrice.removeTextChangedListener(h.priceWatcher);

            h.txtName.setText(item.getName());
            h.txtQty.setText(String.valueOf(item.getQuantity()));
            h.edtPrice.setText(decimalFormat.format(item.getPrice()));
            h.edtPrice.setSelection(h.edtPrice.getText().length());

            // bind watcher
            h.priceWatcher.bind(item, this);
            h.edtPrice.addTextChangedListener(h.priceWatcher);

            // === LOAD LOCAL DRAWABLE BASED ON item.getImage() (NULL-SAFE) ===
            Context context = h.itemView.getContext();
            String imageName = item.getImage(); // may be null
            int resId = 0;

            if (imageName != null && !imageName.isEmpty()) {
                resId = context.getResources()
                        .getIdentifier(imageName, "drawable", context.getPackageName());
            }

            if (resId != 0) {
                Glide.with(context)
                        .load(resId)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(h.imgProduct);
            } else {
                // fallback if drawable not found or imageName is null/empty
                h.imgProduct.setImageResource(R.drawable.ic_launcher_foreground);
            }

            // quantity + / -
            h.btnPlus.setOnClickListener(v -> {
                int pos = h.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && pos < items.size()) {
                    GroceryItem current = items.get(pos);
                    current.setQuantity(current.getQuantity() + 1);
                    notifyItemChanged(pos);
                    notifyItemChanged(getItemCount() - 1); // update footer total
                }
            });

            h.btnMinus.setOnClickListener(v -> {
                int pos = h.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && pos < items.size()) {
                    GroceryItem current = items.get(pos);
                    int newQty = current.getQuantity() - 1;
                    if (newQty <= 0) {
                        GroceryItem deleted = items.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, items.size());
                        notifyItemChanged(getItemCount() - 1);
                        if (deleteListener != null) deleteListener.onDelete(deleted);
                    } else {
                        current.setQuantity(newQty);
                        notifyItemChanged(pos);
                        notifyItemChanged(getItemCount() - 1);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (items == null ? 0 : items.size()) + 1; // +1 for footer
    }

    private double calculateTotal() {
        double total = 0.0;
        if (items != null) {
            for (GroceryItem item : items) {
                total += item.getQuantity() * item.getPrice();
            }
        }
        return total;
    }

    static class ProductVH extends RecyclerView.ViewHolder {
        TextView txtName, txtQty, btnPlus, btnMinus;
        ImageView imgProduct;
        EditText edtPrice;
        final PriceTextWatcher priceWatcher;

        ProductVH(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtQty = itemView.findViewById(R.id.txtProductQty);
            imgProduct = itemView.findViewById(R.id.imgIcon);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            edtPrice = itemView.findViewById(R.id.edtPrice);
            priceWatcher = new PriceTextWatcher();
        }
    }

    static class FooterVH extends RecyclerView.ViewHolder {
        TextView tvTotalAmount;
        ImageView ivUsd, ivKhr;

        FooterVH(@NonNull View itemView) {
            super(itemView);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            ivUsd = itemView.findViewById(R.id.ivUsd);
            ivKhr = itemView.findViewById(R.id.ivKhr);
        }
    }

    private static class PriceTextWatcher implements TextWatcher {
        private GroceryItem item;
        private HistoryProductAdapter adapter;
        private boolean updating;

        void bind(GroceryItem item, HistoryProductAdapter adapter) {
            this.item = item;
            this.adapter = adapter;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (updating || item == null || adapter == null) return;

            updating = true;
            try {
                String raw = s.toString().replaceAll("[^\\d.]", "");
                double price = raw.isEmpty() ? 0.0 : Double.parseDouble(raw);
                item.setPrice(price);
                adapter.notifyItemChanged(adapter.getItemCount() - 1); // update footer
            } catch (NumberFormatException ignored) {
                item.setPrice(0.0);
                adapter.notifyItemChanged(adapter.getItemCount() - 1);
            } finally {
                updating = false;
            }
        }
    }
}
