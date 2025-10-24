package com.example.myfirstapp_jesusvelasquez.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;

/**
 * AppDbHelper
 * - v1: users, items (your original schema)
 * - v2: adds created_at/updated_at on both tables, NOCASE on username, indexes, and an update trigger
 */
public class AppDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "inventory.db";
    // Bump to 2 to apply the migration path below
    public static final int DB_VERSION = 2;

    public static final String TBL_USERS = "users";
    public static final String COL_USER_ID = "_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASS_HASH = "pass_hash";
    public static final String COL_SALT = "salt";
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_UPDATED_AT = "updated_at";

    public static final String TBL_ITEMS = "items";
    public static final String COL_ITEM_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_SKU = "sku";
    public static final String COL_QTY = "qty";
    public static final String COL_LOCATION = "location";
    public static final String COL_THRESHOLD = "threshold";

    public AppDbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    // Enable FK and WAL early
    @Override
    public void onConfigure(@NonNull SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        // Optional: WAL for better write perf while scrolling lists
        db.enableWriteAheadLogging();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // USERS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // Enforce case-insensitive uniqueness on usernames
                COL_USERNAME + " TEXT UNIQUE NOT NULL COLLATE NOCASE," +
                COL_PASS_HASH + " TEXT NOT NULL," +
                COL_SALT + " TEXT NOT NULL," +
                COL_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s','now'))," +
                COL_UPDATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                ")");

        // ITEMS
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TBL_ITEMS + " (" +
                COL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " TEXT NOT NULL," +
                COL_SKU + " TEXT," +
                COL_QTY + " INTEGER NOT NULL DEFAULT 0," +
                COL_LOCATION + " TEXT," +
                COL_THRESHOLD + " INTEGER NOT NULL DEFAULT 0," +
                COL_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s','now'))," +
                COL_UPDATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s','now'))" +
                ")");

        // Indexes for fast grid queries (name/sku for search & ordering, qty for low-stock checks)
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_name ON " + TBL_ITEMS + " (" + COL_NAME + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_sku ON " + TBL_ITEMS + " (" + COL_SKU + ")");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_qty ON " + TBL_ITEMS + " (" + COL_QTY + ")");

        // Update timestamp trigger for ITEMS (fires on UPDATE)
        db.execSQL("CREATE TRIGGER IF NOT EXISTS trg_items_updated_at " +
                "AFTER UPDATE ON " + TBL_ITEMS + " " +
                "BEGIN " +
                "  UPDATE " + TBL_ITEMS + " SET " + COL_UPDATED_AT + " = strftime('%s','now') " +
                "  WHERE " + COL_ITEM_ID + " = NEW." + COL_ITEM_ID + ";" +
                "END;");

        // Update timestamp trigger for USERS (fires on UPDATE)
        db.execSQL("CREATE TRIGGER IF NOT EXISTS trg_users_updated_at " +
                "AFTER UPDATE ON " + TBL_USERS + " " +
                "BEGIN " +
                "  UPDATE " + TBL_USERS + " SET " + COL_UPDATED_AT + " = strftime('%s','now') " +
                "  WHERE " + COL_USER_ID + " = NEW." + COL_USER_ID + ";" +
                "END;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            // ---- v1 -> v2 non-destructive migration ----
            // Add timestamps if missing
            db.execSQL("ALTER TABLE " + TBL_USERS + " ADD COLUMN " + COL_CREATED_AT + " INTEGER DEFAULT (strftime('%s','now'))");
            db.execSQL("ALTER TABLE " + TBL_USERS + " ADD COLUMN " + COL_UPDATED_AT + " INTEGER DEFAULT (strftime('%s','now'))");
            db.execSQL("ALTER TABLE " + TBL_ITEMS + " ADD COLUMN " + COL_CREATED_AT + " INTEGER DEFAULT (strftime('%s','now'))");
            db.execSQL("ALTER TABLE " + TBL_ITEMS + " ADD COLUMN " + COL_UPDATED_AT + " INTEGER DEFAULT (strftime('%s','now'))");

            // We cannot change existing column collation inline; enforce via unique index trick:
            // Create a new unique index on LOWER(username) to simulate case-insensitive uniqueness.
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS ux_users_username_nocase ON " +
                    TBL_USERS + " (LOWER(" + COL_USERNAME + "))");

            // Indexes and triggers (idempotent)
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_name ON " + TBL_ITEMS + " (" + COL_NAME + ")");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_sku ON " + TBL_ITEMS + " (" + COL_SKU + ")");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_items_qty ON " + TBL_ITEMS + " (" + COL_QTY + ")");

            db.execSQL("CREATE TRIGGER IF NOT EXISTS trg_items_updated_at " +
                    "AFTER UPDATE ON " + TBL_ITEMS + " " +
                    "BEGIN " +
                    "  UPDATE " + TBL_ITEMS + " SET " + COL_UPDATED_AT + " = strftime('%s','now') " +
                    "  WHERE " + COL_ITEM_ID + " = NEW." + COL_ITEM_ID + ";" +
                    "END;");

            db.execSQL("CREATE TRIGGER IF NOT EXISTS trg_users_updated_at " +
                    "AFTER UPDATE ON " + TBL_USERS + " " +
                    "BEGIN " +
                    "  UPDATE " + TBL_USERS + " SET " + COL_UPDATED_AT + " = strftime('%s','now') " +
                    "  WHERE " + COL_USER_ID + " = NEW." + COL_USER_ID + ";" +
                    "END;");
        }

        // Future versions: add additional if-blocks (oldV < 3) { … }
    }

    // Optional: consistency checks
    @Override
    public void onOpen(@NonNull SQLiteDatabase db) {
        super.onOpen(db);
        // Lightweight integrity check in debug runs (no-op in release if you prefer)
        // db.execSQL("PRAGMA quick_check");
    }
}