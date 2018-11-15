package com.example.hstalk.Retrofit.ResponseBody;

import java.util.HashMap;

public class ResponseCreateBoard {

    public final String title;
    public final String writeId;
    public final String started_at;
    public final String ended_at;
    public final String created_at;
    public final String description;
    public final int postState;
    public final boolean freeState;

//    public ResponseCreateBoard(String title, String wrtieId, String started_at, String ended_at, String created_at, String description, int postState, boolean freeState) {
//        this.title = title;
//        this.writeId = wrtieId;
//        this.started_at = started_at;
//        this.ended_at = ended_at;
//        this.created_at = created_at;
//        this.description = description;
//        this.postState = postState;
//        this.freeState = freeState;
//    }

    public ResponseCreateBoard(HashMap<String, Object> parameters) {
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
