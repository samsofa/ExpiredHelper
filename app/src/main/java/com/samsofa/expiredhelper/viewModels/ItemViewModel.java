package com.samsofa.expiredhelper.viewModels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.samsofa.expiredhelper.models.Item;
import com.samsofa.expiredhelper.repo.ItemRepository;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemViewModel extends AndroidViewModel {

  private ItemRepository itemRepository;
  private LiveData<List<Item>> allItems;


  public ItemViewModel(@NonNull Application application) {
    super(application);

    itemRepository = new ItemRepository(application);
    allItems = itemRepository.getAllItems();
  }

  public LiveData<List<Item>> getAllItems() {
    return allItems;
  }

  public void insert(Item item) {
    itemRepository.insert(item);
  }

  public void update(Item item) {
    itemRepository.update(item);

  }

  public void delete(Item item) {
    itemRepository.delete(item);
  }

  public void deleteAllItems() {
    itemRepository.deleteAll();
  }


}
