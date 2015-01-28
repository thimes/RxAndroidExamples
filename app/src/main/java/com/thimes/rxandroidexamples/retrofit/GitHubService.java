package com.thimes.rxandroidexamples.retrofit;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by thimes on 1/27/15.
 */
public interface GitHubService {
    @GET("/repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    @GET("/repos/{owner}/{repo}/contributors")
    void contributorsAsync(
            @Path("owner") String owner,
            @Path("repo") String repo,
            Callback<List<Contributor>> contributors
    );

    @GET("/repos/{owner}/{repo}/contributors")
    Observable<List<Contributor>> getContributorsObservable(
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    @GET("/users/{user}")
    Observable<User> getContributorsObservable(
            @Path("user") String user
    );
}
