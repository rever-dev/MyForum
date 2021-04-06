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
import java.util.List;

public class PostBase {

    private static Post post;
    private static List<Integer> likeMemberId;
    private static List<Integer> favMemberId;

    public static Post getPost() {

        return post;
    }

    public static void queryPost(Activity activity, int postId) {
        if (RemoteAccess.networkConnected(activity)) {
            /*
             * 取得Post本體
             * */
            String url = RemoteAccess.URL_SERVER + "PostServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById");
            jsonObject.addProperty("postId", postId);
            String jsonPost = RemoteAccess.getRemoteData(url, jsonObject.toString());
            Type collectionType = new TypeToken<Post>() {
            }.getType();
            post = gson.fromJson(jsonPost, collectionType);
            /*
             * 取得Post 按讚Member列表
             * */
            jsonObject.addProperty("action", "getPostLike");
            jsonObject.addProperty("postId", postId);
            String jsonLike = RemoteAccess.getRemoteData(url, jsonObject.toString());
            collectionType = new TypeToken<List<Integer>>() {
            }.getType();
            likeMemberId = gson.fromJson(jsonLike, collectionType);
            /*
             * 取得Post 收藏Member列表
             * */
            jsonObject.addProperty("action", "getPostFav");
            jsonObject.addProperty("postId", postId);
            String jsonFav = RemoteAccess.getRemoteData(url, jsonObject.toString());
            collectionType = new TypeToken<List<Integer>>() {
            }.getType();
            favMemberId = gson.fromJson(jsonFav, collectionType);

        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }
    /*
    * 檢查是否有在按讚名單內
    * */
    public static boolean checkedLikeStatus(int memberId) {
        boolean checkResult = false;
        if (likeMemberId != null) {
            for (int temp : likeMemberId) {
                if (temp == memberId) {
                    checkResult = true;
                    break;
                }
            }
        }
        return checkResult;
    }
    /*
     * 檢查是否有在收藏名單內
     * */
    public static boolean checkedFavStatus(int memberId) {
        boolean checkResult = false;
        if (favMemberId != null) {
            for (int temp : favMemberId) {
                if (temp == memberId) {
                    checkResult = true;
                    break;
                }
            }
        }
        return checkResult;
    }

    public static void addPostLike(Activity activity, int postId, int likeCount) {
        if (RemoteAccess.networkConnected(activity)) {
            /*
            * 在Post_Like insert新data
            * */
            String url = RemoteAccess.URL_SERVER + "PostServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "insertPostLike");
            jsonObject.addProperty("postId", postId);
            jsonObject.addProperty("memberId", MemberBase.getMember().getId());
            RemoteAccess.getRemoteData(url, jsonObject.toString());
            /*
            * 修改Post likeCount data
            * */
            jsonObject.addProperty("action", "updateCount");
            jsonObject.addProperty("postId", postId);
            jsonObject.addProperty("likeCount", likeCount);
            RemoteAccess.getRemoteData(url, jsonObject.toString());
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }

    public static void deletePostLike(Activity activity, int postId, int likeCount) {
        /*
         * 在Post_Like insert新data
         * */
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "PostServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "deletePostLike");
            jsonObject.addProperty("postId", postId);
            jsonObject.addProperty("memberId", MemberBase.getMember().getId());
            RemoteAccess.getRemoteData(url, jsonObject.toString());
            /*
             * 修改Post likeCount data
             * */
            jsonObject.addProperty("action", "updateCount");
            jsonObject.addProperty("postId", postId);
            jsonObject.addProperty("likeCount", likeCount);
            RemoteAccess.getRemoteData(url, jsonObject.toString());
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }

    public static void addPostFav(Activity activity, int postId) {
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "PostServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "insertPostFav");
            jsonObject.addProperty("postId", postId);
            jsonObject.addProperty("memberId", MemberBase.getMember().getId());
            RemoteAccess.getRemoteData(url, jsonObject.toString());
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }

    public static void deletePostFav(Activity activity, int postId) {
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "PostServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "deletePostFav");
            jsonObject.addProperty("postId", postId);
            jsonObject.addProperty("memberId", MemberBase.getMember().getId());
            RemoteAccess.getRemoteData(url, jsonObject.toString());
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }
}
