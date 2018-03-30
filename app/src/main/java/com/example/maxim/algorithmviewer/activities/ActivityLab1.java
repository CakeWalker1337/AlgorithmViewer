package com.example.maxim.algorithmviewer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.maxim.algorithmviewer.controllers.Lab1Controller;
import com.example.maxim.algorithmviewer.R;

public class ActivityLab1 extends AppCompatActivity {

    Lab1Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1);
        controller = new Lab1Controller();
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

}
