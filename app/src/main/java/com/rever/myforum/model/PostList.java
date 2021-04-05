package com.rever.myforum.model;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.rever.myforum.R;
import com.rever.myforum.bean.Post;
import com.rever.myforum.network.RemoteAccess;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PostList {

    private static List<Post> postList;

    public static List<Post> getPostList() {
        if (postList == null) {
            postList = new ArrayList<>();
        }
        return postList;
    }

    public static void queryPostListByType(Activity activity, String type, String sort) {
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "PostServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "query");
            jsonObject.addProperty("type", type);
            jsonObject.addProperty("sort", sort);
            String jsonIn = RemoteAccess.getRemoteData(url, jsonObject.toString());
            Type collectionType = new TypeToken<List<Post>>() {
            }.getType();
            postList = gson.fromJson(jsonIn, collectionType);
            } else {
                Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
            }
    }

}
