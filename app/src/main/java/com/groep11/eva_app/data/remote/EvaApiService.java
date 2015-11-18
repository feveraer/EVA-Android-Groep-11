package com.groep11.eva_app.data.remote;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface EvaApiService {
    @GET("users/{user}/tasks")
    Call<List<Task>> listRepos(@Path("user") String userId);

    @POST("authenticate")
    Call<TokenResponse> getToken(@Body User user);

    @POST("users")
    Call<TokenResponse> register(@Body User user);
}
