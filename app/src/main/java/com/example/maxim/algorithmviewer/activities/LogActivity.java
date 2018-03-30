package com.example.maxim.algorithmviewer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.database.DatabaseHelper;


public class LogActivity extends AppCompatActivity {

    private int startIndex = 0, endIndex = 0, currentCountOfRows = 0;

    private Button nextLogButton, prevLogButton;
    private TextView logView;
    private ActivityID currentActivityId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        currentActivityId = ActivityID.values()[getIntent().getIntExtra("activityId", 0)];

        nextLogButton = findViewById(R.id.showNextLogButton);
        prevLogButton = findViewById(R.id.showPrevLogButton);
        logView = findViewById(R.id.logView);

        prevLogButton.setEnabled(false);

        currentCountOfRows = DatabaseHelper.getCountOfLogRows(getBaseContext(), currentActivityId);
        if(currentCountOfRows > 50)
            endIndex = 50;
        else if(currentCountOfRows == 0)
        {
            nextLogButton.setEnabled(false);
            logView.setText(R.string.logsNotFound);
        }
        else {
            nextLogButton.setEnabled(false);
            endIndex = 50;
        }


        logView.setText(DatabaseHelper.getLogs(getBaseContext(), currentActivityId, startIndex, endIndex));
    }

    public void showNextLogButtonClick(View view) {
        startIndex += 50;
        endIndex += 50;
        if (endIndex > currentCountOfRows){
            nextLogButton.setEnabled(false);
        }
        prevLogButton.setEnabled(true);
        logView.setText(DatabaseHelper.getLogs(getBaseContext(), currentActivityId, startIndex, endIndex));
    }

    public void showPrevLogButtonClick(View view) {
        startIndex -= 50;
        endIndex -= 50;
        if (endIndex < 0){
            prevLogButton.setEnabled(false);
        }
        nextLogButton.setEnabled(true);
        logView.setText(DatabaseHelper.getLogs(getBaseContext(), currentActivityId, startIndex, endIndex));
    }
}
