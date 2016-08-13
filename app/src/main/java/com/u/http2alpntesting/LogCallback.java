package com.u.http2alpntesting;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.Callback;

/**
 * Created by saguilera on 8/13/16.
 */
public class LogCallback implements Callback {
    
    private String tag;
    
    public LogCallback(Object tag) {
        this.tag = tag.getClass().getSimpleName();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.w(tag, "onFailure");

        e.printStackTrace();

        Log.w(tag, "------------------------------------");
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.w(tag, "onSuccess");

        Log.w(tag, "------------------------------------");

        Log.w(tag, response.toString());

        Log.w(tag, "------------------------------------");
        Log.w(tag, "------------------------------------");
        Log.w(tag, "------------------------------------");

        Log.w(tag, "Headers:: " + response.headers().toString());

        Log.w(tag, "------------------------------------");
        Log.w(tag, "------------------------------------");
        Log.w(tag, "------------------------------------");

        Log.w(tag, "Body:: " + response.body().string());

        Log.w(tag, "------------------------------------");
    }
    
}