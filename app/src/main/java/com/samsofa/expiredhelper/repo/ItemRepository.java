package com.samsofa.expiredhelper.repo;

import android.app.Application;
import android.provider.ContactsContract.CommonDataKinds.Note;
import androidx.lifecycle.LiveData;
import com.samsofa.expiredhelper.models.Item;
import com.samsofa.expiredhelper.room.ItemDao;
import com.samsofa.expiredhelper.room.ItemDatabase;
import java.util.List;

public class ItemRepository {

  private ItemDao itemDao;

  private LiveData<List<Item>> allItems;

  public ItemRepository(Application application) {

    ItemDatabase itemDatabase = ItemDatabase.getInstance(application);

    itemDao = itemDatabase.itemDao();

    allItems = itemDao.getAllItems();
  }

  public void insert(Item item){

    itemDao.insert(item);
  }

  public void update(Item item){

    itemDao.update(item);
    
  }

  public void delete(Item item){

    itemDao.delete(item);
  }

  public LiveData<List<Item>> getAllItems(){
    return allItems;
  }
}
