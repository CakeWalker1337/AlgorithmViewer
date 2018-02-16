package com.example.maxim.algorithmviewer;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Main class of program
 * */
public class MainActivity extends AppCompatActivity {
    MainPresenter pr;
    final MainActivity ma = this;

    /**
     * Initial method of activity
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pr = new MainPresenter();
    }

    /**
     * Simple start of algorithm
     * startButton event
     * */
    public void startButtonClick(View view) {
        pr.Start(ma);
    }

    /**
     * Start with random elements
     * startButtonRandom event
     * */
    public void startRandomButtonClick(View view) {
        pr.RandomStart(ma);
    }
}
