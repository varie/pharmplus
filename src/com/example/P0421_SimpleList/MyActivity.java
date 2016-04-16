package com.example.P0421_SimpleList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyActivity extends Activity implements View.OnClickListener {

    String[] categories = {"Обезбаливающие", "Противоаллергенные", "Лечение алкогольной и наркотической зависимости"};

    final int DIALOG = 1;
    LinearLayout view;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    Calendar c = Calendar.getInstance();
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ListView listView = (ListView) findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(),MedicineListActivity.class);
                startActivity(i);
            }
        }));

        handleIntent(getIntent());

 /*       SimpleCursorAdapter mAdapter =
                new SimpleCursorAdapter(
                        this,                // Current context
                        android.R.layout.simple_list_item_1,  // Layout for a single row
                        null,                // No Cursor yet
                        mFromColumns,        // Cursor columns to use
                        mToFields,           // Layout fields to use
                        0                    // No flags
                );
// Sets the adapter for the view
        listView.setAdapter(mAdapter);*/

        mDatabaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);

        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();



  /*      ContentValues values = new ContentValues();
        // Задайте значения для каждого столбца
        values.put(DatabaseHelper.MEDICINE_NAME_COLUMN, "Аспирин");
        values.put(DatabaseHelper.CATEGORY_COLUMN, "Обезбалевающее");
        values.put(DatabaseHelper.DESCIPTION_COLUMN, "Обезбаливает");
        // Вставляем данные в таблицу
        mSqLiteDatabase.insert("medicine", null, values);
        values.put(DatabaseHelper.MEDICINE_NAME_COLUMN, "Баралгин");
        values.put(DatabaseHelper.CATEGORY_COLUMN, "Обезбалевающее");
        values.put(DatabaseHelper.DESCIPTION_COLUMN, "Обезбаливает");
        mSqLiteDatabase.insert("medicine", null, values);
        */
    }

    DatabaseTable db = new DatabaseTable(this);

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Cursor c = db.getWordMatches(query, null);

            //WE NEED TO DO SOMETHING WITH THAT!!! VVVV
            //c.getString(c.getColumnIndex(c.getColumnName(0)));
            //process Cursor and display results
            //TextView infoTextView = (TextView)findViewById(R.id.textView);
            //infoTextView.setText(c.getString(c.getColumnIndex(c.getColumnName(0))));
        }
    }

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    public void onClick(View v) {
        Cursor cursor = mSqLiteDatabase.query("medicine", new String[] {DatabaseHelper.MEDICINE_NAME_COLUMN,
                        DatabaseHelper.CATEGORY_COLUMN, DatabaseHelper.DESCIPTION_COLUMN},
                null, null,
                null, null, null) ;

        cursor.moveToLast();

        String medicineName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MEDICINE_NAME_COLUMN));
        String category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CATEGORY_COLUMN));
        String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DESCIPTION_COLUMN));

        TextView infoTextView = (TextView)findViewById(R.id.textView);
        infoTextView.setText(String.format("Название %s\nКатегория: %s\nОписание %s", medicineName, category, description));

        // не забываем закрывать курсор
        cursor.close();
    }

 /*   public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.add:
            {
                onSearchRequested();
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("dialog");
        view = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog, null);
        adb.setView(view);

        return adb.create();
    }

    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == DIALOG) {
            TextView tvTime = (TextView) dialog.getWindow().findViewById(R.id.tvTime);
            tvTime.setText(sdf.format(new Date(System.currentTimeMillis())));
        };
    }

    OnClickListener myClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_NEGATIVE:
                    finish();
                    break;
            }
        }
    };
}
