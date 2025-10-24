package com.example.myfirstapp_jesusvelasquez.data;

import android.content.Context;
import java.util.List;

public class DBRepository {

    private final UserDao userDao;
    private final ItemDao itemDao;

    public DBRepository(Context ctx) {
        this.userDao = new UserDao(ctx);
        this.itemDao = new ItemDao(ctx);
    }

    // ---------- USERS ----------
    public boolean createUser(String username, String password) {
        // Hashing & salt handled inside UserDao.register()
        return userDao.register(username, password);
    }

    public boolean validateUser(String username, String password) {
        return userDao.login(username, password);
    }

    // ---------- ITEMS ----------
    public long addItem(String name, int quantity) {
        Item it = new Item();
        it.name = name;
        it.qty = quantity;
        return itemDao.insert(it);
    }

    public int updateItem(long id, String name, int quantity) {
        Item it = new Item();
        it.id = id;
        it.name = name;
        it.qty = quantity;
        return itemDao.update(it) ? 1 : 0;
    }

    public int deleteItem(long id) {
        return itemDao.delete(id) ? 1 : 0;
    }

    public List<Item> getAllItems() {
        return itemDao.all();
    }
}

