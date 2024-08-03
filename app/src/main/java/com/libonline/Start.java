package com.libonline;

import android.content.Context;

import com.libonline.system.Startup;

public class Start {
    public static void Startup(Context context) {
        Startup start = new Startup();
        start.StartLoading(context);
    }
}
