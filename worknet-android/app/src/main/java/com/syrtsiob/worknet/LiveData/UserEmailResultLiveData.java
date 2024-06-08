package com.syrtsiob.worknet.LiveData;

import androidx.lifecycle.MutableLiveData;

import com.syrtsiob.worknet.model.UserDTO;

public class UserEmailResultLiveData extends MutableLiveData<Long> {
    private static UserEmailResultLiveData instance;

    public static UserEmailResultLiveData getInstance() {
        if (instance == null) {
            instance = new UserEmailResultLiveData();
        }
        return instance;
    }
}
