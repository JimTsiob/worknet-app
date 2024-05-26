package com.syrtsiob.worknet.LiveData;

import androidx.lifecycle.MutableLiveData;

public class AuthResultLiveData extends MutableLiveData<Boolean> {

    private static AuthResultLiveData instance;

    public static AuthResultLiveData getInstance() {
        if (instance == null) {
            instance = new AuthResultLiveData();
        }
        return instance;
    }
}
