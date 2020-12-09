package com.samsofa.expiredhelper.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.ItemCallback;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.samsofa.expiredhelper.R;
import com.samsofa.expiredhelper.interfaces.RecyclerViewClickListener;
import com.samsofa.expiredhelper.models.Item;
import java.util.Calendar;

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
  private final RecyclerViewClickListener listener;
  private final Context context;


  public ItemAdapter(RecyclerViewClickListener listener, Context context) {
    super(DIFF_CALLBACK);
    this.listener = listener;
    this.context = context;
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

    holder.container
        .setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
    holder.codeDateTextView
        .setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
    holder.codeNameTextView.setText(currentItem.getCode());
    holder.codeSupplierTextView.setText(currentItem.getSupplier());
    holder.codeDateTextView
        .setText(String.valueOf(extractDayFromMilli(currentItem.getExpireDate())));

    // Set the proper background color on the month circle.
    // Fetch the background from the TextView, which is a GradientDrawable.
    GradientDrawable expireDateCircle = (GradientDrawable) holder.codeDateTextView.getBackground();

    // Get the appropriate background color based on the current expired day

    int dayColor = getExpireDayColor(extractDayFromMilli(currentItem.getExpireDate()));
    // Set the color on the day circle
    expireDateCircle.setColor(dayColor);

  }

  public Item getItemAt(int position) {
    return getItem(position);
  }

  private int extractDayFromMilli(long expiredDate) {
    long expirePeriodInDay = 0;

    if (expiredDate != 0) {
      Calendar cal = Calendar.getInstance();
      long currentDate = cal.getTimeInMillis();

      long expiredDateLong = Long.valueOf(expiredDate);
      long expiredPeriod = expiredDateLong - currentDate;

      expirePeriodInDay =
          expiredPeriod / (1000 * 60 * 60 * 24); //remain period after extract month (%)
    } else {
      expirePeriodInDay = 0L;
    }
    //return (int) (remainPeriod / (1000 * 60 * 60 * 24)); //millisecond in the day
    return (int) expirePeriodInDay;
  }

  private int getExpireDayColor(int day) {

    int dayColorResourceId;

    int cognitiveDay = day / 30;
    switch (cognitiveDay) {
      case 0:
        dayColorResourceId = R.color.day5;
        break;
      case 1:
        dayColorResourceId = R.color.day15;
        break;
      case 2:
        dayColorResourceId = R.color.day30;
        break;
      default:
        dayColorResourceId = R.color.day60plus;
        break;
    }
    return ContextCompat.getColor(context, dayColorResourceId);
  }

  public class ItemHolder extends RecyclerView.ViewHolder {

    TextView codeNameTextView, codeDateTextView, codeSupplierTextView;

    CardView container;

    public ItemHolder(@NonNull View itemView) {
      super(itemView);

      container = itemView.findViewById(R.id.container);
      codeNameTextView = itemView.findViewById(R.id.txv_item_name);
      codeDateTextView = itemView.findViewById(R.id.txv_item_date);
      codeSupplierTextView = itemView.findViewById(R.id.txv_item_supplier);

      itemView.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          int position = getAdapterPosition();
          if (listener != null && position != RecyclerView.NO_POSITION) {
            listener.onItemClick(position);
          }
        }
      });


    }
  }

}
