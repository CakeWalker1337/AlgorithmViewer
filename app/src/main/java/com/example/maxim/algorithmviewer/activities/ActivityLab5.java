package com.example.maxim.algorithmviewer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.controllers.Lab5Controller;

public class ActivityLab5 extends AppCompatActivity {

    Lab5Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab5);
        controller = new Lab5Controller(this);
    }

    /**
     * Simple start of algorithm
     * startButton event
     */
    public void startButtonClick(View view) {
        controller.start();
    }

    /**
     * Start with random elements
     * startButtonRandom event
     */
    public void startRandomButtonClick(View view) {
        controller.randomStart();
    }

    /**
     * Show logs
     * showLogButton event
     */
    public void showLogButtonClick(View view) {
        controller.showLogs();
    }

    /**
     * Clear logs
     * clearLogButton event
     */
    public void clearLogButtonClick(View view) {
        controller.clearLogs();
    }

}
