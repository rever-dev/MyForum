package com.rever.myforum.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class RemoteAccess {
    private static final String TAG = "TAG_RemoteAccess";

    //        public static String URL_SERVER = "http://192.168.0.12:8080/MyForum/";
    public static String URL_SERVER = "http://10.0.2.2:8080/Spot_MySQL_Web/";


    public static String getRemoteData(String url, String outStr) {
        JsonCallable callable = new JsonCallable(url, outStr);
        FutureTask<String> task = new FutureTask<>(callable);
        Thread thread = new Thread(task);
        thread.start();
        try {
            return task.get();
        } catch (Exception e) {
            Log.e(TAG, "getRemoteData(): " + e.toString());
            task.cancel(true);
            return "";
        }
    }

    // 適用取得一張圖
    public static Bitmap getRemoteImage(String url, String outStr) {
        ImageCallable callable = new ImageCallable(url, outStr);
        FutureTask<Bitmap> task = new FutureTask<>(callable);
        Thread thread = new Thread(task);
        thread.start();
        try {
            return task.get();
        } catch (Exception e) {
            Log.e(TAG, "getRemoteImage(): " + e.toString());
            task.cancel(true);
            return null;
        }
    }

    // 搭配Executor取圖
    public static Bitmap getRemoteImage(String url, String outStr, ExecutorService executor) {
        ImageCallable callable = new ImageCallable(url, outStr);
        Future<Bitmap> future = executor.submit(callable);
        Bitmap bitmap = null;
        try {
            bitmap = future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "future.get(): " + e.toString());
            future.cancel(true);
        }
        return bitmap;
    }

    // 檢查是否有網路連線
    public static boolean networkConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // API 23支援getActiveNetwork()
                Network network = connectivityManager.getActiveNetwork();
                // API 21支援getNetworkCapabilities()
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                if (networkCapabilities != null) {
                    String msg = String.format(Locale.getDefault(),
                            "TRANSPORT_WIFI: %b%nTRANSPORT_CELLULAR: %b%nTRANSPORT_ETHERNET: %b%n",
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
                    Log.d(TAG, msg);
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                // API 29將NetworkInfo列為deprecated
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        return false;
    }
}