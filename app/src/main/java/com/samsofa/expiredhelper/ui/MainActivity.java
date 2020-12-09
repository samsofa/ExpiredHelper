package com.samsofa.expiredhelper.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.samsofa.expiredhelper.R;
import com.samsofa.expiredhelper.adapters.ItemAdapter;
import com.samsofa.expiredhelper.interfaces.RecyclerViewClickListener;
import com.samsofa.expiredhelper.models.Item;
import com.samsofa.expiredhelper.viewModels.ItemViewModel;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator.Builder;
import java.util.List;


public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

  private ItemViewModel itemViewModel;

  private RelativeLayout emptyView;

  private ItemAdapter adapter;


  private RecyclerView mRecyclerView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = findViewById(R.id.toolbar);
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

    new ItemTouchHelper(new SimpleCallback(0, ItemTouchHelper.LEFT) {

      @Override
      public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder,
          @NonNull ViewHolder target) {
        return false;
      }

      @Override
      public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
          @NonNull ViewHolder viewHolder, float dX, float dY, int actionState,
          boolean isCurrentlyActive) {
        new Builder(c, mRecyclerView, viewHolder, dX, dY, actionState,
            isCurrentlyActive)
            .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.gray))
            .addActionIcon(R.drawable.ic_delete)
            .addSwipeLeftLabel("Delete")

            //
            //.setSwipeLeftLabelTypeface()
            //      .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f)
            .create()
            .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
      }

      @Override
      public void onSwiped(@NonNull ViewHolder viewHolder, int direction) {

        Item deletedItem = adapter.getItemAt(viewHolder.getAdapterPosition());
        if (direction == ItemTouchHelper.LEFT) {

          itemViewModel.delete(deletedItem);

          createUndoDeleteItemSnackBar(deletedItem);
        }
      }
    }).attachToRecyclerView(mRecyclerView);

  }

  @Override
  public void onItemClick(int position) {
    Item currentItem = adapter.getItemAt(position);
    Intent intent = new Intent(MainActivity.this, AddEditItemActivity.class);

//    intent.putExtra(Constants.EXTRA_ITEM_ID, currentItem.getId());
//    intent.putExtra(Constants.EXTRA_ITEM_CODE, currentItem.getCode());
//    intent.putExtra(Constants.EXTRA_CODE_SUPPLIER, currentItem.getSupplier());
//    intent.putExtra(Constants.EXTRA_CODE_EXPIRE, String.valueOf(currentItem.getExpireDate()));

    intent.putExtra("selected_item", currentItem);

    startActivity(intent);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);

    return true;
  }


  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_delete_all_items:
        if (itemViewModel.getAllItems().getValue().size() > 1) {
          showAllDeleteConfirmationDialog();
          return true;
        }

        Toast.makeText(this, "no items to delete", Toast.LENGTH_SHORT).show();

      default:
        return super.onOptionsItemSelected(item);
    }

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


  private void createUndoDeleteItemSnackBar(Item item) {
    Snackbar.make(mRecyclerView,
        "you want to delete Code " + item.getCode(),
        Snackbar.LENGTH_LONG)
        .setAction("Undo", new OnClickListener() {
          @Override
          public void onClick(View v) {
            itemViewModel.insert(item);
          }
        }).show();

  }


  private void showAllDeleteConfirmationDialog() {

    // Create an AlertDialog.Builder and set the message, and click listeners
    // for the positive and negative buttons on the dialog.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Do you want to delete all items? \n\" you will lose all Data\"");
    //to add icon you have to add title for alert dialog
    builder.setTitle("Be careful");
    builder.setIcon(R.drawable.ic_alert);
    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        // User clicked the "Delete" button, so delete the items.
        deleteAllItems();

      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        // User clicked the "Cancel" button, so dismiss the dialog
        // and continue editing the pet.
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    builder.setCancelable(true);

    // Create and show the AlertDialog
    AlertDialog alertDialog = builder.create();
    alertDialog.show();
  }

  private void deleteAllItems() {

    itemViewModel.deleteAllItems();
    Toast.makeText(this, "all Items deleted", Toast.LENGTH_SHORT).show();
  }
}
