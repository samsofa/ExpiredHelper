package com.samsofa.expiredhelper.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "item_table")
public class Item implements Parcelable {

  @PrimaryKey(autoGenerate = true)
  private int id;


  private String code;

  private String supplier;

  @ColumnInfo(name = "expire_date")
  private long expireDate;


  public Item(String code, String supplier, long expireDate) {
    this.code = code;
    this.supplier = supplier;
    this.expireDate = expireDate;
  }

  public static final Creator<Item> CREATOR = new Creator<Item>() {
    @Override
    public Item createFromParcel(Parcel in) {
      return new Item(in);
    }

    @Override
    public Item[] newArray(int size) {
      return new Item[size];
    }
  };

  protected Item(Parcel in) {
    id = in.readInt();
    code = in.readString();
    supplier = in.readString();
    expireDate = in.readLong();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeString(code);
    dest.writeString(supplier);
    dest.writeLong(expireDate);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public int getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getSupplier() {
    return supplier;
  }

  public long getExpireDate() {
    return expireDate;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Item)) {
      return false;
    }
    Item item = (Item) o;
    return getId() == item.getId() &&
        getExpireDate() == item.getExpireDate() &&
        getCode().equals(item.getCode()) &&
        Objects.equals(getSupplier(), item.getSupplier());
  }


}
