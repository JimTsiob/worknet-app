package com.syrtsiob.worknet.LiveData;

import androidx.lifecycle.MutableLiveData;

public class RegisterResultLiveData extends MutableLiveData<Boolean> {
    private static RegisterResultLiveData instance;

    public static RegisterResultLiveData getInstance() {
        if (instance == null) {
            instance = new RegisterResultLiveData();
        }
        return instance;
    }
}
