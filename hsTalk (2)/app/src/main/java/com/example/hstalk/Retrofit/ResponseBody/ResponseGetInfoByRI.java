package com.example.hstalk.Retrofit.ResponseBody;

public class ResponseGetInfoByRI {
    public final int serviceId;
    public final String name;
    public final String started_at;
    public ResponseGetInfoByRI(int serviceId, String name, String started_at){
        this.serviceId = serviceId;
        this.name = name;
        this.started_at = started_at;
    }
}
