package com.samsofa.expiredhelper.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import com.google.android.material.textfield.TextInputLayout;
import com.samsofa.expiredhelper.R;
import com.samsofa.expiredhelper.models.Item;
import com.samsofa.expiredhelper.viewModels.ItemViewModel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class AddEditItemActivity extends AppCompatActivity {

  private static final String TAG = "AddEditItemActivity";

  TextInputLayout codeTextInputLayout, expireDateInputLayout;
  EditText codeEditText, expireDateEditText;
  ImageView calendarImage;


  String codeString;
  long expireDateMilliSec;
  ItemViewModel itemViewModel;

  private Spinner mSupplierSpinner;
  private String mSupplier = "unknown supplier";

  private int itemId = -1;

  private DatePickerDialog.OnDateSetListener mOnDateSetListener;

  Item selectedItem;
  /**
   * Boolean flag that keeps track of whether the item has been edited (true) or not (false)
   */
  private boolean mItemHasChange = false;

  /**
   * OnTouchListener that listens for any user touches on a View, implying that they are modifying
   * the view, and we change the mItemHasChanged boolean to true.
   */
  private View.OnTouchListener mOnTouchListener = new OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      mItemHasChange = true;
      return false;
    }
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    setContentView(R.layout.activity_add_edit_item);

    viewInit();
    setupSpinner();

    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

    Intent intent = getIntent();

    itemViewModel = new ViewModelProvider(this,
        AndroidViewModelFactory.getInstance(this.getApplication()))
        .get(ItemViewModel.class);

    codeEditText.setOnTouchListener(mOnTouchListener);
    expireDateEditText.setOnTouchListener(mOnTouchListener);

    expireDateEditText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        displayDatePicker();
      }
    });

    mOnDateSetListener = new OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, dayOfMonth);

        expireDateMilliSec = cal.getTimeInMillis();

        expireDateEditText.setText(getFormattedDate(expireDateMilliSec));
      }
    };
    if (intent.hasExtra("selected_item")) {
      setTitle("Edit Item");
      selectedItem = intent.getParcelableExtra("selected_item");
      codeEditText.setText(selectedItem.getCode());
      expireDateMilliSec = selectedItem.getExpireDate();

      expireDateEditText.setText(getFormattedDate(expireDateMilliSec));
//      Toast.makeText(this, "formatedDate: " + itemViewModel.getFormattedDate(expireDateMilliSec), Toast.LENGTH_SHORT).show();

      mSupplier = selectedItem.getSupplier();
      itemId = selectedItem.getId();
      addValueToSpinner();

    } else {
      setTitle("Add Item");
      // Invalidate the options menu, so the "Delete" menu option can be hidden.
      // (It doesn't make sense to delete an item that hasn't been created yet.)

      invalidateOptionsMenu();
//      Observable.create(new ObservableOnSubscribe<Object>() {
//        @Override
//        public void subscribe(
//            @io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Object> emitter)
//            throws Throwable {
//          codeEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//              emitter.onNext(s);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//
//
//            }
//          });
//        }
//      })  .doOnNext(c -> Log.d(TAG, "onCreate: " + c ))
//          .debounce(3, TimeUnit.SECONDS)
//          .subscribe(s -> Log.d(TAG, "onCreate: " + s));
//
    }


  }

  private void viewInit() {
    //EditText
    codeEditText = findViewById(R.id.et_code_name);
    expireDateEditText = findViewById(R.id.et_expire_date);
    mSupplierSpinner = findViewById(R.id.spinner_supplier);

    calendarImage = findViewById(R.id.imgv_calendar);
    expireDateInputLayout = findViewById(R.id.til_expire_date);
    codeTextInputLayout = findViewById(R.id.til_code);

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
        showDeleteConfirmationDialog();
        return true;

      case android.R.id.home:
        // if the pet hasn't changed, continue with navigating up to parent activity
        // which is the {@link CatalogActivity}.
        if (!mItemHasChange) {
          NavUtils.navigateUpFromSameTask(AddEditItemActivity.this);
          return true;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that
        // changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            NavUtils.navigateUpFromSameTask(AddEditItemActivity.this);
          }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }

  }


  private void saveItem() {

    extractValueFromEditText();

    Item newEditItem = new Item(codeString, mSupplier, expireDateMilliSec);

    if (validation(codeTextInputLayout, codeString)) {
      if (itemId != -1) {

        if (mItemHasChange) {
          newEditItem.setId(itemId);
          itemViewModel.update(newEditItem);
          Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }

      } else {
        itemViewModel.insert(newEditItem);
        Toast.makeText(this, "Item inserted", Toast.LENGTH_SHORT).show();
      }
      finish();
    }

    //todo  AlertDialog to ask would you finish layout or contonui adding more items
  }


  private void deleteItem() {
    Log.i(TAG, "delete: delete");
    itemViewModel.delete(selectedItem);
    Toast.makeText(this, "delete code: " + selectedItem.getCode(), Toast.LENGTH_SHORT).show();

  }


  private void extractValueFromEditText() {
    codeString = codeEditText.getText().toString();
    // expireCode = expireDateEditText.getText().toString();

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

  private void showDeleteConfirmationDialog() {

    // Create an AlertDialog.Builder and set the message, and click listeners
    // for the positive and negative buttons on the dialog.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Are you sure to delete this item?");
    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        // User clicked the "Delete" button, so delete the pet.
        deleteItem();
        finish();
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

  /**
   * Show a dialog that warns the user there are unsaved changes that will be lost if they continue
   * leaving the editor.
   *
   * @param discardButtonClickListener is the click listener for what to do when the user confirms
   *                                   they want to discard their changes
   */
  private void showUnsavedChangesDialog(
      DialogInterface.OnClickListener discardButtonClickListener) {

    // Create an AlertDialog.Builder and set the message, and click listeners
    // for the positive and negative buttons on the dialog.
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Discard your change and quit editing?");
    builder.setPositiveButton("Discard", discardButtonClickListener);
    builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // User clicked the "Keep editing" button, so dismiss the dialog
        // and continue editing the item
        if (dialog != null) {
          dialog.dismiss();
        }
      }
    });

    // Create and show the AlertDialog
    AlertDialog alertDialog = builder.create();
    alertDialog.show();

  }

  /**
   * This method is called when the back button is pressed.
   */
  @Override
  public void onBackPressed() {
    if (!mItemHasChange) {
      super.onBackPressed();
      return;
    }

    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
    // Create a click listener to handle the user confirming that changes should be discarded.
    DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // User clicked "Discard" button, close the current activity.
        finish();
      }
    };

    showUnsavedChangesDialog(discardButtonClickListener);
  }


  private void displayDatePicker() {

    Calendar calendar = Calendar.getInstance();

    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    DatePickerDialog dialog = new DatePickerDialog(AddEditItemActivity.this,
        android.R.style.Theme_Holo_Light_Dialog_MinWidth
        , mOnDateSetListener,
        year, month, day);

    //select date for future time not past time
    dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
    dialog.getWindow().setGravity(Gravity.BOTTOM);
    dialog.show();
  }


  private String getFormattedDate(long dateInMillisec) {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");

    return simpleDateFormat.format(dateInMillisec);
  }
}
