package com.example.hstalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hstalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {

    private EditText textEmail;
    private EditText textName;
    private EditText textPassword;
    private Button signupB;
    private String splash_background;
    private ImageView profile;
    private Uri imageUri;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    private String uid;

    private static String IP_ADDRESS = "52.231.69.121";
    private static String TAG = "phptest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }

        textEmail = (EditText)findViewById(R.id.signupActivity_edittext_email);
        textName = (EditText)findViewById(R.id.signupActivity_edittext_name);
        textPassword = (EditText)findViewById(R.id.signupActivity_edittext_password);
        signupB = (Button)findViewById(R.id.signupActivity_button_signup);
        signupB.setBackgroundColor(Color.parseColor(splash_background));
        radioGroup = (RadioGroup)findViewById(R.id.signupactivity_radiogroup);
        radioButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());

        signupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(textEmail.getText().toString() == null || textName.getText().toString() == null || textPassword.getText().toString() == null){
                    return;
                }

                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(textEmail.getText().toString(), textPassword.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        final String uid = task.getResult().getUser().getUid();



                                                final UserModel userModel = new UserModel();
                                                userModel.userName = textName.getText().toString();
                                                userModel.userType = radioButton.getText().toString();
                                                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //mysql DB 에 회원정보 저장(user table)
                                                        String email = textEmail.getText().toString();
                                                        String name = textName.getText().toString();
                                                        String password = textPassword.getText().toString();
                                                        String uid = userModel.uid;
                                                        String token = FirebaseInstanceId.getInstance().getToken();
                                                        String userType;
                                                        if(radioButton.getText().toString().equals("청각장애인")){
                                                            userType = "H";
                                                        }else{
                                                            userType = "V";
                                                        }
                                                        InsertUserData insertUserData = new InsertUserData();
                                                        insertUserData.execute("http://"+IP_ADDRESS+"/insertUser.php",email,password,name,uid,token,userType);
                                                        SignupActivity.this.finish();
                                                    }
                                                });


                                    }

                            });
            }
        });
    }


    class InsertUserData extends AsyncTask<String,Void,String>{


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
            String userId = (String)strings[1];
            String password = (String)strings[2];
            String name = (String)strings[3];
            String uid = (String)strings[4];
            String token = (String)strings[5];
            String userType = (String)strings[6];

            String postParameters = "userId=" + userId + "&password=" + password + "&name=" + name + "&uid=" + uid + "&token=" + token + "&userType=" + userType;

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
