package com.samsofa.expiredhelper.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.samsofa.expiredhelper.models.Item;
import java.util.List;

@Dao
public interface ItemDao {

  @Insert
  void insert(Item item);

  @Update
  void update(Item item);

  @Delete
  void delete(Item item);

  @Query("DELETE FROM item_table")
  void deleteAll();

  @Query("SELECT * FROM item_table ORDER BY expire_date")
  LiveData<List<Item>> getAllItems();

  @Query("SELECT * FROM item_table where supplier = :supplier ORDER BY expire_date")
  LiveData<List<Item>> queryBySupplier(String supplier);


}
