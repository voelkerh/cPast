package com.voelkerh.cPast;

import android.app.Application;
import com.voelkerh.cPast.di.AppModule;

public class CPastApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppModule.init(this);
    }
}
