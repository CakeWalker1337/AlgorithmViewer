package com.example.maxim.algorithmviewer.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.controllers.Lab2Controller;

public class ActivityLab2 extends AppCompatActivity {

    Lab2Controller controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab2);
        controller = new Lab2Controller(getApplicationContext(), this);
    }

    public void startSearchButtonClick(View view) {
        EditText stringBlock = (EditText) findViewById(R.id.stringBlock);
        EditText substringBlock = (EditText) findViewById(R.id.substringBlock);
        CheckBox sensitivityCheckBox = (CheckBox) findViewById(R.id.sensitivityCheckBox);
        controller.startSearch(stringBlock.getText().toString(), substringBlock.getText().toString(), sensitivityCheckBox.isChecked());
    }

    public void showLogButtonClick(View view) {
        controller.showLogs();
    }

    public void clearLogButtonClick(View view) {
        controller.clearLogs();
    }
}
