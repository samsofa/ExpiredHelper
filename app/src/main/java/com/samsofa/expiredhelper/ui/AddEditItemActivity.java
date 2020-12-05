package com.samsofa.expiredhelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import com.google.android.material.textfield.TextInputLayout;
import com.samsofa.expiredhelper.R;
import com.samsofa.expiredhelper.models.Item;
import com.samsofa.expiredhelper.viewModels.ItemViewModel;


public class AddEditItemActivity extends AppCompatActivity {

  private static final String TAG = "AddEditItemActivity";

  TextInputLayout codeTextInputLayout, supplierTextInputLayout, expireTextInputLayout;
  EditText codeEditText, expireDateEditText;

  String codeString, expireCode;

  ItemViewModel itemViewModel;

  private Spinner mSupplierSpinner;
  private String mSupplier = "unknown supplier";

  private int itemId = -1;

  // private Intent intent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit_item);

    viewInit();
    setupSpinner();

    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);

    Intent intent = getIntent();

    if (intent.hasExtra(Constants.EXTRA_ITEM_ID)) {
      setTitle("Edit Item");
      codeEditText.setText(intent.getStringExtra(Constants.EXTRA_ITEM_CODE));
      expireDateEditText.setText(intent.getStringExtra(Constants.EXTRA_CODE_EXPIRE));
      mSupplier = intent.getStringExtra(Constants.EXTRA_CODE_SUPPLIER);
      itemId = intent.getIntExtra(Constants.EXTRA_ITEM_ID, -1);
      addValueToSpinner();

    } else {
      setTitle("Add Item");
      // Invalidate the options menu, so the "Delete" menu option can be hidden.
      // (It doesn't make sense to delete an item that hasn't been created yet.)

      invalidateOptionsMenu();
    }

    itemViewModel = new ViewModelProvider(this,
        AndroidViewModelFactory.getInstance(this.getApplication()))
        .get(ItemViewModel.class);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.save_menu, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
//    super.onPrepareOptionsMenu(menu);
    if (itemId == -1) {
      MenuItem menuItem = menu.findItem(R.id.action_delete);
      menuItem.setVisible(false);
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save:
        saveItem();
        return true;

      case R.id.action_delete:
        delete();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }

  }


  private void saveItem() {

    extractValueFromEditText();

    Item newEditItem = new Item(codeString, mSupplier, Long.parseLong(expireCode));

    if (validation(codeTextInputLayout, codeString)) {
      if (itemId != -1) {
        newEditItem.setId(itemId);
        itemViewModel.update(newEditItem);
        Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
      } else {
        itemViewModel.insert(newEditItem);
        Toast.makeText(this, "Item inserted", Toast.LENGTH_SHORT).show();
      }
      finish();
    }

    //todo  AlertDialog to ask would you finish layout or contonui adding more items
  }


  private void delete() {
    Log.i(TAG, "delete: delete");


  }


  private void extractValueFromEditText() {
    codeString = codeEditText.getText().toString();
    expireCode = expireDateEditText.getText().toString();

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
          } else if (selection.equals(getString(R.string.supplier_sameh))) {
            mSupplier = Constants.SUPPLIER_SAMEH; //sameh
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

  private void addValueToSpinner() {
    switch (mSupplier) {
      case Constants.SUPPLIER_IDF:
        mSupplierSpinner.setSelection(1);
        break;
      case Constants.SUPPLIER_RITTER:
        mSupplierSpinner.setSelection(2);
        break;
      case Constants.SUPPLIER_OUVO:
        mSupplierSpinner.setSelection(3);
        break;
      case Constants.SUPPLIER_AYMAN:
        mSupplierSpinner.setSelection(4);
        break;
      case Constants.SUPPLIER_SAMEH:
        mSupplierSpinner.setSelection(5);
        break;
      default:
        mSupplierSpinner.setSelection(0);
        break;
    }
  }
}