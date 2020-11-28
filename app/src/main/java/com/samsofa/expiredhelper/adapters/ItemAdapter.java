package com.samsofa.expiredhelper.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.samsofa.expiredhelper.R;
import com.samsofa.expiredhelper.models.Item;

public class ItemAdapter extends ListAdapter<Item, ItemAdapter.ItemHolder> {


  private static final DiffUtil.ItemCallback<Item> DIFF_CALLBACK = new ItemCallback<Item>() {
    @Override
    public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
      return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
      return oldItem.getCode().equals(newItem.getCode()) &&
          oldItem.getSupplier().equals(newItem.getSupplier()) &&
          oldItem.getExpireDate() == (newItem.getExpireDate());
    }
  };

  public ItemAdapter() {
    super(DIFF_CALLBACK);
  }

  @NonNull
  @Override
  public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_row, parent, false);
    return new ItemHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull ItemHolder holder, int position) {

    Item currentItem = getItem(position);
    holder.codeNameTextView.setText(currentItem.getCode());
    holder.codeSupplierTextView.setText(currentItem.getSupplier());
    holder.codeDateTextView.setText(String.valueOf(currentItem.getExpireDate()));
  }

  public Item getItemsAt(int position) {
    return getItem(position);
  }

  public class ItemHolder extends RecyclerView.ViewHolder {

    TextView codeNameTextView, codeDateTextView, codeSupplierTextView;

    public ItemHolder(@NonNull View itemView) {
      super(itemView);

      codeNameTextView = itemView.findViewById(R.id.txv_item_name);
      codeDateTextView = itemView.findViewById(R.id.txv_item_date);
      codeSupplierTextView = itemView.findViewById(R.id.txv_item_supplier);
    }
  }
}
