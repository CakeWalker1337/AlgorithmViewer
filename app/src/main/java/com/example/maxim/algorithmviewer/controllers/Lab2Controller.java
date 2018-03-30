package com.example.maxim.algorithmviewer.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maxim.algorithmviewer.activities.LogActivity;
import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.activities.ActivityID;
import com.example.maxim.algorithmviewer.activities.ActivityLab2;
import com.example.maxim.algorithmviewer.database.DatabaseHelper;

import java.util.Calendar;
import java.util.Objects;

public class Lab2Controller {

    private ActivityLab2 act;
    private Context currentContext;
    private int[] ascii;
    private final int ALPHABET_LENGTH = 1200;

    public Lab2Controller(Context ctxt, ActivityLab2 act) {
        currentContext = ctxt;
        this.act = act;
        ascii = new int[ALPHABET_LENGTH];

        createDatabaseTable();

    }

    private void createDatabaseTable()
    {
        DatabaseHelper.pushNonResultQuery(currentContext, "CREATE TABLE IF NOT EXISTS `lab2log`(`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `logTime` TEXT NOT NULL," +
                " `sourceStr` TEXT NOT NULL," +
                " `subStr` TEXT NOT NULL," +
                "`isSensitive` INTEGER DEFAULT '0'," +
                "`result` INTEGER DEFAULT '0');");
    }

    public void startSearch(String str, String substr, boolean isSensitive) {
        if (Objects.equals(str, "") || Objects.equals(substr, "") || substr.length() > str.length()) {
            Toast.makeText(currentContext, R.string.incorrectDataMessage, Toast.LENGTH_LONG).show();
            return;
        }

        String modStr, modSubstr;

        if (!isSensitive) {
            modStr = str.toUpperCase();
            modSubstr = substr.toUpperCase();
        } else {
            modStr = str;
            modSubstr = substr;
        }

        int len = modSubstr.length();
        for (int i = 0; i < ALPHABET_LENGTH; i++)
            ascii[i] = len;


        for (int i = len - 1; i >= 0; i--) {
            if (ascii[(int) modSubstr.charAt(i)] == len) {
                ascii[(int) modSubstr.charAt(i)] = len - i - 1;
            }

        }

        int index = len - 1, localIndex = len - 1;
        boolean isFound = false;
        while (index < modStr.length() && !isFound) {
            if (modStr.charAt(index) == modSubstr.charAt(localIndex)) {
                localIndex--;
                index--;
                if (localIndex < 0) {
                    isFound = true;
                }

            } else {
                localIndex = len - 1;
                index += ascii[(int) modStr.charAt(index)];

            }
        }
        index++;

        ((TextView) act.findViewById(R.id.inputStringLog)).setText("Исходная строка: " + str);
        ((TextView) act.findViewById(R.id.inputSubStringLog)).setText("Образ: " + substr);
        ((TextView) act.findViewById(R.id.isSensitiveLog)).setText("Чувствительность к регистру: " + ((isSensitive)?"Да":"Нет"));

        if (isFound) {
            Spannable spannable = new SpannableString(act.getString(R.string.match_found) + str);
            spannable.setSpan(new ForegroundColorSpan(Color.RED), index+act.getString(R.string.match_found).length(), index+act.getString(R.string.match_found).length() + len, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), index+act.getString(R.string.match_found).length(), index+act.getString(R.string.match_found).length() + len, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            //((EditText) act.findViewById(R.id.stringBlock)).setText(spannable);
            ((TextView) act.findViewById(R.id.resultLog)).setText(spannable);

        } else {

            ((TextView) act.findViewById(R.id.resultLog)).setText("Совпадений не найдено!");
        }

        DatabaseHelper.pushNonResultQuery(currentContext,"INSERT INTO `lab2log` (`logTime`, `sourceStr`, `subStr`, `isSensitive`, `result`) " +
                                                               "VALUES ('" + Calendar.getInstance().getTime().toString() + "', '" + str + "', '" + substr + "', '" + ((isSensitive)?1:0) + "', '" + ((isFound)?1:0) + "');");

    }

    public void showLogs()
    {
        Intent intent = new Intent(currentContext, LogActivity.class);
        intent.putExtra("activityId", ActivityID.BOILER_MOORE_FINDER.ordinal());
        act.startActivity(intent);
    }

    public void clearLogs()
    {
        DatabaseHelper.pushNonResultQuery(currentContext, "DROP TABLE `lab2log`;");
        createDatabaseTable();
    }

}
