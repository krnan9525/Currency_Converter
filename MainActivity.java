package com.yang.mark.currency_converter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private MyDatabaseManager myDatabaseManager = new MyDatabaseManager(this) ;
    private ListView listView ;
    private Button button;
    private MyDatabaseManagerForSelectedCountries myDatabaseManagerForSelectedCountries = new MyDatabaseManagerForSelectedCountries(this);
    private EditText editText;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);
        listView.setClickable(true);
        context=this;
        button = (Button)findViewById(R.id.button);
        editText = (EditText)findViewById(R.id.editText);

        try {
            myDatabaseManager.open();
            myDatabaseManagerForSelectedCountries.open();
            //myDatabaseManagerForSelectedCountries.CreateDb();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TextView textView = (TextView)findViewById(R.id.textView7);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String temp = prefs.getString("datetime", "UNKNOWN");
        temp="Update at:"+temp;
        textView.setText(temp);
        myDatabaseManager.CreateDb();
        Cursor cursor;
        cursor = myDatabaseManagerForSelectedCountries.GetCursor();
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.listcurrency,cursor,new String[] {"name","symbol","description","amount"},
                new int[]{R.id.textView2,R.id.textView3,R.id.textView,R.id.textView6});
        listView.setAdapter(simpleCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                TextView textView = (TextView)view.findViewById(R.id.textView2);
                String abbr=textView.getText().toString();
                Toast.makeText(MainActivity.this, "You chose:"+abbr, Toast.LENGTH_SHORT).show();
                Double formerRate = myDatabaseManagerForSelectedCountries.GetRateByname(abbr);
                if(formerRate!=0.0)
                {
                    TextView textView1 = (TextView) view.findViewById(R.id.textView6);
                    textView1.setText(editText.getText());
                    myDatabaseManagerForSelectedCountries.UpdateByname(abbr, Double.parseDouble(editText.getText().toString()));
                    for (int i = 0; i < listView.getAdapter().getCount(); i++)
                    {
                        if (i != position)
                        {
                            View newView;
                            newView = listView.getAdapter().getView(i, null, listView);
                            TextView newTextView = (TextView) newView.findViewById(R.id.textView2);
                            String des = newTextView.getText().toString();
                            Double rate = myDatabaseManagerForSelectedCountries.GetRateByname(des);
                            //TextView tempTextView = (TextView) newView.findViewById(R.id.textView6);
                            if (rate != 0)
                            {
                                Double TEMP = (Double.parseDouble(editText.getText().toString()) * formerRate / rate);
                                myDatabaseManagerForSelectedCountries.UpdateByname(des, TEMP);
                            }
                        }

                    }

                    Cursor cursor;
                    cursor = myDatabaseManagerForSelectedCountries.GetCursor();
                    SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(context, R.layout.listcurrency, cursor, new String[]{"name", "symbol", "description", "amount"},
                            new int[]{R.id.textView2, R.id.textView3, R.id.textView, R.id.textView6});
                    listView.setAdapter(simpleCursorAdapter);

                }

            }

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void sendMessage(View view)
    {
        Intent intent = new Intent(this,selectcurrency.class);
        startActivity(intent);
        this.finish();
    }
    public void sendMessage5(View view) throws IOException, SQLException {
        try {
            myDatabaseManagerForSelectedCountries.UpDateAllRate();
            Cursor cursor;
            cursor = myDatabaseManagerForSelectedCountries.GetCursor();
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(context, R.layout.listcurrency, cursor, new String[]{"name", "symbol", "description", "amount"},
                    new int[]{R.id.textView2, R.id.textView3, R.id.textView, R.id.textView6});
            listView.setAdapter(simpleCursorAdapter);
            TextView textView = (TextView)findViewById(R.id.textView7);
            Calendar c = Calendar.getInstance();
            String dateTimeKey = "com.example.app.datetime";
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date now = c.getTime();
            String mTimeString = sdf.format( now ); // contains yyyy-MM-dd (e.g. 2012-03-15 for March 15, 2012)

            String dateTimeString = mTimeString+", "+c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE);
            editor.putString("datetime", dateTimeString);
            editor.apply();
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            String temp = prefs.getString("datetime", null);
            temp="Update at:"+temp;
            textView.setText(temp);
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "NO CONNECTION", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public class MyAdapter extends SimpleCursorAdapter {
        public MyAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);

            /*editText = (EditText)findViewById(R.id.editText);
            editText.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    LinearLayout vwParentRow = (LinearLayout) editText.getParent();
                    LinearLayout vwParentRow2 = (LinearLayout) vwParentRow.getParent();
                    TextView textView = (TextView) vwParentRow2.findViewById(R.id.textView);
                    String des = textView.getText().toString();
                    Double rate = myDatabaseManagerForSelectedCountries.GetRateByDes(des);
                    textView.setText(rate.toString());
                }
            });*/
        }

    }

}
