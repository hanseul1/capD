package com.example.hstalk.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.R;

public class BoardActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        TextView Title = (TextView)findViewById(R.id.textView1);
        ImageButton Create = (ImageButton)findViewById(R.id.imageButton1);
        ImageButton Search = (ImageButton)findViewById(R.id.imageButton2);
        Intent intent = getIntent(); // 보내온 Intent를 얻는다
        String type = intent.getStringExtra("title");
        Title.setText(type);
        if(type.equals("자유게시판") ||type.equals("통역 해주세요") || type.equals("통역 해드려요"))
        {
            Create.setVisibility(View.VISIBLE);
            Search.setVisibility(View.VISIBLE);
        }
    } // end of onCreate


}
