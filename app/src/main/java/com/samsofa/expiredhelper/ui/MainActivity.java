package com.samsofa.expiredhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.samsofa.expiredhelper.R;
import com.samsofa.expiredhelper.adapters.ItemAdapter;
import com.samsofa.expiredhelper.interfaces.RecyclerViewClickListener;
import com.samsofa.expiredhelper.models.Item;
import com.samsofa.expiredhelper.viewModels.ItemViewModel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

  private ItemViewModel itemViewModel;
  private List<Item> displayItems;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    displayItems = new ArrayList<>();
    ItemAdapter adapter = new ItemAdapter(this);
    RecyclerView recyclerView = findViewById(R.id.main_recylerView);
    recyclerView
        .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    recyclerView.setAdapter(adapter);

    itemViewModel = new ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
        .get(ItemViewModel.class);

    itemViewModel.getAllItems().observe(this, new Observer<List<Item>>() {
      @Override
      public void onChanged(List<Item> items) {
        adapter.submitList(items);
        displayItems.addAll(items);
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
  }


  @Override
  public void onItemClick(int position) {
    Toast.makeText(this, "position " + position, Toast.LENGTH_SHORT).show();
  }
}
