package com.syrtsiob.worknet.LiveData;

import androidx.lifecycle.MutableLiveData;

import com.syrtsiob.worknet.model.ApplicantDTO;
import com.syrtsiob.worknet.model.SmallUserDTO;

public class ApplicantUserDtoResultLiveData extends MutableLiveData<ApplicantDTO> {

    private static ApplicantUserDtoResultLiveData instance;

    public static ApplicantUserDtoResultLiveData getInstance() {
        if (instance == null) {
            instance = new ApplicantUserDtoResultLiveData();
        }
        return instance;
    }
}
