package com.example.playinkodi;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
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
    private List<Bookmark> bookmarkList;
    private MyDatabaseHelper dbHelper;

    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookmarks);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Bookmarks");
//        actionBar.hide();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new MyDatabaseHelper(this);
        bookmarkList = loadDataFromDatabase();

        adapter = new BookmarkAdapter(this, bookmarkList, this);
        recyclerView.setAdapter(adapter);

         // Add the divider
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        // Initialize the SharedViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }

    @Override
    public void onDelete(int position) {
        Bookmark item = bookmarkList.get(position);
        dbHelper.deleteBookmark(item.getId());
        bookmarkList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onClick(int position) {
        Bookmark item = bookmarkList.get(position);
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
}