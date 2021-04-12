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
import com.rever.myforum.network.RemoteAccess;

import java.lang.reflect.Type;
import java.util.List;

public class ReplyBase {


    /*
     * 新增Reply data
     * */
    public static int insertReply(Activity activity, int postId, String content) {
        int replyId = 0;
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "ReplyServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "insert");
            jsonObject.addProperty("memberId", MemberBase.getMember().getId());
            jsonObject.addProperty("postId", postId);
            jsonObject.addProperty("memberNickname", MemberBase.getMember().getNickname());
            jsonObject.addProperty("content", content);
            String jsonIn = RemoteAccess.getRemoteData(url, jsonObject.toString());
            replyId = Integer.parseInt(jsonIn);
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
        return replyId;
    }
    /*
    * 刪除Reply
    * */
    public static void deleteReply(Activity activity, int replyId) {
        if (RemoteAccess.networkConnected(activity)) {
            String url = RemoteAccess.URL_SERVER + "ReplyServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "delete");
            jsonObject.addProperty("replyId", replyId);
            RemoteAccess.getRemoteData(url, jsonObject.toString());
            Toast.makeText(activity, R.string.toast_deleteReplySuccess, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * 檢查是否有在按讚名單內
     * */
    public static boolean checkedLikeStatus(int replyId, int memberId) {
        boolean checkResult = false;
        List<Integer> likeMemberId;
        /*
         * 取得Reply 按讚Member列表
         * */
        String url = RemoteAccess.URL_SERVER + "ReplyServlet";
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getReplyLike");
        jsonObject.addProperty("replyId", replyId);
        String jsonLike = RemoteAccess.getRemoteData(url, jsonObject.toString());
        Type collectionType = new TypeToken<List<Integer>>() {
        }.getType();
        likeMemberId = gson.fromJson(jsonLike, collectionType);

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

    public static void addReplyLike(Activity activity, int replyId, int postId, int likeCount) {
        if (RemoteAccess.networkConnected(activity)) {
            /*
             * 在Reply_Like insert新data
             * */
            String url = RemoteAccess.URL_SERVER + "ReplyServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "insertReplyLike");
            jsonObject.addProperty("replyId", replyId);
            jsonObject.addProperty("memberId", MemberBase.getMember().getId());
            jsonObject.addProperty("postId", postId);
            RemoteAccess.getRemoteData(url, jsonObject.toString());
            /*
             * 修改Reply likeCount data
             * */
            jsonObject.addProperty("action", "updateCount");
            jsonObject.addProperty("replyId", replyId);
            jsonObject.addProperty("likeCount", likeCount);
            RemoteAccess.getRemoteData(url, jsonObject.toString());
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteReplyLike(Activity activity, int replyId, int postId, int likeCount) {
        if (RemoteAccess.networkConnected(activity)) {
            /*
             * 刪除在Reply_Like 的data
             * */
            String url = RemoteAccess.URL_SERVER + "ReplyServlet";
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "deleteReplyLike");
            jsonObject.addProperty("replyId", replyId);
            jsonObject.addProperty("memberId", MemberBase.getMember().getId());
            jsonObject.addProperty("postId", postId);
            RemoteAccess.getRemoteData(url, jsonObject.toString());
            /*
             * 修改Reply likeCount data
             * */
            jsonObject.addProperty("action", "updateCount");
            jsonObject.addProperty("replyId", replyId);
            jsonObject.addProperty("likeCount", likeCount);
            RemoteAccess.getRemoteData(url, jsonObject.toString());
        } else {
            Toast.makeText(activity, R.string.toast_noNetWork, Toast.LENGTH_SHORT).show();
        }
    }
}
