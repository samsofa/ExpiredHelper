package com.samsofa.expiredhelper.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
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


public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

  private ItemViewModel itemViewModel;

  private RelativeLayout emptyView;

  private ItemAdapter adapter;

  private RecyclerView mRecyclerView;
  ItemTouchHelper.SimpleCallback simpleCallback = new SimpleCallback(0, ItemTouchHelper.LEFT) {
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
          .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
          .addActionIcon(R.drawable.ic_delete)
          .addSwipeLeftLabel("Delete")
          .setSwipeLeftLabelColor(R.color.black)
          .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f)
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
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    emptyView = findViewById(R.id.empty_view);

    adapter = new ItemAdapter(this);
    mRecyclerView = findViewById(R.id.main_recylerView);
    mRecyclerView
        .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    mRecyclerView.setAdapter(adapter);

    itemViewModel = new ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
        .get(ItemViewModel.class);

    itemViewModel.getAllItems().observe(this, new Observer<List<Item>>() {
      @Override
      public void onChanged(List<Item> items) {
        adapter.submitList(items);

        if (adapter.getCurrentList().size() > 0) {
          emptyView.setVisibility(View.GONE);
        }
      }
    });

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);
        startActivity(intent);
      }
    });

    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
    itemTouchHelper.attachToRecyclerView(mRecyclerView);

//    new ItemTouchHelper(new SimpleCallback(0, ItemTouchHelper.LEFT) {
//      @Override
//      public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder,
//          @NonNull ViewHolder target) {
//        return false;
//      }
//
//      @Override
//      public void onSwiped(@NonNull ViewHolder viewHolder, int direction) {
//
//        itemViewModel.delete(adapter.getItemsAt(viewHolder.getAdapterPosition()));
//        Toast.makeText(MainActivity.this, "Item deleted " + viewHolder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
//      }
////
////      @Override
////      public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
////          @NonNull ViewHolder viewHolder, float dX, float dY, int actionState,
////          boolean isCurrentlyActive) {
////        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
////            .addSwipeLeftActionIcon(R.drawable.ic_delete)
////            .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
////            .addSwipeLeftLabel("Delete")
////            .create()
////            .decorate();
////        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
////      }
//    }).attachToRecyclerView(mRecyclerView);
//

  }

  @Override
  public void onItemClick(int position) {
    Item currentItem = adapter.getItemAt(position);
    Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);

    intent.putExtra("code", currentItem.getCode());
    intent.putExtra("supplier", currentItem.getSupplier());
    intent.putExtra("expired_date", currentItem.getExpireDate());
    intent.putExtra("id", currentItem.getId());

    startActivity(intent);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.delete_all_items:
        itemViewModel.deleteAllItems();
        Toast.makeText(this, "all Items deleted", Toast.LENGTH_SHORT).show();
    }
    return super.onOptionsItemSelected(item);
  }
}
