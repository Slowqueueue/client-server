package com.labscs.services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public class AppService {
    private NetworkService networkService_;

    private interface NetworkService {
        @GET("/object/list")
        Call<Results> getObjectList();

        @GET("/object/img")
        Call<Results> requestImage(@Query("cmd") String msg);

        @GET("/object/text")
        Call<Results> requestText(@Query("cmd") String msg);

        @POST("/object/add")
        Call<Results> sendObject(@Query("cmd") String msg);

        @DELETE("/object/delete")
        Call<Results> deleteObject(@Query("cmd") String msg);
    }

    public class Results {
        @SerializedName("data")
        private String data_;

        public Results(String data) {
            this.data_ = data;
        }

        public String getData() {
            return this.data_;
        }
    }

    public AppService() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http:localhost:4545")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        this.networkService_ = retrofit.create(NetworkService.class);
    }

    public String getObjectList() {
        String backMessage = "";
        Call<Results> call = this.networkService_.getObjectList();

        try {
            Response<Results> res = call.execute();

            backMessage += res.isSuccessful()
                    ? res.body().getData()
                    : res.code();

        } catch (Exception err) {
            backMessage += err.getMessage();
            err.printStackTrace();
        }

        return backMessage;
    }

    public String sendObject(String obj) {
        String backMessage = "";
        Call<Results> call = this.networkService_.sendObject(obj);

        try {
            Response<Results> res = call.execute();

            backMessage += res.isSuccessful()
                    ? res.body().getData()
                    : res.code();

        } catch (Exception err) {
            backMessage += err.getMessage();
            err.printStackTrace();
        }

        return backMessage;
    }

    public String requestImage(String idx) {
        String backMessage = "";
        Call<Results> call = this.networkService_.requestImage(idx);

        try {
            Response<Results> res = call.execute();

            backMessage += res.isSuccessful()
                    ? res.body().getData()
                    : res.code();

        } catch (Exception err) {
            backMessage += err.getMessage();
            err.printStackTrace();
        }

        return backMessage;
    }

    public String requestText(String idx) {
        String backMessage = "";
        Call<Results> call = this.networkService_.requestText(idx);

        try {
            Response<Results> res = call.execute();

            backMessage += res.isSuccessful()
                    ? res.body().getData()
                    : res.code();

        } catch (Exception err) {
            backMessage += err.getMessage();
            err.printStackTrace();
        }

        return backMessage;
    }

    public String deleteObject(String obj) {
        String backMessage = "";
        Call<Results> call = this.networkService_.deleteObject(obj);

        try {
            Response<Results> res = call.execute();

            backMessage += res.isSuccessful()
                    ? res.body().getData()
                    : res.code();

        } catch (Exception err) {
            backMessage += err.getMessage();
            err.printStackTrace();
        }

        return backMessage;
    }

}
