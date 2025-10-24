package com.example.myfirstapp_jesusvelasquez.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class UserDao {
    private final AppDbHelper helper;
    public UserDao(Context ctx){ helper = new AppDbHelper(ctx); }

    public boolean usernameExists(String u){
        try (SQLiteDatabase db = helper.getReadableDatabase();
             Cursor c = db.query(AppDbHelper.TBL_USERS, new String[]{AppDbHelper.COL_USER_ID},
                     "LOWER(" + AppDbHelper.COL_USERNAME + ")=LOWER(?)",
                     new String[]{u}, null, null, null)) {
            return c.moveToFirst();
        }
    }

    public boolean register(String u, String raw){
        if (usernameExists(u)) return false;
        String salt = genSalt();
        String hash = hash(raw, salt);
        ContentValues cv = new ContentValues();
        cv.put(AppDbHelper.COL_USERNAME, u);
        cv.put(AppDbHelper.COL_PASS_HASH, hash);
        cv.put(AppDbHelper.COL_SALT, salt);
        try(SQLiteDatabase db = helper.getWritableDatabase()){
            return db.insert(AppDbHelper.TBL_USERS, null, cv) > 0;
        }
    }

    public boolean login(String u, String raw){
        try(SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query(AppDbHelper.TBL_USERS,
                    new String[]{AppDbHelper.COL_PASS_HASH, AppDbHelper.COL_SALT},
                    "LOWER(" + AppDbHelper.COL_USERNAME + ")=LOWER(?)",
                    new String[]{u}, null, null, null)){
            if (!c.moveToFirst()) return false;
            String hash = c.getString(0), salt = c.getString(1);
            return hash.equals(hash(raw, salt));
        }
    }

    private static String genSalt(){
        byte[] s = new byte[16];
        new SecureRandom().nextBytes(s);
        return Base64.getEncoder().encodeToString(s);
    }
    private static String hash(String raw, String salt){
        try{
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            KeySpec spec = new PBEKeySpec(raw.toCharArray(), saltBytes, 100_000, 256);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return Base64.getEncoder().encodeToString(f.generateSecret(spec).getEncoded());
        }catch(Exception e){ throw new RuntimeException(e); }
    }
}
