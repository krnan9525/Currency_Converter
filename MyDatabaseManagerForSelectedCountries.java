package com.yang.mark.currency_converter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by TR on 22/11/2015.
 */
public class MyDatabaseManagerForSelectedCountries {
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "selected_con";
    //    public static final String CONTACTS_COLUMN_ID = "name";
//    public static final String CONTACTS_COLUMN_NAME = "check";
    private static Context context;
    private DBHelper dbHelper;
    public SQLiteDatabase db;
    private class DBHelper extends SQLiteOpenHelper
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
                            " (_id integer primary key autoincrement, name text, description text, symbol text, rate double, amount double);"
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

            db.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME);
            onCreate(db);
            //insertCountry("ireland",R.drawable.ireland_grunge_flag_by_think0);
            //insertCountry("england", R.drawable.download);
            //insertCountry("japan", R.drawable.jjapan);

        }

        public boolean insertCountry(String name, String res_name,String symbol) throws IOException {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("description", res_name);
            contentValues.put("symbol", symbol);
            contentValues.put("rate",0.0);
            String urlRequestStr = "https://currency-api.appspot.com/api/"+name+"/USD.json";
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null && networkInfo.isConnected())
            {
                new DownloadWebpageTask(context).execute(urlRequestStr,name,res_name,symbol);

            }
            else {
                //textView.setText("No network connection!");
            }

            //contentValues.put("_id",idInUse++);
            //if(db.update(CONTACTS_TABLE_NAME,contentValues, "name = ? ", new String[]{name})==0)
              //  db.insert(CONTACTS_TABLE_NAME, null, contentValues);
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
        public void UpdateByName(String name, Double valu)
        {
            db.execSQL("update "+CONTACTS_TABLE_NAME+ " set amount = "+valu+" where name = '"+name+"'");
        }

        public void DeleteDataByName(String name)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(CONTACTS_TABLE_NAME,
                    "name = ? ",
                    new String[]{name});
        }

        public Double GetRateByDes(String des)
        {
            Double result=0.0;

            Cursor res = db.query(CONTACTS_TABLE_NAME,new String[]{"*"},"name = ?",new String[]{des},null,null,null);
            res.moveToFirst();
            while (res.isAfterLast() == false)
            {
                if(res.getString(res.getColumnIndex("rate"))!=null)
                result =Double.parseDouble(res.getString(res.getColumnIndex("rate")));
                res.moveToNext();
            }
            return result;
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

    public MyDatabaseManagerForSelectedCountries(Context ctx)
    {
        this.context=ctx;
        dbHelper= new DBHelper(context);
    }
    public MyDatabaseManagerForSelectedCountries open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public boolean DBInsert(String name,String description,String symbol) throws SQLException, IOException {
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
    public  boolean CreateDb() throws IOException {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        dbHelper.onCreate(db);
        dbHelper.insertCountry("EUR", "European Euro", "\u20ac");
        dbHelper.insertCountry("USD", "United States dollar","US$");
        dbHelper.insertCountry("CNY","Chinese/Yuan renminbi","\u00a5");
        return true;
    }
    public  Cursor GetCursor()
    {
        Cursor res = db.rawQuery("select * from "+CONTACTS_TABLE_NAME , null);
        return  res;
    }



    private class DownloadWebpageTask extends AsyncTask<String, Void, String>
    {
        private Context context;
        public DownloadWebpageTask(Context context_new)
        {
            context=context_new;
        }
        protected String doInBackground(String... urls ) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0],urls[1],urls[2],urls[3]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        protected void onPostExecute(String result) {

            //textView.setText(result);
        }
    }
    private String downloadUrl(String myurl,String name,String res_name,String symbol) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 30000;

        try {
            URL url = new URL(myurl);
  /* PART 2:  INSERT CODE HERE: Use the HTTPURLConnection class to make a http connection.  Set some useful limits on the connection, such as connection timeout time, and read timeout. Set the HTTP request method to GET. Assume your connection object is called “conn”.*/
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(150000);
            conn.setConnectTimeout(100000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Network", "The response is: " + response);
            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = "["+readIt(is, len)+"]";
            try {
                String s = "";
                //JSONObject reader = new JSONObject(result);
                JSONArray jsonArray = new  JSONArray(contentAsString);
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", name);
                contentValues.put("description", res_name);
                contentValues.put("symbol", symbol);

                try {
                    for(int i = 0 ;i < jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //s=s+"Task is:" + jsonObject.optString("title").toString() + ". And state is:" + jsonObject.optString("completed").toString() + "\n";
                        contentValues.put("rate", Double.parseDouble(jsonObject.optString("rate").toString()));
                        contentValues.put("amount", Double.parseDouble(jsonObject.optString("rate").toString()));
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                if(db.update(CONTACTS_TABLE_NAME,contentValues, "name = ? ", new String[]{name})==0)
                    db.insert(CONTACTS_TABLE_NAME, null, contentValues);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }


    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        InputStreamReader inputData = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(inputData,len);
        //char[] buffer = new char[len];
        String temp = "";
        String aux="";
        while((aux = reader.readLine()) != null)
        {
            temp+=aux;
        }

        return temp;
    }

    public Double GetRateByname (String des)
    {
        return dbHelper.GetRateByDes(des);
    }

    public void UpdateByname(String name, double valu)
    {
        dbHelper.UpdateByName(name,valu);
    }


    public void DeleteDataByName(String name)
    {
        dbHelper.DeleteDataByName(name);
    }

    public void UpDateAllRate() throws IOException, SQLException {
        Cursor res = GetCursor();
        res.moveToFirst();
        DataStruct dataStruct = new DataStruct();
        while (res.isAfterLast() == false)
        {
            dataStruct.abbr= res.getString(res.getColumnIndex("name"));
            dataStruct.description= res.getString(res.getColumnIndex("description"));
            dataStruct.symbol= res.getString(res.getColumnIndex("symbol"));
            DBInsert(dataStruct.abbr,dataStruct.description,dataStruct.symbol);
            res.moveToNext();
        }
    }
}

