package com.samsofa.expiredhelper.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.samsofa.expiredhelper.R;
import com.samsofa.expiredhelper.models.Item;
import com.samsofa.expiredhelper.viewModels.ItemViewModel;


public class AddEditItemActivity extends AppCompatActivity {

  EditText codeEditText, supplierEditText, expireDateEditText;

  String code, StringExpire, supplier;

  ItemViewModel itemViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_item);

    codeEditText = findViewById(R.id.et_code_name);
    supplierEditText = findViewById(R.id.et_supplier);
    expireDateEditText = findViewById(R.id.et_expire);

    itemViewModel = new ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
        .get(ItemViewModel.class);


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.save_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save:
        saveItem();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }

  }

  private void saveItem() {

    getTextFromEditText();
    Item newItem = new Item(code, supplier, Long.parseLong(StringExpire));
    itemViewModel.insert(newItem);
  }


  private void getTextFromEditText() {
    code = codeEditText.getText().toString();
    supplier = supplierEditText.getText().toString();
    StringExpire = expireDateEditText.getText().toString();
  }
}