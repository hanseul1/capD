package com.example.hstalk;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

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
import java.util.HashMap;

public class MatchingActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "52.231.69.121";
    private static String TAG ="pushtest";
    private static final String TAG_JSON = "getpush";
    private static final String TAG_PUSH = "pushId";
    String mJsonString;
    String pushId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String user = bundle.getString("user");
        String title = bundle.getString("title");
        String body = bundle.getString("body");

        TextView titleText = (TextView)findViewById(R.id.matchingactivity_textview_title);
        TextView bodyText = (TextView)findViewById(R.id.matchingactivity_textview_body);
        Button button = (Button)findViewById(R.id.matchingactivity_button);
        titleText.setText(title);
        bodyText.setText(body);

        GetPush push = new GetPush();
        push.execute("http://" + IP_ADDRESS + "/getPush.php",title,body);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
        });


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

                String id = item.getString(TAG_PUSH);
                pushId = id;
            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }


    }
}
