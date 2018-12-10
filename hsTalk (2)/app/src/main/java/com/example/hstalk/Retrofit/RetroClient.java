package com.example.hstalk.Retrofit;

import android.content.Context;

import com.example.hstalk.Retrofit.ResponseBody.ResponseGetBoard;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetBoardList;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetComments;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetCommentsUserName;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetInfoByPI;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetInfoByRI;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetPush;
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
    public int chk;
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

    public void getCommentsUserName(String id, final RetroCallback callback){
        apiService.getCommentsUserName(id).enqueue(new Callback<List<ResponseGetCommentsUserName>>() {
            @Override
            public void onResponse(Call<List<ResponseGetCommentsUserName>> call, Response<List<ResponseGetCommentsUserName>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ResponseGetCommentsUserName>> call, Throwable t) {

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



    public void getInfoByPI(String id, final RetroCallback callback){
        apiService.getInfoByPI(id).enqueue(new Callback<List<ResponseGetInfoByPI>>() {
            @Override
            public void onResponse(Call<List<ResponseGetInfoByPI>> call, Response<List<ResponseGetInfoByPI>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ResponseGetInfoByPI>> call, Throwable t) {

            }
        });
    }
    public void getInfoByRI(String id, final RetroCallback callback){
        apiService.getInfoByRI(id).enqueue(new Callback<List<ResponseGetInfoByRI>>() {
            @Override
            public void onResponse(Call<List<ResponseGetInfoByRI>> call, Response<List<ResponseGetInfoByRI>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ResponseGetInfoByRI>> call, Throwable t) {

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

    public void getBoard(int id, final RetroCallback callback){
        apiService.getBoard(id).enqueue(new Callback<ResponseGetBoard>() {
            @Override
            public void onResponse(Call<ResponseGetBoard> call, Response<ResponseGetBoard> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetBoard> call, Throwable t) {

            }
        });
    }

    public void getPush(String id, final RetroCallback callback){
        apiService.getPush(id).enqueue(new Callback<ResponseGetPush>() {
            @Override
            public void onResponse(Call<ResponseGetPush> call, Response<ResponseGetPush> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetPush> call, Throwable t) {

            }
        });
    }

    public void updateEndCall(HashMap<String, Object> parameters, final RetroCallback callback){
        apiService.updateEndCall(parameters).enqueue(new Callback<Void>() {
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
}
