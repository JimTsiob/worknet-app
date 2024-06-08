package com.syrtsiob.worknet.interfaces;

import com.syrtsiob.worknet.model.EducationDTO;
import com.syrtsiob.worknet.model.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EducationService {
    @POST("educations/")
    Call<String> addEducation(@Body EducationDTO educationDTO, @Query("email") String email);

    @PUT("educations/{id}")
    Call<String> updateEducation(@Path("id") Long educationId, @Body EducationDTO educationDTO);

    @DELETE("educations/{id}")
    Call<String> deleteEducation(@Path("id") Long educationId);
}
