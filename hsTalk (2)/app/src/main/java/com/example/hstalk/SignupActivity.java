package com.example.hstalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.Retrofit.ResponseBody.ResponseGetBoardList;
import com.example.hstalk.Retrofit.ResponseBody.ResponsecheckEmail;
import com.example.hstalk.Retrofit.RetroCallback;
import com.example.hstalk.Retrofit.RetroClient;
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
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    private EditText textEmail;
    private EditText textName;
    private EditText textPassword;
    private EditText textPwCheck;
    private Button signupB;
    private String splash_background;
    private RadioButton userTypeH;
    private RadioButton userTypeV;
    private RadioGroup radioGroup;
    private String uid;
    private String userType;

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
        textPwCheck = (EditText)findViewById(R.id.signupActivity_edittext_passwordcheck);
        signupB = (Button)findViewById(R.id.signupActivity_button_signup);
        signupB.setBackgroundColor(Color.parseColor(splash_background));
        radioGroup = (RadioGroup)findViewById(R.id.signupactivity_radiogroup);
        userTypeH = (RadioButton)findViewById(R.id.signupactivity_radiobutton_hear);
        userTypeV = (RadioButton)findViewById(R.id.signupactivity_radiobutton_trans);

        textPwCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                String pw = textPassword.getText().toString();
//                if(pw.length() < 6){
//                    Toast.makeText(SignupActivity.this,"비밀번호는 6자리 이상 입력해주세요",Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pw = textPassword.getText().toString();
                String check = textPwCheck.getText().toString();

                if(pw.equals(check)){
                    textPwCheck.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.baseline_check_circle_black_18dp),null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        signupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (textEmail.getText().toString().length() == 0 || textName.getText().toString().length() == 0 || textPassword.getText().toString().length() == 0) {
                    Toast.makeText(SignupActivity.this, "회원가입 오류", Toast.LENGTH_SHORT).show();
                    return;
                }else if (textPassword.getText().toString().length() < 6) {
                    Toast.makeText(SignupActivity.this, "비밀번호는 6자리 이상으로 설정해주세요.", Toast.LENGTH_SHORT).show();
                    textPassword.setText("");
                    textPwCheck.setText("");
                    return;
                } else if (!textPassword.getText().toString().equals(textPwCheck.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "비밀번호가 일치하지 않습니다,", Toast.LENGTH_SHORT).show();
                    textPassword.setText("");
                    textPwCheck.setText("");
                    return;
                }




                final AppCompatDialog pd = new AppCompatDialog(SignupActivity.this);
                pd.setCancelable(false);
                pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                pd.setContentView(R.layout.progress_loading);
                pd.show();
                final ImageView img_loading_frame = (ImageView) pd.findViewById(R.id.iv_frame_loading);
                final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
                img_loading_frame.post(new Runnable() {
                    @Override
                    public void run() {
                        frameAnimation.start();
                    }
                });

                TextView tv_progress_message = (TextView) pd.findViewById(R.id.tv_progress_message);
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(textEmail.getText().toString(), textPassword.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final String uid = task.getResult().getUser().getUid();


                                final UserModel userModel = new UserModel();
                                userModel.userName = textName.getText().toString();
                                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                if (userTypeH.isChecked()) {
                                    userType = "H";
                                } else {
                                    userType = "V";
                                }
                                userModel.userType = userType;

                                //mysql DB 에 회원정보 저장(user table)
                                String email = textEmail.getText().toString();
                                String name = textName.getText().toString();
                                String password = textPassword.getText().toString();
                                String token = FirebaseInstanceId.getInstance().getToken();

                                InsertUserData insertUserData = new InsertUserData();
                                insertUserData.execute("http://" + IP_ADDRESS + "/insertUser.php", email, password, name, uid, token, userType);
                                pd.dismiss();
                                SignupActivity.this.finish();

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
