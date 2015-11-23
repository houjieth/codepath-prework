package com.codepath.todo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mItems;
    private ArrayAdapter<String> mItemsAdapter;
    private ListView mLvItems;
    private EditText mEtNewItem;

    private static int REQUEST_CODE_EDIT = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readItems();

        mLvItems = (ListView) findViewById(R.id.lvItems);

        mItemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mItems);

        mLvItems.setAdapter(mItemsAdapter);
        setupListViewListener();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_EDIT) {
            int position = data.getIntExtra("position", 0);
            String itemValue = data.getStringExtra("itemValue");
            mItems.set(position, itemValue);
            mItemsAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    public void onAddItem(View view) {
        mEtNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = mEtNewItem.getText().toString();
        mItems.add(itemText);
        mEtNewItem.setText("");
        writeItems();
    }

    private void setupListViewListener() {
        mLvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                mItems.remove(pos);
                mItemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
        mLvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("position", pos);
                intent.putExtra("itemValue", mItems.get(pos));
                startActivityForResult(intent, REQUEST_CODE_EDIT);
            }
        });
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            mItems = new ArrayList<>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            Toast.makeText(this, "Fail to load items!", Toast.LENGTH_SHORT).show();
            mItems = new ArrayList<>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, mItems);
        } catch (IOException e) {
            Toast.makeText(this, "Fail to load items!", Toast.LENGTH_SHORT).show();
        }
    }
}
