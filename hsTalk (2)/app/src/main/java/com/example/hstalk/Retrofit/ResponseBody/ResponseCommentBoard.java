package com.example.hstalk.Retrofit.ResponseBody;

import java.util.HashMap;

public class ResponseCommentBoard {

    public final String writeId;
    public final int postId;
    public final String created_at;
    public final String body;

    public ResponseCommentBoard(HashMap<String, Object> parameters){
        this.writeId = (String)parameters.get("writeId");
        this.postId = (int)parameters.get("postId");
        this.created_at = (String)parameters.get("created_at");
        this.body = (String)parameters.get("body");
    }

}
