package com.example.hstalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class MatchingActivity extends AppCompatActivity {

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            }
        });


    }
}
