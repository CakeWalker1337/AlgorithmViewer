package com.example.maxim.algorithmviewer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.controllers.Lab3Controller;

public class ActivityLab3 extends AppCompatActivity {

    Lab3Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab3);
        controller = new Lab3Controller(getBaseContext());
    }

    /**
     * Simple start of algorithm
     * startButton event
     */
    public void startButtonClick(View view) {
        controller.start(this);
    }

    /**
     * Start with random elements
     * startButtonRandom event
     */
    public void startRandomButtonClick(View view) {
        controller.randomStart(this);
    }

    public void showLogButtonClick(View view) {
        controller.showLogs(this);
    }

    public void clearLogButtonClick(View view) {
        controller.clearLogs(getBaseContext());
    }
}
