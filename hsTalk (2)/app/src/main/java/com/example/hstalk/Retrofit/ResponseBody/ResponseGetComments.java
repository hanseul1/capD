package com.example.hstalk.Retrofit.ResponseBody;

public class ResponseGetComments {

    public final String writeId;
    public final String created_at;
    public final String body;

    public ResponseGetComments(String writeId, String created_at, String body){
        this.writeId = writeId;
        this.created_at = created_at;
        this.body = body;
    }
}
