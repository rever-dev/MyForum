package com.rever.myforum.bean;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Reply {

    private int id;
    private int memberId;
    private int postId;
    private String memberNickname;
    private String content;
    private Timestamp datetime;
    private int likeCount;

    public Reply(int id, int memberId, int postId, String memberNickname, String content, Timestamp datetime, int likeCount) {
        this.id = id;
        this.memberId = memberId;
        this.postId = postId;
        this.memberNickname = memberNickname;
        this.content = content;
        this.datetime = datetime;
        this.likeCount = likeCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getMemberNickname() {
        return memberNickname;
    }

    public void setMemberNickname(String memberNickname) {
        this.memberNickname = memberNickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDatetime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

        return sdf.format(this.datetime);
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
