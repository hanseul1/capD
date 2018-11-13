package com.example.hstalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MatchingActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "52.231.69.121";
    private static String TAG ="pushtest";
    private static final String TAG_JSON = "getpush";
    private static final String TAG_PUSH = "pushId";
    private static final String TAG_PROVIDER = "providerId";

    private SharedPreferences sharedPreferences;
    private String stdByChannel;
    private String user;
    private String title;
    private String body;
    private String receiver;
    private Pubnub mPubNub;

    String mJsonString;
    String pushId;
    String providerId;
    TextView doneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        user = sharedPreferences.getString(Constants.USER_NAME,"");
        title = bundle.getString("title");
        body = bundle.getString("body");
        receiver = bundle.getString("receiver");
        stdByChannel = user + Constants.STDBY_SUFFIX;

        Log.d("태그", receiver);
        Log.d("태그", user);

        TextView titleText = (TextView)findViewById(R.id.matchingactivity_textview_title);
        TextView bodyText = (TextView)findViewById(R.id.matchingactivity_textview_body);
        Button button = (Button)findViewById(R.id.matchingactivity_button);
        doneText = (TextView)findViewById(R.id.matchingactivity_textview_donetext);
        titleText.setText(title);
        bodyText.setText(body);

        GetPush push = new GetPush();
        push.execute("http://" + IP_ADDRESS + "/getPush.php",title,body);

        initPubNub();

        if(providerId == "none"){
            button.setVisibility(View.INVISIBLE);
        }else{
            doneText.setVisibility(View.INVISIBLE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //service 테이블에 providerId 저장 및 푸시 전달
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                UpdateProvider task = new UpdateProvider();
                task.execute("http://" + IP_ADDRESS + "/matching_complete.php",uid,pushId);

                makeCall(v);

                Toast.makeText(MatchingActivity.this,"매칭완료",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(this.mPubNub==null){
            initPubNub();
        } else {
            subscribeStdBy();
        }
    }

    class GetPush extends AsyncTask<String,Void,String> {

        @Override

        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            if (result != null){
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String serverUrl = (String)strings[0];
            String title = (String)strings[1];
            String body = (String)strings[2];

            String postParameters = "title=" + title + "&body=" + body;

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);

                return null;

            }

        }
    }

    private void showResult(){
        try {

            if(mJsonString != null) {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                JSONObject item = jsonArray.getJSONObject(0);

                pushId = item.getString(TAG_PUSH);
                providerId = item.getString(TAG_PROVIDER);
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    class UpdateProvider extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            String serverUrl = (String)strings[0];
            String uid = (String)strings[1];
            String pushId2 = (String)strings[2];

            String postParameters = "uid=" + uid + "&pushId=" + pushId2;

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return null;
            }
        }
    }

    public void makeCall(View view){
        String callNum = receiver;
        if (callNum.isEmpty() || callNum.equals(user)){
            showToast("Enter a valid user ID to call.");
            return;
        }
        else
            showToast("Hi");
        dispatchCall(callNum);
    }

    private void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MatchingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initPubNub(){
        this.mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        this.mPubNub.setUUID(user);
        subscribeStdBy();
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

    private void dispatchIncomingCall(String userId){
        showToast("Call from: " + userId);
        Intent intent = new Intent(MatchingActivity.this, IncomingCallActivity.class);
        intent.putExtra(Constants.USER_NAME, user);
        intent.putExtra(Constants.CALL_USER, userId);
        startActivity(intent);
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
                        showToast("calling");
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

    public void dispatchCall(final String callNum){
        final String callNumStdBy = callNum + Constants.STDBY_SUFFIX;
        this.mPubNub.hereNow(callNumStdBy, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.d("MA-dC", "HERE_NOW: " +" CH - " + callNumStdBy + " " + message.toString());
                try {
                    int occupancy = ((JSONObject) message).getInt(Constants.JSON_OCCUPANCY);
                    if (occupancy == 0) {
                        showToast("User is not online!");
                        return;
                    }
                    JSONObject jsonCall = new JSONObject();
                    jsonCall.put(Constants.JSON_CALL_USER, user);
                    jsonCall.put(Constants.JSON_CALL_TIME, System.currentTimeMillis());
                    mPubNub.publish(callNumStdBy, jsonCall, new Callback() {
                        @Override
                        public void successCallback(String channel, Object message) {
                            Log.d("MA-dC", "SUCCESS: " + message.toString());
                            Intent intent = new Intent(MatchingActivity.this, VideoChatActivity.class);
                            intent.putExtra(Constants.USER_NAME, user);
                            intent.putExtra(Constants.CALL_USER, callNum);  // Only accept from this number?
                            intent.putExtra("dialed", true);
                            startActivity(intent);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
