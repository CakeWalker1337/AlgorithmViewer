package com.example.maxim.algorithmviewer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;


import com.example.maxim.algorithmviewer.activities.ActivityID;

public class DatabaseHelper {

    public static final String DB_NAME = "algorithmviewer";

    private static SQLiteDatabase database;

    public static void openDB(Context context) {
        try {
            database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        } catch (SQLiteException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static void closeDB() {
        database.close();
    }

    public static void pushNonResultQuery(Context context, String query) {
        try {
            database.execSQL(query);
        } catch (SQLException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static String getLogs(Context context, ActivityID aId, int startPos, int endPos) {
        if (aId == ActivityID.ERROR)
            return null;

        if (aId == ActivityID.BOILER_MOORE_FINDER) {
            try {
                Cursor query = database.rawQuery("SELECT * FROM `lab2log` WHERE `id`>=" + startPos + " and `id`<=" + endPos + ";", null);
                if (!query.moveToFirst())
                {
                    query.close();
                    return null;
                }


                StringBuilder sb = new StringBuilder();
                do {
                    sb.append(query.getInt(0) + ". ")
                            .append(query.getString(1))
                            .append("\nSource string: " + query.getString(2))
                            .append("\nSubstring: " + query.getString(3))
                            .append("\nIs sensitive: " + ((query.getInt(4) == 1) ? "Yes" : "No"))
                            .append("\nIs found: " + ((query.getInt(5) == 1) ? "Yes" : "No") + "\n\n");
                }
                while (query.moveToNext());

                query.close();
                return sb.toString();
            } catch (SQLException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            }

        }
        if (aId == ActivityID.SIMPLE_SORT_ALGORITHMS) {
            try {
                Cursor query = database.rawQuery("SELECT * FROM `lab3log` WHERE `id`>=" + startPos + " and `id`<=" + endPos + ";", null);
                if (!query.moveToFirst())
                {
                    query.close();
                    return null;
                }


                StringBuilder sb = new StringBuilder();
                do {
                    sb.append(query.getInt(0) + ". ")
                            .append(query.getString(1))
                            .append("\nSource sequence: " + query.getString(2))
                            .append("\nIS Sorted sequence: " + query.getString(3))
                            .append("\nIS swaps count: " + query.getInt(4))
                            .append("\nIS compares count: " + query.getInt(5))
                            .append("\nBS Sorted sequence: " + query.getString(6))
                            .append("\nBS swaps count: " + query.getInt(7))
                            .append("\nBS compares count: " + query.getInt(8) + "\n\n");
                }
                while (query.moveToNext());

                query.close();
                return sb.toString();
            } catch (SQLException e) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            }

        }
        return null;
    }

    public static int getCountOfLogRows(Context context, ActivityID aId)
    {
        String tableName = "errorname";
        if (aId == ActivityID.ERROR)
            return 0;
        if (aId == ActivityID.BOILER_MOORE_FINDER)
            tableName = "lab2log";
        if (aId == ActivityID.SIMPLE_SORT_ALGORITHMS)
            tableName = "lab3log";

        try {
            Cursor query = database.rawQuery("SELECT max(`id`) from " + tableName + ";", null);
            if (!query.moveToFirst())
            {
                query.close();
                return 0;
            }

            int result = query.getInt(0);
            query.close();
            return result;

        } catch (SQLException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
        return 0;
    }

}
