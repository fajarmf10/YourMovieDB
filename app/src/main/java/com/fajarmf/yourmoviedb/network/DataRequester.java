package com.fajarmf.yourmoviedb.network;

/**
 * Created by znaznazna on 12/5/2016.
 */

public interface DataRequester {
    void onFailure(Throwable error);

    void onSuccess(Object respObj);
}
