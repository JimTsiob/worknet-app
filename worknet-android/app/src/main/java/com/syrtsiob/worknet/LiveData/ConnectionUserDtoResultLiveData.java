package com.syrtsiob.worknet.LiveData;

import androidx.lifecycle.MutableLiveData;

import com.syrtsiob.worknet.model.ConnectionDTO;

public class ConnectionUserDtoResultLiveData extends MutableLiveData<ConnectionDTO> {
    private static ConnectionUserDtoResultLiveData instance;

    public static ConnectionUserDtoResultLiveData getInstance() {
        if (instance == null) {
            instance = new ConnectionUserDtoResultLiveData();
        }
        return instance;
    }
}
