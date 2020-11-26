package com.samsofa.expiredhelper.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "item_table")
public class Item {

  @PrimaryKey(autoGenerate = true)
  private int id;


  private final String code;


  private final String supplier; 

  @ColumnInfo(name = "expire_date")
  private final long expireDate;


  public Item(String code, String supplier, long expire_date) {
    this.code = code;
    this.supplier = supplier;
    this.expireDate = expire_date;
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
}
