package com.rever.myforum;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rever.myforum.bean.Reply;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyHolder>{
    Activity activity;
    List<Reply> replyList;

    public ReplyAdapter(Activity activity, List<Reply> replyList) {
        this.activity = activity;
        this.replyList = replyList;
    }

    @NonNull
    @Override
    public ReplyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity)
                .inflate(R.layout.post_item, parent, false);
        return new ReplyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyHolder holder, int position) {
        Reply reply = replyList.get(position);
//        holder.imageViewMemberAvatar.setImageBitmap();
        holder.checkBoxLike.setText(String.valueOf(reply.getLikeCount()));
        holder.memberNickname.setText(reply.getMemberNickname());
        holder.datetime.setText(reply.getDatetime());
        holder.content.setText(reply.getContent());

    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public void update(List<Reply> list) {
        replyList.clear();
        replyList.addAll(list);
        notifyDataSetChanged();
    }

}
class ReplyHolder extends RecyclerView.ViewHolder {

    ImageView imageViewMemberAvatar;
    CheckBox checkBoxLike;
    TextView memberNickname, content, datetime;

    public ReplyHolder(@NonNull View itemView) {
        super(itemView);
        imageViewMemberAvatar = itemView.findViewById(R.id.replyItem_imageViewMemberAvatar);
        checkBoxLike = itemView.findViewById(R.id.replyItem_checkBoxLike);
        memberNickname = itemView.findViewById(R.id.replyItem_textViewMemberNickname);
        content = itemView.findViewById(R.id.postItem_textViewContent);
        datetime = itemView.findViewById(R.id.postItem_textViewPostDatetime);
    }
}
