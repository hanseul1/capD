package com.example.hstalk.Retrofit.ResponseBody;

public class ResponseGetCommentsUserName {

    public int postId;
    public final String title;
    public final String writer;
    public final String startTime;
    public final String endTime;
    public final String createTime;
    public final String description;
    public final int freeState;

    public ResponseGetCommentsUserName(int postId, String title, String writer, String startTime, String endTime,
                                       String createTime, String description, int freeState){
        this.postId = postId;
        this.title = title;
        this.writer = writer;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
        this.description = description;
        this.freeState = freeState;
    }
}
