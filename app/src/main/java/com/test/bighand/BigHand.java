package com.test.bighand;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Application class for the BigHand test project. Performs the necessary init on app startup.
 *
 * @author Usman
 */

public class BigHand extends Application {

    private String TAG = "BigHand";

    @Override
    public void onCreate() {
        Log.i(TAG, "App started");
        super.onCreate();
        initRealm();


    }

    /**
     * Inits Realm
     */
    private void initRealm() {

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("bighand.realm").build();
        Realm.setDefaultConfiguration(realmConfig);
    }

}
