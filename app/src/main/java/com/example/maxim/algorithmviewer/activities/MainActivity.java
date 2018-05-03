package com.example.maxim.algorithmviewer.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.controllers.Lab1Controller;
import com.example.maxim.algorithmviewer.database.DatabaseHelper;


public class MainActivity extends AppCompatActivity {
    final int LAB_COUNT = 8;
    Lab1Controller pr;
    String[] algorithmNames = {"Нахождение моды и медианы", "Алгоритм Бойлера-Мура",
            "Алгоритмы сортировки массивов", "Улучшенные алгоритмы сортировки",
            "Внешняя сортировка", "Обход графов"
    };


    /**
     * Initial method of activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper.openDB(getBaseContext());

        LinearLayout layout = findViewById(R.id.mainLayout);
        for (int i = 0; i < LAB_COUNT; i++) {
            try {
                final Class activity = Class.forName("com.example.maxim.algorithmviewer.activities.ActivityLab" + (i + 1));

                Button button = new Button(this);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), activity);
                        startActivity(intent);
                    }
                });
                button.setText(algorithmNames[i]);
                button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layout.addView(button);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
