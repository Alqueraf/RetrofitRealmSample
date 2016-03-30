package com.alexqueudotrafel.retrofitrealmsample;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by alexqueudotrafel on 16/03/16.
 */
public class MyApp extends Application {

    private static final String TAG = "MyApp";
    private static final boolean D = BuildConfig.DEBUG;

    private static final int REALM_SCHEMA_VERSION = 1;

    private static MyApp instance;


    public MyApp(){
    }

    public static MyApp getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //Inits
        initRealm();
    }

    private void initRealm(){
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .schemaVersion(REALM_SCHEMA_VERSION)
                //.deleteRealmIfMigrationNeeded() // DEBUG PURPOSES
                //.migration(new MyMigration()) // DB PRODUCTION UPDATE (check: https://realm.io/docs/java/latest/#migrations)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    /**
     * Checks if user has internet connectivity
     * @return true if device is internet-connected
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
