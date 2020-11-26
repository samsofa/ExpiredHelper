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

  private LiveData<List<Item>> allItem;

  public ItemRepository(Application application) {

    ItemDatabase itemDatabase = ItemDatabase.getInstance(application);

    itemDao = itemDatabase.itemDao();

    allItem = itemDao.getAllItems();
  }

  public void insert(Item item){
    
  }

  public void update(Item item){
    
  }

  public void delete(Item item){

  }
}
