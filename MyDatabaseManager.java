package com.yang.mark.currency_converter;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;

/**
 * Created by Mark Nan Yang on 14/11/2015.
 */

public class MyDatabaseManager {
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "currency_type";
    //    public static final String CONTACTS_COLUMN_ID = "name";
//    public static final String CONTACTS_COLUMN_NAME = "check";
    private static Context context;
    private DBHelper dbHelper;
    public SQLiteDatabase db;
    private static class DBHelper extends SQLiteOpenHelper
    {

        private int idInUse = 0;

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            //db = getWritableDatabase();
            db.execSQL(
                    "create table " + CONTACTS_TABLE_NAME +
                            " (_id integer primary key autoincrement, name text, description text, symbol text);"
            );
            //insertCountry("todo", 1);
            //insertCountry("to0do", 1);
            //insertCountry("to9do", 1);
            //insertCountry("ireland",R.drawable.ireland_grunge_flag_by_think0);
            //insertCountry("england", R.drawable.download);
            //insertCountry("japan", R.drawable.jjapan);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
            onCreate(db);
            //insertCountry("ireland",R.drawable.ireland_grunge_flag_by_think0);
            //insertCountry("england", R.drawable.download);
            //insertCountry("japan", R.drawable.jjapan);

        }

        public boolean insertCountry(String name, String res_name,String symbol) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("description", res_name);
            contentValues.put("symbol", symbol);
            //contentValues.put("_id",idInUse++);
            if(db.update(CONTACTS_TABLE_NAME,contentValues, "name = ? ", new String[]{name})==0)
                db.insert(CONTACTS_TABLE_NAME, null, contentValues);
            return true;
        }

        public Cursor getData(String name) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from "+CONTACTS_TABLE_NAME+" where name=" + name + "", null);
            return res;
        }

        public int numberOfRows() {
            SQLiteDatabase db = this.getReadableDatabase();
            int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
            return numRows;
        }

        public boolean updateContact(String name, int checked) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("checked", checked);

            db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ", new String[]{name});
            return true;
        }

        public Integer deleteContact(String name) {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(CONTACTS_TABLE_NAME,
                    "id = ? ",
                    new String[]{name});
        }

        public void DeleteDataByName(String name)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(CONTACTS_TABLE_NAME,
                    "id = ? ",
                    new String[]{name});
        }

        public DataStruct GetDataStructByName(String name)
        {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + CONTACTS_TABLE_NAME + " where name = ? ", new String[]{name});
            res.moveToFirst();
            DataStruct dataStruct = new DataStruct();
            while (res.isAfterLast() == false) {

                dataStruct.abbr=(res.getString(res.getColumnIndex("name")));
                dataStruct.description=(res.getString(res.getColumnIndex("description")));
                dataStruct.symbol=(res.getString(res.getColumnIndex("symbol")));
                /*dataStruct.rate=Double.parseDouble(res.getString(res.getColumnIndex("rate")));
                dataStruct.amount= Double.parseDouble(res.getString(res.getColumnIndex("rate")));*/
                res.moveToNext();
            }
            return dataStruct;
        }

        public ArrayList<String> getAllCotacts() {
            ArrayList<String> array_list = new ArrayList<String>();

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from "+CONTACTS_TABLE_NAME, null);
            res.moveToFirst();

            while (res.isAfterLast() == false) {

                array_list.add(res.getString(res.getColumnIndex("name")));
                array_list.add(res.getString(res.getColumnIndex("checked")));
                res.moveToNext();
            }
            return array_list;
        }
    }

    public MyDatabaseManager(Context ctx)
    {
        this.context=ctx;
        dbHelper= new DBHelper(context);
    }
    public MyDatabaseManager open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public boolean DBInsert(String name,String description,String symbol) throws SQLException
    {
        return dbHelper.insertCountry(name,description,symbol);
    }
    public ArrayList<String> GetAllData()
    {
        return dbHelper.getAllCotacts();
    }
    public void close()
    {
        dbHelper.close();
    }
    public  boolean CreateDb()
    {
        InputStream is =context.getResources().openRawResource(R.raw.currencymap);
        /*
        quoted from http://stackoverflow.com/questions/6349759/using-json-file-in-android-app-resources/6349913#6349913
         */
        Writer writer = new StringWriter();
        //char[] buffer = new char[20024];
        String temp = new String();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // int n;
        String aux="";
        try {
            while((aux = reader.readLine()) != null)
                temp += aux;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //end quoted
        //String jsonString = writer.toString();
        JSONArray jsonArray;
        try {
            //JSONObject jsonObject = new JSONObject(temp);
            //JSONArray jsonArray = jsonObject.getJSONArray("CHF");
            jsonArray = new JSONArray(temp);
            for(int i = 0 ;i < jsonArray.length();i++)
            {
                //jsonArray.
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //s=s+"Task is:" + jsonObject.optString("title").toString() + ". And state is:" + jsonObject.optString("completed").toString() + "\n";
                //temp= jsonObject.optString("symbol").toString() + jsonObject.optString("name").toString();
                dbHelper.insertCountry(jsonObject.optString("cc").toString(), jsonObject.optString("name").toString(), jsonObject.optString("symbol").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
    public  Cursor GetCursor()
    {
        Cursor res = db.rawQuery("select * from "+CONTACTS_TABLE_NAME , null);
        return  res;
    }

    public DataStruct GetDataStructByName(String name)
    {
        return dbHelper.GetDataStructByName(name);
    }

    public void DeleteDataByName(String name)
    {
        dbHelper.DeleteDataByName(name);
    }
}

