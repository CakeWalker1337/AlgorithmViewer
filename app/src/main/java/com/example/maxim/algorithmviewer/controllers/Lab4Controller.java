package com.example.maxim.algorithmviewer.controllers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.activities.ActivityID;
import com.example.maxim.algorithmviewer.activities.ActivityLab4;
import com.example.maxim.algorithmviewer.activities.LogActivity;
import com.example.maxim.algorithmviewer.activities.SortActivity;
import com.example.maxim.algorithmviewer.database.DatabaseHelper;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;


public class Lab4Controller {

    private final int NUM_OF_RANDOM_ELEMENTS = 200;

    private int[] sourceMassive = null;
    private int[] firstSwapsCountMassive = null;
    private int[] secondSwapsCountMassive = null;

    public Lab4Controller(Context context) {
        createDatabaseTable(context);
    }

    private void createDatabaseTable(Context context) {

        DatabaseHelper.pushNonResultQuery(context, "CREATE TABLE IF NOT EXISTS `lab4log`(`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `logTime` TEXT NOT NULL," +
                " `sourceSequence` TEXT NOT NULL," +
                " `firstSortedSequence` TEXT NOT NULL," +
                " `firstSwapsCount` INTEGER DEFAULT '0'," +
                " `firstComparesCount` INTEGER DEFAULT '0'," +
                " `secondSortedSequence` TEXT NOT NULL," +
                " `secondSwapsCount` INTEGER DEFAULT '0'," +
                " `secondComparesCount` INTEGER DEFAULT '0');");

    }

    public void start(ActivityLab4 ma) {
        String data = ((EditText) ma.findViewById(R.id.inputSequenceBlock)).getText().toString();
        if (Objects.equals(data, "")) return;

        String[] result = data.split(" ");
        if (result.length <= 0) {
            ((TextView) ma.findViewById(R.id.statusBlock)).setText(R.string.wrongInputFormat);
            return;
        }
        Log.w("Test: ", String.format("%d", result.length));
        int parseRes, i = 0, maxnum = -200, sum = 0;
        int[] mass = new int[result.length];
        for (i = 0; i < result.length; i++)    // parsing data
        {
            try {
                parseRes = Integer.parseInt(result[i]);
                if (parseRes >= -100 && parseRes <= 100) {
                    mass[i] = parseRes;
                } else
                    throw new NumberFormatException(ma.getString(R.string.incorrectFormatException));
            } catch (NumberFormatException e) {
                ((TextView) ma.findViewById(R.id.statusBlock)).setText(R.string.wrongInputFormat);
                return;
            }
        }
        Log.w("Test: ", String.format("Parsed"));
        startChase(ma, mass);
    }

    /**
     * Random start function.
     *
     * @param ActivityLab4 ma - activity of main screen
     */
    public void randomStart(ActivityLab4 ma) {
        Random r = new Random();
        int[] mass = new int[NUM_OF_RANDOM_ELEMENTS];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUM_OF_RANDOM_ELEMENTS; i++) // random massive filling
        {
            mass[i] = r.nextInt(201) - 100;
            sb.append(mass[i] + " ");
        }
        ((EditText) ma.findViewById(R.id.inputSequenceBlock)).setText(sb.toString());

        startChase(ma, mass);
    }

    /**
     * This function begins work of algorithms.
     *
     * @param ActivityLab4 ma - activity of main screen
     * @param int[]        mass - data massive for computing
     */
    private void startChase(ActivityLab4 ma, int[] mass) {

        firstSwapsCountMassive = new int[mass.length];
        secondSwapsCountMassive = new int[mass.length];
        int swapsCount = 0, comparesCount = 0;

        StringBuilder sequence = new StringBuilder();
        for (int i = 0; i < mass.length; i++)
            sequence.append(mass[i] + " ");

        String sourceSequence = sequence.toString();

        int[] copyMass = mass.clone();

        //Начало шейкерной сортировки
        int buffer = 0, left = 0, right = mass.length - 1;
        while (left <= right) {

            for (int i = right; i > left; i--) {
                if (mass[i - 1] > mass[i]) {
                    buffer = mass[i];
                    mass[i] = mass[i - 1];
                    mass[i - 1] = buffer;

                    swapsCount++;

                    buffer = firstSwapsCountMassive[i];
                    firstSwapsCountMassive[i] = firstSwapsCountMassive[i - 1];
                    firstSwapsCountMassive[i - 1] = buffer;

                    firstSwapsCountMassive[i]++;
                    firstSwapsCountMassive[i - 1]++;
                }
                comparesCount++;
            }
            left++;

            for (int i = left; i < right; i++) {
                if (mass[i] > mass[i + 1]) {
                    buffer = mass[i];
                    mass[i] = mass[i + 1];
                    mass[i + 1] = buffer;

                    swapsCount++;

                    buffer = firstSwapsCountMassive[i];
                    firstSwapsCountMassive[i] = firstSwapsCountMassive[i + 1];
                    firstSwapsCountMassive[i + 1] = buffer;

                    firstSwapsCountMassive[i]++;
                    firstSwapsCountMassive[i + 1]++;

                }
                comparesCount++;
            }
            right--;
        }

        sequence.setLength(0);
        for (int i = 0; i < mass.length; i++)
            sequence.append(mass[i] + " ");

        String firstSortedSequence = sequence.toString();

        ((TextView) ma.findViewById(R.id.swapsCountFirstBlock)).setText("" + swapsCount);
        ((TextView) ma.findViewById(R.id.comparesCountFirstBlock)).setText("" + comparesCount);

        int firstSwapsCount = swapsCount, firstComparesCount = comparesCount;

        swapsCount = 0;
        comparesCount = 0;

        int step = copyMass.length;

        int i, j, buffer2 = 0;
        while (step > 0) {
            step = step / 2;
            i = 0;
            j = 0;
            for (i = step; i < copyMass.length; i++) {
                buffer = copyMass[i];
                buffer2 = secondSwapsCountMassive[i];
                for (j = i - step; (j >= 0) && (copyMass[j] > buffer); j -= step) {
                    copyMass[j + step] = copyMass[j];
                    secondSwapsCountMassive[j + step] = secondSwapsCountMassive[j] + 1;
                    comparesCount++;
                    swapsCount++;
                }
                copyMass[j + step] = buffer;
                secondSwapsCountMassive[j + step] = buffer2 + 1;
            }
        }

        sequence.setLength(0);
        for (i = 0; i < copyMass.length; i++)
            sequence.append(copyMass[i] + " ");


        ((TextView) ma.findViewById(R.id.swapsCountSecondBlock)).setText("" + swapsCount);
        ((TextView) ma.findViewById(R.id.comparesCountSecondBlock)).setText("" + comparesCount);


        ((TextView) ma.findViewById(R.id.inputSequenceBlock)).setText(sequence.toString());

        DatabaseHelper.pushNonResultQuery(ma.getBaseContext(), "INSERT INTO `lab4log` (`logTime`," +
                " `sourceSequence`, `firstSortedSequence`, `firstSwapsCount`, `firstComparesCount`," +
                " `secondSortedSequence`, `secondSwapsCount`, `secondComparesCount`) " +
                " VALUES ('" + Calendar.getInstance().getTime().toString() + "', '" + sourceSequence +
                "', '" + firstSortedSequence + "', '" + firstSwapsCount + "', '" + firstComparesCount +
                "', '" + sequence.toString() + "', '" + swapsCount + "', '" + comparesCount + "');");
        ((TextView) ma.findViewById(R.id.statusBlock)).setText(R.string.success);
        sourceMassive = copyMass;
    }

    public void showLogs(ActivityLab4 act) {
        Intent intent = new Intent(act.getBaseContext(), LogActivity.class);
        intent.putExtra("activityId", ActivityID.ADVANCED_SORT_ALGORITHMS.ordinal());
        act.startActivity(intent);

    }

    public void clearLogs(Context context) {
        DatabaseHelper.pushNonResultQuery(context, "DROP TABLE `lab4log`;");
        createDatabaseTable(context);
    }

    public void showDetails(ActivityLab4 act) {
        if (sourceMassive == null)
            return;
        Intent intent = new Intent(act.getBaseContext(), SortActivity.class);
        intent.putExtra("sourceMassive", sourceMassive);
        intent.putExtra("firstCountMassive", firstSwapsCountMassive);
        intent.putExtra("secondCountMassive", secondSwapsCountMassive);
        act.startActivity(intent);

    }
}
