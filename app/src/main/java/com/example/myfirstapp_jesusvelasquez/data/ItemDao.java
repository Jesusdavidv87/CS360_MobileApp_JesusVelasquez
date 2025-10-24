package com.example.myfirstapp_jesusvelasquez.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

public class ItemDao {
    private final AppDbHelper helper;
    public ItemDao(Context ctx){ helper = new AppDbHelper(ctx); }

    public long insert(Item it){
        ContentValues cv = new ContentValues();
        cv.put(AppDbHelper.COL_NAME, it.name);
        cv.put(AppDbHelper.COL_SKU, it.sku);
        cv.put(AppDbHelper.COL_QTY, it.qty);
        cv.put(AppDbHelper.COL_LOCATION, it.location);
        cv.put(AppDbHelper.COL_THRESHOLD, it.threshold);
        try(SQLiteDatabase db = helper.getWritableDatabase()){
            return db.insert(AppDbHelper.TBL_ITEMS, null, cv);
        }
    }

    public boolean update(Item it){
        ContentValues cv = new ContentValues();
        cv.put(AppDbHelper.COL_NAME, it.name);
        cv.put(AppDbHelper.COL_SKU, it.sku);
        cv.put(AppDbHelper.COL_QTY, it.qty);
        cv.put(AppDbHelper.COL_LOCATION, it.location);
        cv.put(AppDbHelper.COL_THRESHOLD, it.threshold);
        try(SQLiteDatabase db = helper.getWritableDatabase()){
            return db.update(AppDbHelper.TBL_ITEMS, cv,
                    AppDbHelper.COL_ITEM_ID+"=?",
                    new String[]{String.valueOf(it.id)}) > 0;
        }
    }

    public boolean delete(long id){
        try(SQLiteDatabase db = helper.getWritableDatabase()){
            return db.delete(AppDbHelper.TBL_ITEMS,
                    AppDbHelper.COL_ITEM_ID+"=?",
                    new String[]{String.valueOf(id)}) > 0;
        }
    }

    public List<Item> all(){
        List<Item> out = new ArrayList<>();
        try(SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query(AppDbHelper.TBL_ITEMS, null, null, null, null, null, AppDbHelper.COL_NAME)){
            while(c.moveToNext()){
                Item it = new Item();
                it.id = c.getLong(c.getColumnIndexOrThrow(AppDbHelper.COL_ITEM_ID));
                it.name = c.getString(c.getColumnIndexOrThrow(AppDbHelper.COL_NAME));
                it.sku = c.getString(c.getColumnIndexOrThrow(AppDbHelper.COL_SKU));
                it.qty = c.getInt(c.getColumnIndexOrThrow(AppDbHelper.COL_QTY));
                it.location = c.getString(c.getColumnIndexOrThrow(AppDbHelper.COL_LOCATION));
                it.threshold = c.getInt(c.getColumnIndexOrThrow(AppDbHelper.COL_THRESHOLD));
                out.add(it);
            }
        }
        return out;
    }
}

