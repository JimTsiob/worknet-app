package com.syrtsiob.worknet.interfaces;

import com.syrtsiob.worknet.model.EducationDTO;
import com.syrtsiob.worknet.model.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EducationService {
    @POST("educations/")
    Call<String> addEducation(@Body EducationDTO educationDTO, @Query("email") String email);
}
