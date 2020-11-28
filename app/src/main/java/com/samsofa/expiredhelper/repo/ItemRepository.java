package com.samsofa.expiredhelper.repo;

import android.app.Application;
import android.os.AsyncTask;
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

  public void insert(Item item) {

    new InsertNoteAsyncTask(itemDao).execute(item);
  }

  public void update(Item item) {
    new UpdateNoteAsyncTask(itemDao).execute(item);
  }

  public void delete(Item item) {

    new UpdateNoteAsyncTask(itemDao).execute(item);
  }

  public void deleteAll() {

    new DeleteAllNoteAsyncTask(itemDao).execute();
  }


  public LiveData<List<Item>> getAllItems() {
    return allItems;
  }


  private static class InsertNoteAsyncTask extends AsyncTask<Item, Void, Void> {

    private ItemDao itemDao;

    private InsertNoteAsyncTask(ItemDao itemDao) {
      this.itemDao = itemDao;
    }

    @Override
    protected Void doInBackground(Item... items) {
      itemDao.insert(items[0]);
      return null;
    }
  }

  private static class UpdateNoteAsyncTask extends AsyncTask<Item, Void, Void> {

    private ItemDao itemDao;

    private UpdateNoteAsyncTask(ItemDao itemDao) {
      this.itemDao = itemDao;
    }

    @Override
    protected Void doInBackground(Item... items) {
      itemDao.update(items[0]);
      return null;
    }
  }

  private static class DeleteNoteAsyncTask extends AsyncTask<Item, Void, Void> {

    private ItemDao itemDao;

    private DeleteNoteAsyncTask(ItemDao itemDao) {
      this.itemDao = itemDao;
    }

    @Override
    protected Void doInBackground(Item... items) {
      itemDao.delete(items[0]);
      return null;
    }
  }

  private static class DeleteAllNoteAsyncTask extends AsyncTask<Void, Void, Void> {

    private ItemDao itemDao;


    private DeleteAllNoteAsyncTask(ItemDao itemDao) {
      this.itemDao = itemDao;
    }


    @Override
    protected Void doInBackground(Void... voids) {
      itemDao.deleteAll();
      return null;
    }
  }


}
