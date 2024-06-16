package com.syrtsiob.worknet.services;

import com.syrtsiob.worknet.model.EducationDTO;
import com.syrtsiob.worknet.model.WorkExperienceDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WorkExperienceService {

    @POST("workexperiences/")
    Call<String> addWorkExperience(@Body WorkExperienceDTO workExperienceDTO, @Query("email") String email);

    @PUT("workexperiences/{id}")
    Call<String> updateWorkExperience(@Path("id") Long workExperienceId, @Body WorkExperienceDTO workExperienceDTO);

    @DELETE("workexperiences/{id}")
    Call<String> deleteWorkExperience(@Path("id") Long workExperienceId);
}
