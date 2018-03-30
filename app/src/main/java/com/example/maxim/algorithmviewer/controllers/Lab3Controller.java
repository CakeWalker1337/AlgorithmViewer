package com.example.maxim.algorithmviewer.controllers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.activities.ActivityID;
import com.example.maxim.algorithmviewer.activities.ActivityLab3;
import com.example.maxim.algorithmviewer.activities.LogActivity;
import com.example.maxim.algorithmviewer.database.DatabaseHelper;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;


public class Lab3Controller {

    private final int NUM_OF_RANDOM_ELEMENTS = 200;

    public  Lab3Controller(Context context)
    {
        createDatabaseTable(context);
    }

    private void createDatabaseTable(Context context)
    {
        DatabaseHelper.pushNonResultQuery(context, "CREATE TABLE IF NOT EXISTS `lab3log`(`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `logTime` TEXT NOT NULL," +
                " `sourceSequence` TEXT NOT NULL," +
                " `ISSortedSequence` TEXT NOT NULL," +
                " `ISSwapsCount` INTEGER DEFAULT '0'," +
                " `ISComparesCount` INTEGER DEFAULT '0'," +
                " `BSSortedSequence` TEXT NOT NULL," +
                " `BSSwapsCount` INTEGER DEFAULT '0'," +
                " `BSComparesCount` INTEGER DEFAULT '0');");

    }

    public void start(ActivityLab3 ma)
    {
        String data = ((EditText)ma.findViewById(R.id.inputSequenceBlock)).getText().toString();
        if(Objects.equals(data, "")) return;

        String[] result = data.split(" ");
        if(result.length <= 0)
        {
            ((TextView)ma.findViewById(R.id.statusBlock)).setText(R.string.wrongInputFormat);
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
                    throw new NumberFormatException(ma.getString(R.string.incorrectFormatException));
            }
            catch (NumberFormatException e)
            {
                ((TextView)ma.findViewById(R.id.statusBlock)).setText(R.string.wrongInputFormat);
                return;
            }
        }
        Log.w("Test: ",String.format("Parsed"));
        startChase(ma, mass);
    }

    /**
     * Random start function.
     *@param ActivityLab3 ma - activity of main screen
     * */
    public void randomStart(ActivityLab3 ma)
    {
        Random r = new Random();
        int[] mass = new int[NUM_OF_RANDOM_ELEMENTS];
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<NUM_OF_RANDOM_ELEMENTS; i++) // random massive filling
        {
            mass[i] = r.nextInt(201) - 100;
            sb.append(mass[i] + " ");
        }
        ((EditText)ma.findViewById(R.id.inputSequenceBlock)).setText(sb.toString());

        startChase(ma, mass);
    }

    /**
     * This function begins work of algorithms.
     *@param ActivityLab3 ma - activity of main screen
     *@param int[] mass - data massive for computing
     * */
    private void startChase(ActivityLab3 ma, int[] mass)
    {

        StringBuilder sequence = new StringBuilder();
        for(int i = 0; i<mass.length; i++)
            sequence.append(mass[i] + " ");

        String sourceSequence = sequence.toString();

        int[] copyMass = mass.clone();
        int insertIndex, buffer, comparesCount = 0, swapsCount = 0;

        for(int i = 1; i<mass.length; i++)
        {
            insertIndex = i;
            buffer = 0;
            for(int j = 0; j<i; j++)
            {
                comparesCount++;
                if(mass[i] < mass[j]) {
                    insertIndex = j;
                    buffer = mass[i];
                    break;
                }
            }
            for(int k = i; k>insertIndex; k--) {
                mass[k] = mass[k - 1];
                swapsCount++;
            }
            if(insertIndex != i) {
                mass[insertIndex] = buffer;
                swapsCount++;
            }
        }

        sequence.setLength(0);
        for(int i = 0; i<mass.length; i++)
            sequence.append(mass[i] + " ");

        String ISSortedSequence = sequence.toString();

        ((TextView)ma.findViewById(R.id.swapsCountISBlock)).setText("" + swapsCount);
        ((TextView)ma.findViewById(R.id.comparesCountISBlock)).setText("" + comparesCount);

        int ISSwapsCount = swapsCount, ISComparesCount = comparesCount;

        swapsCount = 0;
        comparesCount = 0;

        for(int i = 1; i<copyMass.length; i++)
        {
            for(int j = copyMass.length-1; j>=i; j--)
            {
                if(copyMass[j]<copyMass[j-1])
                {
                    buffer = copyMass[j];
                    copyMass[j] = copyMass[j-1];
                    copyMass[j-1] = buffer;
                    swapsCount++;
                }
                comparesCount++;
            }
        }

        ((TextView)ma.findViewById(R.id.swapsCountBSBlock)).setText("" + swapsCount);
        ((TextView)ma.findViewById(R.id.comparesCountBSBlock)).setText("" + comparesCount);

        sequence.setLength(0);
        for(int i = 0; i<copyMass.length; i++)
            sequence.append(copyMass[i] + " ");

        ((TextView)ma.findViewById(R.id.inputSequenceBlock)).setText(sequence.toString());

        DatabaseHelper.pushNonResultQuery(ma.getBaseContext(), "INSERT INTO `lab3log` (`logTime`, `sourceSequence`, `ISSortedSequence`, `ISSwapsCount`, `ISComparesCount`, `BSSortedSequence`, `BSSwapsCount`, `BSComparesCount`) " +
                "VALUES ('" + Calendar.getInstance().getTime().toString() + "', '" + sourceSequence + "', '" + ISSortedSequence + "', '" + ISSwapsCount + "', '" + ISComparesCount + "', '" + sequence.toString() + "', '" + swapsCount + "', '" + comparesCount + "');");
        ((TextView)ma.findViewById(R.id.statusBlock)).setText(R.string.success);
    }

    public void showLogs(ActivityLab3 act)
    {
        Intent intent = new Intent(act.getBaseContext(), LogActivity.class);
        intent.putExtra("activityId", ActivityID.SIMPLE_SORT_ALGORITHMS.ordinal());
        act.startActivity(intent);

    }

    public void clearLogs(Context context)
    {
        DatabaseHelper.pushNonResultQuery(context, "DROP TABLE `lab3log`;");
        createDatabaseTable(context);
    }
}
