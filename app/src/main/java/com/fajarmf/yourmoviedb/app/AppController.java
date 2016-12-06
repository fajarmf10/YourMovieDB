package com.fajarmf.yourmoviedb.app;

import android.app.Application;
import android.util.Log;
/**
 * Created by znaznazna on 12/5/2016.
 */
import com.fajarmf.yourmoviedb.network.DataManager;
public class AppController extends Application{
    private static final String TAG = AppController.class
            .getSimpleName();
    // Different Managers
    private DataManager mDataMan;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "App started");
        initApp();
    }

    private void initApp() {
        mDataMan = DataManager.getInstance(AppController.this);
        mDataMan.init();
    }

    /**
     * Get the data manager instance
     */
    public synchronized DataManager getDataManager() {
        return mDataMan;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mDataMan != null) {
            mDataMan.terminate();
        }
    }

}
