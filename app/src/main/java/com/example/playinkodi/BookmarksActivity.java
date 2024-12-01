package com.example.playinkodi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.playinkodi.bookmarks.Bookmark;
import com.example.playinkodi.bookmarks.BookmarkAdapter;
import com.example.playinkodi.database.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity implements BookmarkAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private List<Bookmark> bokkmarkList;
    private MyDatabaseHelper dbHelper;

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookmarks);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new MyDatabaseHelper(this);
        bokkmarkList = loadDataFromDatabase();

        adapter = new BookmarkAdapter(this, bokkmarkList, this);
        recyclerView.setAdapter(adapter);

        // Initialize the SharedViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }

    @Override
    public void onDelete(int position) {
        Bookmark item = bokkmarkList.get(position);
        deleteItemFromDatabase(item.getId());
        bokkmarkList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onClick(int position) {
        Bookmark item = bokkmarkList.get(position);
        Intent intent = new Intent(BookmarksActivity.this, MainActivity.class);
        intent.putExtra("url", item.getUrl());
        startActivity(intent); // Open MainActivity with the URL
    }

    private List<Bookmark> loadDataFromDatabase() {
        List<Bookmark> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Bookmarks", null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                items.add(new Bookmark(id, url));
            }
            cursor.close();
        }
        return items;
    }


    private void deleteItemFromDatabase(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM Bookmarks WHERE id = " + id);
    }
}