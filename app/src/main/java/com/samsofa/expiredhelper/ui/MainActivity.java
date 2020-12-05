package com.samsofa.expiredhelper.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.samsofa.expiredhelper.R;
import com.samsofa.expiredhelper.adapters.ItemAdapter;
import com.samsofa.expiredhelper.interfaces.RecyclerViewClickListener;
import com.samsofa.expiredhelper.models.Item;
import com.samsofa.expiredhelper.viewModels.ItemViewModel;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

  private ItemViewModel itemViewModel;

  private RelativeLayout emptyView;

  private ItemAdapter adapter;

  Toolbar toolbar;

  private RecyclerView mRecyclerView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    emptyView = findViewById(R.id.empty_view);
    emptyView.setVisibility(View.VISIBLE);
    itemViewModel = new ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
        .get(ItemViewModel.class);

    adapter = new ItemAdapter(this, MainActivity.this);
    mRecyclerView = findViewById(R.id.main_recylerView);
    mRecyclerView
        .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    mRecyclerView.setHasFixedSize(true);

    itemViewModel.getAllItems().observe(this, new Observer<List<Item>>() {
      @Override
      public void onChanged(List<Item> items) {
        adapter.submitList(items);
        dealEmptyViewVisibility(items);
      }
    });

    mRecyclerView.setAdapter(adapter);

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
        startActivity(intent);
      }
    });

    new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

      @Override
      public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder,
          @NonNull ViewHolder target) {
        return false;
      }

      @Override
      public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
          @NonNull ViewHolder viewHolder, float dX, float dY, int actionState,
          boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, mRecyclerView, viewHolder, dX, dY, actionState,
            isCurrentlyActive)
            .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.gray))
            .addActionIcon(R.drawable.ic_delete)
            .addSwipeLeftLabel("Delete")
            .setSwipeLeftLabelColor(R.color.red)
            .setSwipeLeftActionIconTint(R.color.red)
            //
            //.setSwipeLeftLabelTypeface()
            //      .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f)
            .create()
            .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
      }

      @Override
      public void onSwiped(@NonNull ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
          itemViewModel.delete(adapter.getItemAt(viewHolder.getAdapterPosition()));
          Toast.makeText(MainActivity.this,
              "item at position deleted: " + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT)
              .show();
        }
      }
    }).attachToRecyclerView(mRecyclerView);

  }

  @Override
  public void onItemClick(int position) {
    Item currentItem = adapter.getItemAt(position);
    Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);

    intent.putExtra(Constants.EXTRA_ITEM_ID, currentItem.getId());
    intent.putExtra(Constants.EXTRA_ITEM_CODE, currentItem.getCode());
    intent.putExtra(Constants.EXTRA_CODE_SUPPLIER, currentItem.getSupplier());
    intent.putExtra(Constants.EXTRA_CODE_EXPIRE, String.valueOf(currentItem.getExpireDate()));

    startActivity(intent);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.action_delete_all_items) {
      if (Objects.requireNonNull(itemViewModel.getAllItems().getValue()).size() > 0) {
        itemViewModel.deleteAllItems();
        Toast.makeText(this, "all Items deleted", Toast.LENGTH_SHORT).show();
        return true;
      } else {
        Toast.makeText(this, "no Items found", Toast.LENGTH_SHORT).show();
      }
    }
    return super.onOptionsItemSelected(item);
  }

  private void dealEmptyViewVisibility(List<Item> items) {
    if (items.size() > 0) {
      mRecyclerView.setVisibility(View.VISIBLE);
      emptyView.setVisibility(View.GONE);
    } else {
      mRecyclerView.setVisibility(View.GONE);
      emptyView.setVisibility(View.VISIBLE);
    }
  }


}
