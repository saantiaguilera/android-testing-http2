package com.u.http2alpntesting;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by saguilera on 8/13/16.
 */
public class H1GetRunnable implements Runnable {

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://www.google.com.ar/")
                .header("User-Agent", "Chrome/23.0.1271.95")
                .get()
                .build();

        client.newCall(request).enqueue(new LogCallback(this));
    }

}
