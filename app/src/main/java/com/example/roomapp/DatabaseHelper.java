package com.example.roomapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "contact_db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Contact.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contact.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public long insertContact(String name, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contact.COLUMN_NAME, name);
        values.put(Contact.COLUMN_EMAIL, email);
        long id = db.insert(Contact.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Contact getContact(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Contact.TABLE_NAME, new String[]{Contact.COLUMN_ID, Contact.COLUMN_NAME, Contact.COLUMN_EMAIL}, Contact.COLUMN_ID +
                "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if(cursor!=null)
            cursor.moveToFirst();

        Contact contact = new Contact(
                cursor.getString(cursor.getColumnIndex(Contact.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Contact.COLUMN_EMAIL)),
                cursor.getInt(cursor.getColumnIndex(Contact.COLUMN_ID))
        );
        cursor.close();
        return contact;
    }

    public ArrayList<Contact> getAllCOntacts(){
        ArrayList<Contact> contacts = new ArrayList<>();
        String  selectQuery = "SELECT * FROM " + Contact.TABLE_NAME + " ORDER BY " + Contact.COLUMN_ID + " DESC";
        SQLiteDatabase db= this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            do{
                Contact contact = new Contact();
                contact.setId(cursor.getInt(cursor.getColumnIndex(Contact.COLUMN_ID)));
                contact.setEmail(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_EMAIL)));
                contact.setName(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_NAME)));

                contacts.add(contact);
            }while (cursor.moveToNext());
        }

        db.close();

        return contacts;
    }

    public int updateContact(Contact contact){
        SQLiteDatabase db =this.getWritableDatabase();
        ContentValues values =  new ContentValues();
        values.put(Contact.COLUMN_EMAIL, contact.getEmail());
        values.put(Contact.COLUMN_NAME, contact.getName());
        return db.update(Contact.TABLE_NAME, values, Contact.COLUMN_ID + " =? ", new String[]{String.valueOf(contact.getId())});
    }

    public void deleteCOntact(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Contact.TABLE_NAME, Contact.COLUMN_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        db.close();

    }
}
