package com.rever.myforum;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.rever.myforum.bean.Reply;
import com.rever.myforum.model.MemberBase;
import com.rever.myforum.model.PostBase;
import com.rever.myforum.model.ReplyBase;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyHolder> {
    private Activity activity;
    private List<Reply> replyList;
    private SharedPreferences shp;

    public ReplyAdapter(Activity activity, List<Reply> replyList, SharedPreferences shp) {
        this.activity = activity;
        this.replyList = replyList;
        this.shp = shp;
    }

    @NonNull
    @Override
    public ReplyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity)
                .inflate(R.layout.reply_item, parent, false);
        return new ReplyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyHolder holder, int position) {
        Reply reply = replyList.get(position);
        Bitmap avatar = MemberBase.getMemberAvatar(activity, reply.getMemberId());
        if (avatar != null) {
            Glide.with(activity)
                    .applyDefaultRequestOptions(new RequestOptions().override(50, 50))
                    .load(avatar)
                    .transform(new CircleCrop())
                    .into(holder.imageViewMemberAvatar);
        } else {
            holder.imageViewMemberAvatar.setImageResource(R.drawable.account_default_image);
        }
        holder.checkBoxLike.setChecked(ReplyBase.checkedLikeStatus(reply.getId(), MemberBase.getMember().getId()));
        holder.checkBoxLike.setText(String.valueOf(reply.getLikeCount()));
        holder.memberNickname.setText(reply.getMemberNickname());
        holder.datetime.setText(reply.getDatetime());
        holder.content.setText(reply.getContent());

        if (!shp.getBoolean("signIn", false)) {
            holder.checkBoxLike.setClickable(false);
        }
        holder.checkBoxLike.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                reply.setLikeCount(reply.getLikeCount() + 1);
                holder.checkBoxLike.setText(String.valueOf(reply.getLikeCount()));
                ReplyBase.addReplyLike(activity, reply.getId(), reply.getPostId(), reply.getLikeCount());
            } else {
                reply.setLikeCount(reply.getLikeCount() - 1);
                holder.checkBoxLike.setText(String.valueOf(reply.getLikeCount()));
                ReplyBase.deleteReplyLike(activity, reply.getId(), reply.getPostId(), reply.getLikeCount());
            }
        });

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
        content = itemView.findViewById(R.id.replyItem_textViewContent);
        datetime = itemView.findViewById(R.id.replyItem_textViewDatetime);
    }
}
