package com.rever.myforum;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rever.myforum.bean.Post;

import java.lang.reflect.Type;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostHolder> {

    Activity activity;
    List<Post> postList;

    public PostAdapter(Activity activity, List<Post> postList) {
        this.activity = activity;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity)
                .inflate(R.layout.post_item, parent, false);
        return new PostHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = postList.get(position);

        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());
        holder.likeCount.setText(String.valueOf(post.getLikeCount()));
        holder.replyCount.setText(String.valueOf(post.getReplyCount()));
        holder.datetime.setText(post.getDatetime());
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("postId", post.getId());
            Navigation.findNavController(v).navigate(R.id.postViewFragment,
                    bundle);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void update(List<Post> list) {
        postList.clear();
        postList.addAll(list);
        notifyDataSetChanged();
    }
}

class PostHolder extends RecyclerView.ViewHolder {

    TextView title, content, likeCount, replyCount, datetime;

    public PostHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.postItem_textViewTitle);
        content = itemView.findViewById(R.id.postItem_textViewContent);
        likeCount = itemView.findViewById(R.id.postItem_textViewLikeCount);
        replyCount = itemView.findViewById(R.id.postItem_textViewReplyCount);
        datetime = itemView.findViewById(R.id.postItem_textViewPostDatetime);
    }
}
