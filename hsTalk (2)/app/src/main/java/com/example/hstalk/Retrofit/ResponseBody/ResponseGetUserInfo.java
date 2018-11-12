package com.example.hstalk.Retrofit.ResponseBody;

public class ResponseGetUserInfo {

    public final String name;
    public final String userType;
    public final int point;
    public final String deviceId;
    public final String push;
    public final String uid;

    public ResponseGetUserInfo(String name, String userType, int point, String deviceId, String push, String uid) {
       this.name = name;
       this.userType = userType;
       this.point = point;
       this.deviceId = deviceId;
       this.push = push;
       this.uid = uid;
    }
}
