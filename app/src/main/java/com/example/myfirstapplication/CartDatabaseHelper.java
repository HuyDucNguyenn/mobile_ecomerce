package com.example.myfirstapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CartDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cart.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_CART = "cart";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_QUANTITY = "quantity";

    public CartDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CART + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PRICE + " REAL, " +
                COLUMN_IMAGE + " INTEGER, " +
                COLUMN_QUANTITY + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    public long insertCartItem(String name, double price, int imageResId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + ", " + COLUMN_QUANTITY +
                " FROM " + TABLE_CART +
                " WHERE " + COLUMN_NAME + " = ?", new String[]{name});

        if (cursor != null && cursor.moveToFirst()) {
            int existingId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            int existingQty = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
            cursor.close();

            int newQty = existingQty + quantity;
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, newQty);
            return db.update(TABLE_CART, values, COLUMN_ID + " = ?", new String[]{String.valueOf(existingId)});
        }
        if (cursor != null) cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, imageResId);
        values.put(COLUMN_QUANTITY, quantity);
        return db.insert(TABLE_CART, null, values);
    }

    public int updateQuantity(int id, int newQty) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (newQty <= 0) {
            return deleteCartItem(id);
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, newQty);
        return db.update(TABLE_CART, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CART, null);
    }

    public int deleteCartItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CART, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
    }
}
