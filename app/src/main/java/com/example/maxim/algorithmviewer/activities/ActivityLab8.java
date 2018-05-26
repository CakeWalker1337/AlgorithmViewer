package com.example.maxim.algorithmviewer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.controllers.Lab8Controller;
import com.example.maxim.algorithmviewer.model.CanvasField;

public class ActivityLab8 extends AppCompatActivity {

    CanvasField canvasField;
    Lab8Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lab8);

        canvasField = findViewById(R.id.paintPlace);
        controller = new Lab8Controller(this, canvasField);
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

    public void startButtonClick(View view) {
        controller.start();
    }

    /**
     * Clear logs
     * clearLogButton event
     */
    public void clearFieldButtonClick(View view) {
        canvasField.clearCanvas();
    }
}
