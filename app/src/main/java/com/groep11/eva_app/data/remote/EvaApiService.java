package com.groep11.eva_app.data.remote;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface EvaApiService {
    @GET("users/{user}/tasks")
    Call<List<Task>> listRepos(@Path("user") String userId);
}
