package com.rever.myforum.model;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rever.myforum.R;
import com.rever.myforum.bean.Post;
import com.rever.myforum.bean.Reply;
import com.rever.myforum.network.RemoteAccess;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReplyList {

    private static List<Reply> replyList;

    public static List<Reply> getReplyList() {
        if (replyList == null) {
            replyList = new ArrayList<>();
        }
        return replyList;
    }

    public static void queryReplyList(Activity activity, int postId) {
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "ReplyServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "query");
            jsonObject.addProperty("postId", postId);
            String jsonIn = RemoteAccess.getRemoteData(url, jsonObject.toString());
            Type collectionType = new TypeToken<List<Reply>>() {
            }.getType();
            replyList = gson.fromJson(jsonIn, collectionType);
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }
}
