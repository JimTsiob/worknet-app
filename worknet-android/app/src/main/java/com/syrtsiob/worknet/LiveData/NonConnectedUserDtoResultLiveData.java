package com.syrtsiob.worknet.LiveData;

import androidx.lifecycle.MutableLiveData;

import com.syrtsiob.worknet.model.EnlargedUserDTO;

public class NonConnectedUserDtoResultLiveData extends MutableLiveData<EnlargedUserDTO> {

    private static NonConnectedUserDtoResultLiveData instance;

    public static NonConnectedUserDtoResultLiveData getInstance() {
        if (instance == null) {
            instance = new NonConnectedUserDtoResultLiveData();
        }
        return instance;
    }
}
