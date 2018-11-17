package com.example.hstalk.Retrofit;

import android.content.Context;

import com.example.hstalk.Retrofit.ResponseBody.ResponseGetBoardList;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetComments;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetUserInfo;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    private RetroBaseApiService apiService;
    public static String baseUrl = RetroBaseApiService.Base_URL;
    private static Context mContext;
    private static Retrofit retrofit;

    private static class SingletonHolder {
        private static RetroClient INSTANCE = new RetroClient(mContext);
    }

    public static RetroClient getInstance(Context context) {
        if (context != null) {
            mContext = context;
        }
        return SingletonHolder.INSTANCE;
    }

    private RetroClient(Context context) {
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    public RetroClient createBaseApi() {
        apiService = create(RetroBaseApiService.class);
        return this;
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public  <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }





    public void getUserInfo(String id, final RetroCallback callback) {
        apiService.getUserInfo(id).enqueue(new Callback<ResponseGetUserInfo>() {
            @Override
            public void onResponse(Call<ResponseGetUserInfo> call, Response<ResponseGetUserInfo> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.code(),response.body());
                } else{
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetUserInfo> call, Throwable t) {
                    callback.onError(t);
            }
        });

    }

    public void createBoard(HashMap<String, Object> parameters, final RetroCallback callback){
        apiService.CreateBoard(parameters).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) { callback.onError(t);}
        });
    }

    public void getBoardList(final RetroCallback callback){
        apiService.getBoardList().enqueue(new Callback<List<ResponseGetBoardList>>() {
            @Override
            public void onResponse(Call<List<ResponseGetBoardList>> call, Response<List<ResponseGetBoardList>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ResponseGetBoardList>> call, Throwable t) {

            }
        });
    }

    public void commentBoard(HashMap<String, Object> parameters, final RetroCallback callback){
        apiService.commentBoard(parameters).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void getComments(int id, final RetroCallback callback){
        apiService.getComments(id).enqueue(new Callback<List<ResponseGetComments>>() {
            @Override
            public void onResponse(Call<List<ResponseGetComments>> call, Response<List<ResponseGetComments>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ResponseGetComments>> call, Throwable t) {

            }
        });
    }

}
