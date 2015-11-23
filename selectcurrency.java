package com.yang.mark.currency_converter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class selectcurrency extends AppCompatActivity {

    ListView listView;
    MyDatabaseManagerForSelectedCountries myDatabaseManagerForSelectedCountries = new MyDatabaseManagerForSelectedCountries(this);
    MyDatabaseManager myDatabaseManager = new MyDatabaseManager(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcurrency);
        listView = (ListView)findViewById(R.id.listView2);
        try
        {
            myDatabaseManagerForSelectedCountries.open();
            myDatabaseManager.open();
            //myDatabaseManagerForSelectedCountries.CreateDb();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        Cursor cursor;
        cursor = myDatabaseManager.GetCursor();
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.forselecting,cursor,new String[] {"name","symbol","description"},
                new int[]{R.id.textView2,R.id.textView3,R.id.textView});
        listView.setAdapter(simpleCursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selectcurrency, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage2(View view)
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        this.finish();
    }

}
