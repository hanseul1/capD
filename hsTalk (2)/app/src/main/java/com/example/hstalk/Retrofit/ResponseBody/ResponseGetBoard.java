package com.example.hstalk.Retrofit.ResponseBody;

public class ResponseGetBoard {
    public final String title;
    public final String writeId;
    public final String started_at;
    public final String ended_at;
    public final String created_at;
    public final String description;
    public final int postState;
    public final int freeState;

    public ResponseGetBoard(String title, String writeId, String started_at, String ended_at, String created_at, String description,
                            int postState, int freeState) {
        this.title = title;
        this.writeId = writeId;
        this.started_at = started_at;
        this.ended_at = ended_at;
        this.created_at = created_at;
        this.description = description;
        this.postState = postState;
        this.freeState = freeState;
    }

}
