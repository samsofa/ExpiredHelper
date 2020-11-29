package com.samsofa.expiredhelper.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

  String codeString, StringExpire;

  ItemViewModel itemViewModel;

  private Spinner mSupplierSpinner;
  private String mSupplier = "unknown supplier";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_item);

    viewInit();
    setupSpinner();

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

    if (validation(codeTextInputLayout, codeString)) {
      Item newItem = new Item(codeString, mSupplier, Long.parseLong(StringExpire));
      itemViewModel.insert(newItem);
      finish();
      //todo  AlertDialog to ask would you finish layout or contonui adding more items
    }
  }


  private void getTextFromEditText() {
    codeString = codeEditText.getText().toString();
    StringExpire = expireDateEditText.getText().toString();

  }

  private void viewInit() {
    //EditText
    codeEditText = findViewById(R.id.et_code_name);
    mSupplierSpinner = findViewById(R.id.spinner_supplier);
    expireDateEditText = findViewById(R.id.et_expire);
    //textInputLayout
    codeTextInputLayout = findViewById(R.id.code_textInputLayout);
    supplierTextInputLayout = findViewById(R.id.supplier_textInputLayout);
    expireTextInputLayout = findViewById(R.id.expire_textInputLayout);

  }

  private boolean validation(TextInputLayout inputLayout, String textString) {
    if (textString.length() != 6) {
      inputLayout.setError("fill code text with 6 numbers");
      return false;
    } else {
      return true;
    }

  }

  private void setupSpinner() {
    ArrayAdapter supplierSpinnerAdapter = ArrayAdapter
        .createFromResource(this, R.array.array_supplier_option,
            android.R.layout.simple_spinner_item);

    // Specify dropdown layout style - simple list view with 1 item per line
    supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
    // Apply the adapter to the spinner
    mSupplierSpinner.setAdapter(supplierSpinnerAdapter);

    // Set the integer mSelected to the constant values
    mSupplierSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = (String) parent.getItemAtPosition(position);
        if (!TextUtils.isEmpty(selection)) {
          if (selection.equals(getString(R.string.supplier_idf))) {
            mSupplier = Constants.SUPPLIER_IDF; // idf
          } else if (selection.equals(getString(R.string.supplier_ritter))) {
            mSupplier = Constants.SUPPLIER_RITTER; // ritter
          } else if (selection.equals(getString(R.string.supplier_ouvo))) {
            mSupplier = Constants.SUPPLIER_OUVO; // ouvo
          } else if (selection.equals(getString(R.string.supplier_ayman))) {
            mSupplier = Constants.SUPPLIER_AYMAN; //ayman
          } else {
            mSupplier = Constants.SUPPLIER_UNKNOWN; // Unknown
          }
        }

      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

        mSupplier = "" + R.string.supplier_unknown;
      }
    });

  }
}