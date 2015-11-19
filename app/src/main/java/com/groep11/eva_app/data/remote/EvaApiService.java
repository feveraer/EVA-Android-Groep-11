package com.groep11.eva_app.data.remote;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface EvaApiService {
    @GET("users/{user}/tasks")
    Call<List<Task>> getTasks(@Path("user") String userId);

    @PUT("users/{user}/tasks/{task}")
    Call<Task> updateTaskStatus(@Path("user") String userId,
                                @Path("task") String taskId,
                                @Body int status);
}
