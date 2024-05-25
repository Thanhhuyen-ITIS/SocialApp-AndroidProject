package com.example.socialapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.socialapp.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder>{

    private Context context;
    private List<String> mTags;

    private List<String> mTagsCount;

    public TagAdapter(Context context, List<String> mTags, List<String> mTagsCount) {
        this.context = context;
        this.mTags = mTags;
        this.mTagsCount = mTagsCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_item, parent, false);
        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tag.setText("#"+mTags.get(position));
        holder.count.setText(mTagsCount.get(position) + " posts");

    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tag;
        public TextView count;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.hash_tag);
            count = itemView.findViewById(R.id.no_of_post);
        }
    }
    public void filter(List<String> filterTags, List<String> filterTagsCount){
        this.mTags = filterTags;
        this.mTagsCount = filterTagsCount;
        notifyDataSetChanged();
    }
}
