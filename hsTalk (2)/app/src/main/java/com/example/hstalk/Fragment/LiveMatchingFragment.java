package com.example.hstalk.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.hstalk.IncomingCallActivity;
import com.example.hstalk.LoginActivity;
import com.example.hstalk.MainActivity;
import com.example.hstalk.MatchingActivity;
import com.example.hstalk.R;
import com.example.hstalk.SignupActivity;
import com.example.hstalk.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.gson.JsonArray;
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

public class LiveMatchingFragment extends Fragment {

    private static String IP_ADDRESS = "52.231.69.121";
    private static final String TAG_JSON = "suhwa";
    private static final String TAG_PUSH = "pushId";
    private static String TAG ="pushtest";
    private String mJsonString;
    private String pushId;
    private EditText title;
    private EditText body;
    private Button button;
    private RadioButton free;
    private RadioButton pay;
    private String payment = "free";
    private Pubnub mPubNub;
    private String user;
    private String stdByChannel;
    private String name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences( Constants.SHARED_PREFS , Context.MODE_PRIVATE);

        View view = (LinearLayout)inflater.inflate(R.layout.fragment_livematching,container,false);
        title = (EditText)view.findViewById(R.id.livematching_title);
        body = (EditText)view.findViewById(R.id.livematching_body);
        button = (Button)view.findViewById(R.id.livematching_button);
        pay = (RadioButton)view.findViewById(R.id.livematching_radiobutton_pay);
        free = (RadioButton)view.findViewById(R.id.livematching_radiobutton_free);

        pay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
              payment = "pay";
            }
        });
        free.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                payment = "free";
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //수화통역사들에게 푸시알람 보냄
                PushNotification push = new PushNotification();
                name = sharedPreferences.getString(Constants.USER_NAME,"");
                user = FirebaseAuth.getInstance().getCurrentUser().getUid();

                stdByChannel = name + Constants.STDBY_SUFFIX;
                push.execute("http://"+IP_ADDRESS+"/push_notification.php",title.getText().toString(),body.getText().toString(),payment,user,name);
                title.setText("");
                body.setText("");
                initPubNub();
                Toast.makeText(getActivity(),"전송완료",Toast.LENGTH_SHORT).show();

            }
        });

       return view;

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

        Intent intent = new Intent(getActivity(), IncomingCallActivity.class);
        intent.putExtra(Constants.USER_NAME, name);
        intent.putExtra(Constants.CALL_USER, userId);
        intent.putExtra("pushId",pushId);
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


    class PushNotification extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "POST response  - " + result);

            if(result != null){
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String serverUrl = (String)strings[0];
            String title = (String)strings[1];
            String body = (String)strings[2];
            String payments = (String)strings[3];
            String uid = (String)strings[4];
            String name = (String)strings[5];

            String postParameters = "title=" + title + "&body=" + body + "&payment=" + payments + "&uid=" + uid  + "&name=" + name;

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

    private void showResult(){
        try{
            if(mJsonString != null){
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                JSONObject item = jsonArray.getJSONObject(0);

                this.pushId = item.getString(TAG_PUSH);
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
