package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import org.apache.commons.io.FileUtils ;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text" ;
    public static  final String KEY_ITEM_POSITION = "item_position ";
    public static final int EDIT_TEXT_CODE = 20 ;
    // create the  value
    List<String> items ;

    Button btnAdd;
    EditText plainText ;
    RecyclerView rvItem ;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect all the id to the java code.
        btnAdd = findViewById(R.id.btnAdd);
        plainText = findViewById(R.id.plainText) ;
        rvItem = findViewById(R.id.rvItem) ;

        // create a empty arraylist
        loadItem();
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvItem.addItemDecoration(itemDecoration);


        ItemsAdapter.OnLongClickListener onLongClickListener =  new ItemsAdapter.OnLongClickListener(){

            @Override
            public void onItemLongClicked(int position) {
                // delete the item from the model
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was remove",Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };

        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("MainActivity", "Single click at position"+ position);
                // Create the new activity
                Intent i = new Intent(MainActivity.this, EditAcitivty.class);
                // pass the data being edited
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION, position);
                // display the activity
                startActivityForResult(i,EDIT_TEXT_CODE );
            }
        };


        itemsAdapter = new ItemsAdapter(items,onLongClickListener,onClickListener);
        rvItem.setAdapter(itemsAdapter);
        rvItem.setLayoutManager(new LinearLayoutManager(this));

        // This is the activity for the bottom
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = plainText.getText().toString();
                //Add item to the model
                items.add(todoItem);
                //Notify adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size()-1);
                plainText.setText("");
                Toast.makeText(getApplicationContext(), "Item was added",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    // handle the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode , int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // Retreive the update Text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // extract the orignal position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // upadate the model at the right position with new item text
            items.set(position, itemText);
            // notify the adapter
            itemsAdapter.notifyItemChanged(position);
            // persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Item updated successfully", Toast.LENGTH_SHORT).show();

        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }

    };

    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }
    // This function will load items by reading every line of the data file
    private void loadItem(){
       try {
           items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
       }catch (IOException e){
           e.printStackTrace();
           Log.e("MainActivity", "Error reading items", e);
           items = new ArrayList<>();
       }
    }

    //This function saves items by writing them into the data file
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        }catch (IOException e ){
            Log.e("MainActivity", "Error reading items", e);
        }
    }



}