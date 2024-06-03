package com.syrtsiob.worknet.LiveData;

import androidx.lifecycle.MutableLiveData;

import com.syrtsiob.worknet.model.UserDTO;

public class UserDtoResultLiveData extends MutableLiveData<UserDTO> {
    private static UserDtoResultLiveData instance;

    public static UserDtoResultLiveData getInstance() {
        if (instance == null) {
            instance = new UserDtoResultLiveData();
        }
        return instance;
    }
}
