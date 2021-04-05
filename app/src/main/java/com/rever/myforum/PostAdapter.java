package com.rever.myforum;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rever.myforum.bean.Post;

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
        holder.likeCount.setText(post.getLikeCount());
        holder.replyCount.setText(post.getReplyCount());
        holder.datetime.setText(post.getDatetime().toString());
    }

    @Override
    public int getItemCount() {
        return postList.size();
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
