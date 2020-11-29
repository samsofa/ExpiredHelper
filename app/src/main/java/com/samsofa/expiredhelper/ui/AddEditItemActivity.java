package com.samsofa.expiredhelper.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputLayout;
import com.samsofa.expiredhelper.R;
import com.samsofa.expiredhelper.models.Item;
import com.samsofa.expiredhelper.viewModels.ItemViewModel;


public class AddEditItemActivity extends AppCompatActivity {

  TextInputLayout codeTextInputLayout, supplierTextInputLayout, expireTextInputLayout;
  EditText codeEditText, supplierEditText, expireDateEditText;

  String codeString, StringExpire, supplierString;

  ItemViewModel itemViewModel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_item);

    viewInit();

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

      case R.id.action_delete:

        return true;

      default:
        return super.onOptionsItemSelected(item);
    }

  }


  private void saveItem() {

    getTextFromEditText();
    Item newItem = new Item(codeString, supplierString, Long.parseLong(StringExpire));
    itemViewModel.insert(newItem);
  }


  private void getTextFromEditText() {
    codeString = codeEditText.getText().toString();
    supplierString = supplierEditText.getText().toString();
    StringExpire = expireDateEditText.getText().toString();

  }

  private void viewInit() {
    //EditText
    codeEditText = findViewById(R.id.et_code_name);
    supplierEditText = findViewById(R.id.et_supplier);
    expireDateEditText = findViewById(R.id.et_expire);
    //textInputLayout
    codeTextInputLayout = findViewById(R.id.code_textInputLayout);
    supplierTextInputLayout = findViewById(R.id.supplier_textInputLayout);
    expireTextInputLayout = findViewById(R.id.expire_textInputLayout);

  }

  private void validation() {
    if (codeString.length() != 6) {
      codeTextInputLayout.setError("fill code text with 6 numbers");
      return;
    }

  }
}