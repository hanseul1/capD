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

import com.example.hstalk.Retrofit.ResponseBody.ResponseGet;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetBoard;
import com.example.hstalk.Retrofit.RetroCallback;
import com.example.hstalk.Retrofit.RetroClient;
import com.example.hstalk.util.Constants;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReserveMatchingActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "52.231.69.121";
    private static String TAG ="pushtest";
    TextView title;
    TextView boardTitleText;
    TextView boardBodyText;
    TextView startTime;
    TextView endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_matching);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        final String sender = bundle.getString("sender");
        int postId = Integer.parseInt(bundle.getString("postId"));

        title = (TextView)findViewById(R.id.resmatchingactivity_title);
        boardTitleText = (TextView)findViewById(R.id.resmatchingactivity_boardtitle);
        boardBodyText = (TextView)findViewById(R.id.resmatchingactivity_boardbody);
        startTime = (TextView)findViewById(R.id.resmatchingactivity_starttime);
        endTime = (TextView)findViewById(R.id.resmatchingactivity_endtime);
        Button button = (Button)findViewById(R.id.resmatchingactivity_button);

        title.setText(sender + " 님의 예약 매칭 신청");
        getBoard(postId);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String provider = FirebaseAuth.getInstance().getCurrentUser().getUid();
                MatchingPush task = new MatchingPush();
                task.execute("http://" + IP_ADDRESS + "/resmatching_complete.php",sender,provider,startTime.getText().toString(),endTime.getText().toString());
            }
        });

    }

    protected void getBoard(int id){
        RetroClient retroClient = RetroClient.getInstance(this).createBaseApi();
        retroClient.getBoard(id, new RetroCallback() {
            @Override
            public void onError(Throwable t) {
                Toast.makeText(ReserveMatchingActivity.this,t.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                ResponseGetBoard data = (ResponseGetBoard) receivedData;
                boardTitleText.setText(data.title);
                boardBodyText.setText(data.description);
                startTime.setText(data.started_at);
                endTime.setText(data.ended_at);
            }

            @Override
            public void onFailure(int code) {
                Toast.makeText(ReserveMatchingActivity.this,"fail",Toast.LENGTH_SHORT).show();
            }
        });
    }

    class MatchingPush extends AsyncTask<String,Void,String>{

        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);

            Toast.makeText(ReserveMatchingActivity.this,"매칭 완료", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String serverUrl = (String)strings[0];
            String receiver = (String)strings[1];
            String provider = (String)strings[2];
            String started_at = (String)strings[3];
            String ended_at = (String)strings[4];

            String postParameters = "receiver=" + receiver + "&provider=" + provider + "&started_at=" + started_at + "&ended_at=" + ended_at;

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
}
