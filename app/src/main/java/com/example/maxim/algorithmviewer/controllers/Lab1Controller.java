package com.example.maxim.algorithmviewer.controllers;
import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.activities.ActivityLab1;
import com.example.maxim.algorithmviewer.activities.MainActivity;

import java.util.Objects;
import java.util.Random;

/**
 * Presenter class of main activity.
 * */
public class Lab1Controller {

    /**
     * Simple start function.
     *@param ActivityLab1 ma - activity of main screen
     * */

    public void start(ActivityLab1 ma)
    {
        String data = ((EditText)ma.findViewById(R.id.editText)).getText().toString();
        if(Objects.equals(data, "")) return;

        String[] result = data.split(" ");
        if(result.length <= 0)
        {
            ((TextView)ma.findViewById(R.id.statusBlock)).setText("Неверный формат данных!");
            return;
        }
        Log.w("Test: ",String.format("%d", result.length));
        int parseRes, i = 0, maxnum = -200, sum = 0;
        int[] mass = new int[result.length];
        for(i = 0; i<result.length; i++)    // parsing data
        {
            try{
                parseRes = Integer.parseInt(result[i]);
                if(parseRes >= -100 && parseRes <= 100) {
                    mass[i] = parseRes;
                }
                else
                    throw new NumberFormatException("IncorrectFormat");
            }
            catch (NumberFormatException e)
            {
                ((TextView)ma.findViewById(R.id.statusBlock)).setText("Неверный формат данных!");
                return;
            }
        }
        Log.w("Test: ",String.format("Parsed"));
        startChase(ma, mass);
    }

    /**
     * Random start function.
     *@param ActivityLab1 ma - activity of main screen
     * */
    public void randomStart(ActivityLab1 ma)
    {
        Random r = new Random();
        int[] mass = new int[1000];
        for(int i = 0; i<1000; i++) // random massive filling
            mass[i] = r.nextInt(201) - 100;

        ((EditText)ma.findViewById(R.id.editText)).setText("Случайные 1000 чисел в диапазоне [-100, 100]");

        startChase(ma, mass);
    }

    /**
     * This function begins work of algorithms.
     *@param ActivityLab1 ma - activity of main screen
     *@param int[] mass - data massive for computing
     * */
    void startChase(ActivityLab1 ma, int[] mass)
    {

        int[] slots = new int[205];
        long nanotime = System.nanoTime();
        int maxnum = 0, i = 0;

        // Moda algorithm
        for(i = 0; i<mass.length; i++) //finding number of max repeat of elements
        {
            slots[mass[i]+100]++;
            if(maxnum < slots[mass[i]+100])
                maxnum = slots[mass[i]+100];
        }
        StringBuilder sb = new StringBuilder();
        for(i = 0; i<slots.length; i++) //getting elements by max number of repeat
        {
            if(slots[i] == maxnum)
                sb.append(", ").append(i-100);
        }
        sb.delete(0,2);
        Log.w("Test: ", "Moda completed");
        TextView modaTimeTextView = (TextView)ma.findViewById(R.id.modaTimeBlock);
        modaTimeTextView.setText(ma.getString(R.string.ModaTime) + String.format("%09d", System.nanoTime()-nanotime));
        TextView modaTextView = (TextView)ma.findViewById(R.id.modaBlock);
        modaTextView.setText(ma.getString(R.string.Moda) + sb.toString());
        sb.setLength(0);

        for(i = 0; i<slots.length; i++) //clearing massive
            slots[i] = -200;

        // Mediana algorithm

        nanotime = System.nanoTime();
        for(i = 0; i<mass.length; i++) // count sort
            slots[mass[i]+100]++;
        Log.w("Test: ","Half sorting");
        int index = 0;
        for(i = 0; i<slots.length; i++)
        {
            while(slots[i]>0){
                mass[index] = i;
                index++;
            }
        }
        if(mass.length % 2 == 0) // getting mediana
        {
            int del = mass.length/2;
            sb.append(((float)(mass[del]+mass[del-1]))/2.0f);
        }
        else
            sb.append(mass[mass.length/2]);

        Log.w("Test: ","Mediana completed");

        ((TextView)ma.findViewById(R.id.medianaTimeBlock)).setText(ma.getString(R.string.MedianaTime) + String.format("%09d", System.nanoTime()-nanotime));


        ((TextView)ma.findViewById(R.id.medianaBlock)).setText("Mediana: " + sb.toString());
        ((TextView)ma.findViewById(R.id.statusBlock)).setText("Готово!");

    }
}
