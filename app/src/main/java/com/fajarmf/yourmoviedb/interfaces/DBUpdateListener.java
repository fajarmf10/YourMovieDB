package com.fajarmf.yourmoviedb.interfaces;

/**
 * Created by znaznazna on 12/5/2016.
 */

public interface DBUpdateListener {
    void onSuccess(int operationType);

    void onFailure();
}
