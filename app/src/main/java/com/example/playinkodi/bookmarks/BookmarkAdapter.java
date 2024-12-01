package com.example.playinkodi.bookmarks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.playinkodi.R;
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ItemViewHolder> {

    private Context context;
    private List<Bookmark> itemList;
    private OnItemActionListener actionListener;

    public interface OnItemActionListener {
        void onDelete(int position);
        void onClick(int position);
    }
    public BookmarkAdapter(Context context, List<Bookmark> itemList, OnItemActionListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.actionListener = listener;
    }

//    @NonNull
//    @Override
//    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
//        return new ItemViewHolder(view);
//    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.bookmarks_list, parent, false);
        View view = LayoutInflater.from(context).inflate(R.layout.bookmarks_list, parent, false);
        return new ItemViewHolder(view);
    }



//    @Override
//    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
//        Bookmark item = itemList.get(position);
//        holder.textView1.setText(String.valueOf(item.getId()));
//        holder.textView2.setText(item.getUrl());
//    }

      @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Bookmark item = itemList.get(position);
        holder.itemName.setText(item.getUrl());

        holder.deleteIcon.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(position);
            }
        });

        holder.itemName.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onClick(position);
            }
        });

        // Handle delete button click
//        holder.deleteIcon.setOnClickListener(v -> {
//            if (actionListener != null) {
//                actionListener.onDelete(position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

//    static class ItemViewHolder extends RecyclerView.ViewHolder {
//        TextView textView1;
//        TextView textView2;
//
//        public ItemViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textView1 = itemView.findViewById(android.R.id.text1);
//            textView2 = itemView.findViewById(android.R.id.text2);
//        }
//    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView deleteIcon;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);  // Correct ID
            deleteIcon = itemView.findViewById(R.id.delete_icon);  // Correct ID
        }
    }
}