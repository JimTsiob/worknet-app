package com.syrtsiob.worknet.interfaces;

import com.syrtsiob.worknet.model.EducationDTO;
import com.syrtsiob.worknet.model.SkillDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SkillService {

    @POST("skills/")
    Call<String> addSkill(@Body SkillDTO educationDTO, @Query("email") String email);

    @PUT("skills/{id}")
    Call<String> updateSkill(@Path("id") Long skillId, @Body SkillDTO skillDTO);

    @DELETE("skills/{id}")
    Call<String> deleteSkill(@Path("id") Long skillId);
}
