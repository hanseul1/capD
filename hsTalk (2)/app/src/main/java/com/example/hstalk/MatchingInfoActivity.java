package com.example.hstalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class  MatchingInfoActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "52.231.69.121";
    private static String TAG ="pushtest";
    private Pubnub mPubNub;
    private String user,user2,uid;
    private String stdByChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchinginfo);
        String type,uid,date;
        SharedPreferences sharedPreferences = getSharedPreferences( Constants.SHARED_PREFS , MODE_PRIVATE);
        type = sharedPreferences.getString("userType","");
        uid = sharedPreferences.getString("uid","");
        user = sharedPreferences.getString(Constants.USER_NAME,"");
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
                user2 = String.valueOf(data.name);
                name.setText(user2);


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
                user2 = String.valueOf(data.name);
                name.setText( user2);

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
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PushNotification push = new PushNotification();
                        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        stdByChannel = user + Constants.STDBY_SUFFIX;
                        Log.d("TAG", user);
                        Log.d("TAG", stdByChannel);
                        push.execute("http://"+IP_ADDRESS+"/resmatching_notification.php","예약 매칭 통화 알림","예약 매칭된 상대의 통화 요청입니다.",user2,user);
                        initPubNub();
                        Toast.makeText(MatchingInfoActivity.this,"전송완료",Toast.LENGTH_SHORT).show();

                    }
                });
        }
        else{
               accept.setEnabled(false);
        }


    }

    public void initPubNub(){
        this.mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        this.mPubNub.setUUID(user);
        subscribeStdBy();
    }

    private void subscribeStdBy(){

        try {
            this.mPubNub.subscribe(this.stdByChannel, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-iPN", "MESSAGE: " + message.toString());
                    if (!(message instanceof JSONObject)) return; // Ignore if not JSONObject
                    JSONObject jsonMsg = (JSONObject) message;
                    try {
                        if (!jsonMsg.has(Constants.JSON_CALL_USER)) return;     //Ignore Signaling messages.
                        String user = jsonMsg.getString(Constants.JSON_CALL_USER);

                        dispatchIncomingCall(user);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("MA-iPN", "CONNECTED: " + message.toString());
                    setUserStatus(Constants.STATUS_AVAILABLE);
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("MA-iPN","ERROR: " + error.toString());
                }
            });
        } catch (PubnubException e){
            Log.d("HERE","HEREEEE");
            e.printStackTrace();
        }
    }
    private void dispatchIncomingCall(String userId){

        Intent intent = new Intent(MatchingInfoActivity.this, IncomingCallActivity.class);
        intent.putExtra(Constants.USER_NAME, user);
        intent.putExtra(Constants.CALL_USER, userId);
        startActivity(intent);
    }


    private void setUserStatus(String status){
        try {
            JSONObject state = new JSONObject();
            state.put(Constants.JSON_STATUS, status);
            this.mPubNub.setState(this.stdByChannel, user, state, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("MA-sUS","State Set: " + message.toString());
                }
            });
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


    class PushNotification extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... strings) {

            String serverUrl = (String)strings[0];
            String title = (String)strings[1];
            String body = (String)strings[2];
            String name = (String)strings[3];
            String uid = (String)strings[4];

            String postParameters = "title=" + title + "&body=" + body +  "&name=" + name +"&uid=" + uid ;

            try{
                URL url = new URL(serverUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                httpURLConnection.setReadTimeout(100000000);
                httpURLConnection.setConnectTimeout(100000000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();
            }catch (Exception e){
                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }
        }
    }



}
