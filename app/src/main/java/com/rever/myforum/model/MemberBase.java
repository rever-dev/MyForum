package com.rever.myforum.model;

import android.app.Activity;
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

public class MemberBase {
    private static Member member;

    public static Member getMember() {
        if (member == null) {
            member = new Member(0,"guest", "guest", "guest");
        }
        return member;
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
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }
}
