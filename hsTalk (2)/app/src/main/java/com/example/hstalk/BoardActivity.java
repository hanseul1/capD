package com.example.hstalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.Retrofit.ResponseBody.ResponseGetComments;
import com.example.hstalk.Retrofit.RetroCallback;
import com.example.hstalk.Retrofit.RetroClient;
import com.example.hstalk.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BoardActivity extends AppCompatActivity {

    int postId;
    String title;
    String writer;
    String startTime;
    String endTime;
    String createTime;
    String description;
    int freeState;
    ArrayList<ListItem> listItem = new ArrayList<ListItem>();
    ListView listView;
    EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        TextView titleText = (TextView)findViewById(R.id.boardactivity_textview_title);
        TextView body = (TextView)findViewById(R.id.boardactivity_textview_description);
        listView = (ListView)findViewById(R.id.boardactivity_listview);
        comment = (EditText)findViewById(R.id.boardactivity_edittext_reply);
        Button button = (Button)findViewById(R.id.boardactivity_button_input);

        //listItem.add(new ListItem("한슬","댓글테스트ㅡㅡ으으으","2018-11-17 21:24:00"));

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        postId = extras.getInt("postId");
        title = extras.getString("title");
        writer = extras.getString("writer");
        startTime = extras.getString("startTime");
        endTime = extras.getString("endTime");
        createTime = extras.getString("createTime");
        description = extras.getString("description");
        freeState = extras.getInt("freeState");

        getComments(postId);

        titleText.setText(title);
        body.setText(description);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyStr = comment.getText().toString();
                commentBoard(replyStr);
                comment.setText("");
            }
        });

    }

    protected void getComments(int id){
        RetroClient retroClient = RetroClient.getInstance(this).createBaseApi();
        retroClient.getComments(id, new RetroCallback() {
            @Override
            public void onError(Throwable t) {
                Toast.makeText(BoardActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                List<ResponseGetComments> data = (List<ResponseGetComments>) receivedData;
                for(int i=0; i<data.size(); i++){
                    listItem.add(new ListItem(data.get(i).writeId, data.get(i).body, data.get(i).created_at));
                }

                MyAdapter adapter = new MyAdapter(BoardActivity.this, R.layout.item_reply, listItem);
                listView.setAdapter(adapter);

            }

            @Override
            public void onFailure(int code) {
                Toast.makeText(BoardActivity.this,"fail",Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void commentBoard(String comment){
        HashMap<String, Object> data = new HashMap<>();
        SharedPreferences sharedPreferences = getSharedPreferences( Constants.SHARED_PREFS , MODE_PRIVATE);
        String name = sharedPreferences.getString(Constants.USER_NAME,"");
        data.put("writeId", name);
        data.put("postId", postId);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String createdAt =  df.format(new Date());
        data.put("created_at", createdAt);
        data.put("body", comment);

        RetroClient retroClient = RetroClient.getInstance(this).createBaseApi();
        retroClient.commentBoard(data, new RetroCallback() {
            @Override
            public void onError(Throwable t) {
                Toast.makeText(BoardActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                Toast.makeText(BoardActivity.this,"댓글 작성 완료",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int code) {
                Toast.makeText(BoardActivity.this,"fail",Toast.LENGTH_SHORT).show();
            }
        });


    }
}
class MyAdapter extends BaseAdapter { // 리스트 뷰의 아답타
    Context context;
    int layout;
    ArrayList<ListItem> listItem;
    LayoutInflater inf;
    public MyAdapter(Context context, int layout, ArrayList<ListItem> listItem) {
        this.context = context;
        this.layout = layout;
        this.listItem = listItem;
        inf = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return listItem.size();
    }
    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = inf.inflate(layout, null);
        }

        TextView name = (TextView)convertView.findViewById(R.id.replyitem_name);
        TextView body = (TextView)convertView.findViewById(R.id.replyitem_content);
        TextView time = (TextView)convertView.findViewById(R.id.replyitem_time);
        ListItem m = listItem.get(position);
        name.setText(m.name);
        body.setText(m.body);
        time.setText(m.time);
        return convertView;
    }
}


class ListItem{
    String name = "";
    String body = "";
    String time = "";

    public ListItem(String name, String body, String time){
        super();
        this.name = name;
        this.body = body;
        this.time = time;
    }

}
