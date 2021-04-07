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

public class MemberBase {
    private static Member member;
    private static Bitmap memberAvatar;

    public static Member getMember() {
        if (member == null) {
            member = new Member(0,"guest", "guest", "guest");
        }
        return member;
    }

    public static Bitmap getMemberAvatar() {
        return memberAvatar;
    }

    public static void signOut() {
        member = new Member(0,"guest", "guest", "guest");
        memberAvatar = null;
    }

    public static void getMemberDetail(Activity activity, String account) {
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "MemberServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getMemberDetail");
            jsonObject.addProperty("account", account);
            String jsonIn = RemoteAccess.getRemoteData(url, jsonObject.toString());
            Type collectionType = new TypeToken<Member>() {
            }.getType();
            member = gson.fromJson(jsonIn, collectionType);

            jsonObject.addProperty("action", "getAvatar");
            jsonObject.addProperty("memberId", getMember().getId());
//            jsonObject.addProperty("imageSize", imageSize);
            memberAvatar = RemoteAccess.getRemoteImage(url, jsonObject.toString());
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
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
