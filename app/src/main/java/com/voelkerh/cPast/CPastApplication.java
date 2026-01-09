package com.voelkerh.cPast;

import android.app.Application;
import com.voelkerh.cPast.di.AppModule;

/**
 * Application entry point for the cPast app.
 *
 * <p>This class is responsible for the initialization of the dependency graph via {@link AppModule}.</p>
 *
 * <p>No UI or domain logic should be placed here.</p>
 */
public class CPastApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppModule.init(this);
    }
}
