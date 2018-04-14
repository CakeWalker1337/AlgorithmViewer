package com.example.maxim.algorithmviewer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.maxim.algorithmviewer.R;

import com.example.maxim.algorithmviewer.controllers.Lab4Controller;

/**
 * Created by Maxim on 12.04.2018.
 */

public class ActivityLab4 extends AppCompatActivity {

    Lab4Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4);
        controller = new Lab4Controller(getBaseContext());
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

    public void showDetailsButtonClick(View view) {
        controller.showDetails(this);
    }
}
