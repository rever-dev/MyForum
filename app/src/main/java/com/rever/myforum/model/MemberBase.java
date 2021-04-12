package com.rever.myforum.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rever.myforum.R;
import com.rever.myforum.bean.Member;
import com.rever.myforum.bean.Post;
import com.rever.myforum.network.RemoteAccess;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class MemberBase {
    private static Member member;
    private static Bitmap memberAvatar;
    private static List<Post> myPost;
    private static List<Post> myFavPost;
    private static int myPostCount = 0;
    private static int myReplyCount = 0;
    private static int myLikeTotal = 0;

    public static Member getMember() {
        if (member == null) {
            member = new Member(0,"guest", "guest", "guest");
        }
        return member;
    }

    public static Bitmap getMemberAvatar() {
        return memberAvatar;
    }

    public static List<Post> getMyPost() {
        return myPost;
    }

    public static List<Post> getMyFavPost() {
        return myFavPost;
    }

    public static int getMyPostCount() {
        return myPostCount;
    }

    public static int getMyReplyCount() {
        return myReplyCount;
    }

    public static int getMyLikeTotal() {
        return myLikeTotal;
    }

    public static void signOut() {
        member = new Member(0,"guest", "guest", "guest");
        myPostCount = 0;
        myReplyCount = 0;
        myLikeTotal = 0;
        memberAvatar = null;
    }

    public static void getMemberDetail(Activity activity, String account) {
        if (RemoteAccess.networkConnected(activity)) {
            String urlM = RemoteAccess.URL_SERVER + "MemberServlet";
            String urlP = RemoteAccess.URL_SERVER + "PostServlet";
            String urlR = RemoteAccess.URL_SERVER + "ReplyServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getMemberDetail");
            jsonObject.addProperty("account", account);
            String jsonIn = RemoteAccess.getRemoteData(urlM, jsonObject.toString());
            Type collectionType = new TypeToken<Member>() {
            }.getType();
            member = gson.fromJson(jsonIn, collectionType);

            jsonObject.addProperty("action", "getAvatar");
            jsonObject.addProperty("memberId", getMember().getId());
            memberAvatar = RemoteAccess.getRemoteImage(urlM, jsonObject.toString());

            jsonObject.addProperty("action", "getMemberPostCount");
            jsonObject.addProperty("memberId", getMember().getId());
            myPostCount = Integer.parseInt(RemoteAccess.getRemoteData(urlP, jsonObject.toString()));

            jsonObject.addProperty("action", "getMemberPostLikeTotal");
            jsonObject.addProperty("memberId", getMember().getId());
            int postLikeTotal =  Integer.parseInt(RemoteAccess.getRemoteData(urlP, jsonObject.toString()));

            jsonObject.addProperty("action", "getMemberReplyCount");
            jsonObject.addProperty("memberId", getMember().getId());
            myReplyCount = Integer.parseInt(RemoteAccess.getRemoteData(urlR, jsonObject.toString()));

            jsonObject.addProperty("action", "getMemberReplyLikeTotal");
            jsonObject.addProperty("memberId", getMember().getId());
            int replyLikeTotal =  Integer.parseInt(RemoteAccess.getRemoteData(urlR, jsonObject.toString()));

            myLikeTotal = postLikeTotal + replyLikeTotal;
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap getMemberAvatar(Activity activity, int memberId) {
        Bitmap bitmap;
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "MemberServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAvatar");
            jsonObject.addProperty("memberId", memberId);
            bitmap = RemoteAccess.getRemoteImage(url, jsonObject.toString());

            return bitmap;
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static void updateMemberDetail(Activity activity, String nickname, String password, byte[] image) {
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "MemberServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "update");
            jsonObject.addProperty("memberId", MemberBase.getMember().getId());
            jsonObject.addProperty("nickname", nickname);
            jsonObject.addProperty("password", password);
            jsonObject.addProperty("image", Arrays.toString(image));
            RemoteAccess.getRemoteData(url, jsonObject.toString());
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateMemberDetail(Activity activity, String nickname, String password) {
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "MemberServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "update");
            jsonObject.addProperty("memberId", MemberBase.getMember().getId());
            jsonObject.addProperty("nickname", nickname);
            jsonObject.addProperty("password", password);
            RemoteAccess.getRemoteData(url, jsonObject.toString());
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }
}
