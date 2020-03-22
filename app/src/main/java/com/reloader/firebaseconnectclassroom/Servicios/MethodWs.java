package com.reloader.firebaseconnectclassroom.Servicios;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by Reloader on 16/03/2020.
 */


public interface MethodWs {

    @GET("courses?")
    Call<ResponseBody> getCursosMail(
            @Query("courseStates") String courseStates,
            @Query("studentId") String studentId,
            @Query("teacherId") String teacherId,
            @Query("access_token") String access_token);


}

