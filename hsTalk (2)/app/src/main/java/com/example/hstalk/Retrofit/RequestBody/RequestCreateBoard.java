package com.example.hstalk.Retrofit.RequestBody;

import java.util.HashMap;

public class RequestCreateBoard {

    public final String title;
    public final String writeId;
    public final String started_at;
    public final String ended_at;
    public final String created_at;
    public final String description;
    public final int postState;
    public final boolean freeState;



    public RequestCreateBoard(HashMap<String, Object> parameters) {
        this.title = (String)parameters.get("title");
        this.writeId =(String)parameters.get("writeId");
        this.started_at = (String)parameters.get("started_at");
        this.ended_at = (String)parameters.get("ended_at");
        this.created_at = (String)parameters.get("created_at");
        this.description = (String)parameters.get("description");
        this.postState = (int)parameters.get("postState");
        this.freeState = (boolean)parameters.get("freeState");
    }
}
