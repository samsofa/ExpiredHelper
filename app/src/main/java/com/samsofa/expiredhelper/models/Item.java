package com.samsofa.expiredhelper.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Objects;

@Entity(tableName = "item_table")
public class Item {


  @PrimaryKey(autoGenerate = true)
  private int id;


  private final String code;


  private final String supplier;

  @ColumnInfo(name = "expire_date")
  private final long expireDate;


  public Item(String code, String supplier, long expireDate) {
    this.code = code;
    this.supplier = supplier;
    this.expireDate = expireDate;
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
