package com.example.hstalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.app.DatePickerDialog;

public class CreateBoardActivity extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createboard);
        Button startDate = (Button)findViewById(R.id.button_start_date);
        Button startTime = (Button)findViewById(R.id.button_start_time);
        Button endDate = (Button)findViewById(R.id.button_end_date);
        Button endTime = (Button)findViewById(R.id.button_end_time);

//        startDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog(DIALOG_DATE);
//            }
//        });

    }
}
