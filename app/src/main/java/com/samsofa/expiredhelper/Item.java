package com.samsofa.expiredhelper;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "code_table")
public class Item {

  @PrimaryKey(autoGenerate = true)
  private int id;


  private final String code;


  private final String supplier;


  private final long expire_date;


  public Item(String code, String supplier, long expire_date) {
    this.code = code;
    this.supplier = supplier;
    this.expire_date = expire_date;
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

  public long getExpire_date() {
    return expire_date;
  }
}
