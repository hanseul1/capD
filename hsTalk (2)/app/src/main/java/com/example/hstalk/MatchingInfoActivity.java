package com.example.hstalk;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.Retrofit.ResponseBody.ResponseGetStartDateByPI;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetStartDateByRI;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetUserInfo;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetUserNameByPI;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetUserNameByRI;
import com.example.hstalk.Retrofit.RetroCallback;
import com.example.hstalk.Retrofit.RetroClient;
import com.example.hstalk.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MatchingInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchinginfo);
        String type,uid,date;
        SharedPreferences sharedPreferences = getSharedPreferences( Constants.SHARED_PREFS , MODE_PRIVATE);
        type = sharedPreferences.getString("userType","");
        uid = sharedPreferences.getString("uid","");
        //started_at 받아올 변수
        if( type.equals("E")) {
            //providerId 를 가지고 있는 사람의 이름
            getStartDateByPI(uid);
            getUserNameByPI(uid);


        }
        else if( type.equals("V")) {
            //providerId 를 가지고 있는 사람의 이름
            getStartDateByPI(uid);
            getUserNameByPI(uid);


        }
        else
        {
            getStartDateByRI(uid);
            getUserNameByRI(uid);
            //recevedid 를 가지고 있는 사람의 이름
        }

    }

    protected  void getStartDateByPI(String id){


        RetroClient retroClient = RetroClient.getInstance(this).createBaseApi();
        retroClient.getStartDateByPI(id, new RetroCallback() {
            @Override
            public void onError(Throwable t) {
                Toast.makeText(MatchingInfoActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int code, Object receivedData) {

                ResponseGetStartDateByPI data = (ResponseGetStartDateByPI) receivedData;
                TextView date = (TextView)findViewById(R.id.matchinginfo_date);
                date.setText( String.valueOf(data.started_at));
                try {
                    getDiffTime(data.started_at);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int code) {
                Toast.makeText(MatchingInfoActivity.this,"fail",Toast.LENGTH_SHORT).show();

            }
        });
    }
    protected  void getStartDateByRI(String id){

        RetroClient retroClient = RetroClient.getInstance(this).createBaseApi();
        retroClient.getStartDateByRI(id, new RetroCallback() {
            @Override
            public void onError(Throwable t) {
                Toast.makeText(MatchingInfoActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                ResponseGetStartDateByRI data = (ResponseGetStartDateByRI) receivedData;
                TextView date = (TextView)findViewById(R.id.matchinginfo_date);
                date.setText( String.valueOf(data.started_at));
                try {
                    getDiffTime(data.started_at);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int code) {
                Toast.makeText(MatchingInfoActivity.this,"fail",Toast.LENGTH_SHORT).show();

            }
        });
    }
    protected  void getUserNameByPI(String id){


        RetroClient retroClient = RetroClient.getInstance(this).createBaseApi();
        retroClient.getUserNameByPI(id, new RetroCallback() {
            @Override
            public void onError(Throwable t) {
                Toast.makeText(MatchingInfoActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int code, Object receivedData) {

                ResponseGetUserNameByPI data = (ResponseGetUserNameByPI) receivedData;
                TextView name = (TextView)findViewById(R.id.matchinginfo_name);
                name.setText( String.valueOf(data.name));

            }

            @Override
            public void onFailure(int code) {
                Toast.makeText(MatchingInfoActivity.this,"fail",Toast.LENGTH_SHORT).show();

            }
        });
    }
    protected  void getUserNameByRI(String id){


        RetroClient retroClient = RetroClient.getInstance(this).createBaseApi();
        retroClient.getUserNameByRI(id, new RetroCallback() {
            @Override
            public void onError(Throwable t) {
                Toast.makeText(MatchingInfoActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int code, Object receivedData) {

                ResponseGetUserNameByRI data = (ResponseGetUserNameByRI) receivedData;
                TextView name = (TextView)findViewById(R.id.matchinginfo_name);
                name.setText( String.valueOf(data.name));

            }

            @Override
            public void onFailure(int code) {
                Toast.makeText(MatchingInfoActivity.this,"fail",Toast.LENGTH_SHORT).show();

            }
        });
    }

    protected void getDiffTime(String startTime) throws ParseException {


        Button accept = (Button)findViewById(R.id.matchinginfo_button);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = sdf.format(date);
        startTime = "2018-11-20 05:20:12";


        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowDate = f.parse(getTime);
        Date startDate = f.parse(startTime);
        long diff = startDate.getTime() - nowDate.getTime();
        long min = diff / 60000;


        if(min<30){
                accept.setClickable(true);
                accept.setBackgroundColor(Color.GREEN);
        }
        else{
                accept.setClickable(false);
        }


       accept.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

           }
       });

    }
}
