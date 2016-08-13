package com.u.http2alpntesting;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Check OkHttp::Platform for a lot of useful information (such as how alpn is detected or that
 * in tests its not supported, etc).
 *
 * Check jetty in mortbay and eclipse. Be very careful that its developed for java 8, so we have to
 * use older versions.
 * Eclipse supports client version and was last updated on april 2016 (3 months ago , nice) but
 * its not used by okhttp it seems, so meh
 *
 * Mortbad has as dependency the eclipse alpn-api version (which runs on java 8) but ported for java 7,
 * version is a bit old (x/7/2015), but its the best I could find around. Also okhttp seems to favour
 * him.
 *
 * Created by saguilera on 8/13/16.
 */
public class GetRunnable implements Runnable {

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
