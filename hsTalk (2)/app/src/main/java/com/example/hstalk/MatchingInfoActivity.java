package com.example.hstalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.Retrofit.ResponseBody.ResponseGetInfoByPI;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetInfoByRI;
import com.example.hstalk.Retrofit.RetroCallback;
import com.example.hstalk.Retrofit.RetroClient;
import com.example.hstalk.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class  MatchingInfoActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "52.231.69.121";
    private static final String TAG_JSON = "suhwa";
    private static final String TAG_PUSH = "pushId";
    private static String TAG ="pushtest";
    private String mJsonString;
    private String pushId;
    private Pubnub mPubNub;
    private String user;
    private String stdByChannel;
    List<ResponseGetInfoByPI> dataPI;
    List<ResponseGetInfoByRI> dataRI;
    ArrayList<InfoItem> infoitem = new ArrayList<InfoItem>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchinginfo);
        String type,uid,date;
        SharedPreferences sharedPreferences = getSharedPreferences( Constants.SHARED_PREFS , MODE_PRIVATE);
        type = sharedPreferences.getString("userType","");
        uid = sharedPreferences.getString("uid","");
        user = sharedPreferences.getString(Constants.USER_NAME,"");
        listView = (ListView)findViewById(R.id.matchinginfo_listview);

        //started_at 받아올 변수
        if( type.equals("E")) {
            //providerId 를 가지고 있는 사람의 이름
            getInfoByPI(uid);


        }
        else if( type.equals("V")) {
            //providerId 를 가지고 있는 사람의 이름
            getInfoByPI(uid);

        }
        else
        {
            getInfoByRI(uid);
            //recevedid 를 가지고 있는 사람의 이름
        }


    }
    protected  void getInfoByPI(String id){
        RetroClient retroClient = RetroClient.getInstance(this).createBaseApi();
        retroClient.getInfoByPI(id, new RetroCallback() {
            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                dataPI = (List<ResponseGetInfoByPI>) receivedData;
                for(int i = 0 ; i<dataPI.size() ; i++){
                    infoitem.add(new InfoItem(dataPI.get(i).serviceId,dataPI.get(i).name,dataPI.get(i).started_at));
                }
                MyAdapter adapter = new MyAdapter(MatchingInfoActivity.this, R.layout.item_matchinginfo, infoitem);
                listView.setAdapter(adapter);

            }

            @Override
            public void onFailure(int code) {

            }
        });

    }
    protected  void getInfoByRI(String id){
        RetroClient retroClient = RetroClient.getInstance(this).createBaseApi();
        retroClient.getInfoByRI(id, new RetroCallback() {
            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                dataRI = (List<ResponseGetInfoByRI>) receivedData;
                for(int i = 0 ; i<dataRI.size() ; i++){
                    infoitem.add(new InfoItem(dataRI.get(i).serviceId,dataRI.get(i).name,dataRI.get(i).started_at));
                }
                MyAdapter adapter = new MyAdapter(MatchingInfoActivity.this, R.layout.item_matchinginfo, infoitem);
                listView.setAdapter(adapter);

            }

            @Override
            public void onFailure(int code) {

            }
        });

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
        intent.putExtra("pushId",this.pushId);
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

            if(result != null){
                mJsonString = result;
                showResult();
            }

            Toast.makeText(MatchingInfoActivity.this,"전송완료",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String serverUrl = (String)strings[0];
            String title = (String)strings[1];
            String body = (String)strings[2];
            String name = (String)strings[3];
            String uid = (String)strings[4];
            String my_name = (String)strings[5];
            String serviceId = (String)strings[6];

            String postParameters = "title=" + title + "&body=" + body +  "&name=" + name +"&uid=" + uid+ "&my_name=" + my_name + "&serviceId=" + serviceId;

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
            Toast.makeText(MatchingInfoActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    class MyAdapter extends BaseAdapter { // 리스트 뷰의 아답타
        Context context;
        int layout;
        ArrayList<InfoItem> infoItem;
        LayoutInflater inf;
        String sender,recevier,suid;
        String serviceId;
        String id;
        public MyAdapter(Context context, int layout, ArrayList<InfoItem> infoItem) {
            this.context = context;
            this.layout = layout;
            this.infoItem = infoItem;
            inf = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return infoItem.size();
        }
        @Override
        public Object getItem(int position) {
            return infoItem.get(position);
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



            final TextView userName = (TextView)convertView.findViewById(R.id.matchinginfo_name);
            final TextView serviceText = (TextView)convertView.findViewById(R.id.matchinginfo_serviceId);
            TextView started_at = (TextView)convertView.findViewById(R.id.matchinginfo_date);
            Button button = (Button)convertView.findViewById(R.id.matchinginfo_button);
            String startTime;
            long diff = 0,min = 0;
            InfoItem m = infoitem.get(position);
            serviceText.setText(Integer.toString(m.serviceId));
            userName.setText(m.name);
            started_at.setText(m.started_at);

            startTime = m.started_at;

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String getTime = sdf.format(date);


            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date nowDate = null;
            try {
                nowDate = f.parse(getTime);
                Date startDate = f.parse(startTime);
                 diff = startDate.getTime() - nowDate.getTime();
                 min = diff / 60000;
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if(min<30 && min>-30){
            }
            else{
                button.setEnabled(false);
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PushNotification push = new PushNotification();
                    suid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    SharedPreferences sharedPreferences = getSharedPreferences( Constants.SHARED_PREFS , MODE_PRIVATE);
                    sender = sharedPreferences.getString(Constants.USER_NAME,"");
                    recevier = userName.getText().toString();
                    serviceId = serviceText.getText().toString();
                    stdByChannel = sender + Constants.STDBY_SUFFIX;
                    push.execute("http://"+IP_ADDRESS+"/resmatching_notification.php","예약 매칭 통화 알림","예약 매칭된 상대의 통화 요청입니다.",recevier,suid,sender,serviceId);
                    initPubNub();
                }
            });
            return convertView;
        }
    }






}


class InfoItem{
    int serviceId;
    String name = "";
    String started_at = "";

    public InfoItem(int serviceId, String name, String started_at){
        super();
        this.serviceId = serviceId;
        this.name = name;
        this.started_at = started_at;
    }

}