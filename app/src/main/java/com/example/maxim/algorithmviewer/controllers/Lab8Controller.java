package com.example.maxim.algorithmviewer.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.maxim.algorithmviewer.activities.ActivityID;
import com.example.maxim.algorithmviewer.activities.ActivityLab8;
import com.example.maxim.algorithmviewer.activities.LogActivity;
import com.example.maxim.algorithmviewer.database.DatabaseHelper;
import com.example.maxim.algorithmviewer.model.CanvasField;
import com.example.maxim.algorithmviewer.model.GraphType;
import com.example.maxim.algorithmviewer.model.Line;
import com.example.maxim.algorithmviewer.model.Vertex;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Maxim on 24.05.2018.
 */

public class Lab8Controller {

    private boolean[] confirmedVertexes;
    private int[] paths;
    private ActivityLab8 activity;
    private CanvasField canvasField;
    private StringBuilder result;

    public Lab8Controller(ActivityLab8 activity, CanvasField field) {
        canvasField = field;
        this.activity = activity;
        createDatabaseTable(activity.getApplicationContext());
    }

    /**
     * Метод создает таблицу в БД для логов
     *
     * @param context - контекст приложения
     */
    private void createDatabaseTable(Context context) {
        DatabaseHelper.pushNonResultQuery(context, "CREATE TABLE IF NOT EXISTS `lab8log`(" +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `logTime` TEXT," +
                " `startVertex` INTEGER DEFAULT '0'," +
                " `tree` TEXT," +
                " `treeCost` INTEGER DEFAULT '0');");
    }

    /**
     * Метод начинает обход построенного графа в глубину
     */
    public void start() {

        if (canvasField.getVertexesCount() == 0) {
            Toast.makeText(activity.getApplicationContext(), "Нет ни одной вершины!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (canvasField.getVertexesCount() == 1) {
            Toast.makeText(activity.getApplicationContext(), "Нет ни одного ребра!", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        ArrayList<Vertex> newVertices = new ArrayList<>();

        Random r = new Random();

        StringBuilder linesTrace = new StringBuilder();

        newVertices.add(canvasField.getVertex(r.nextInt(canvasField.getVertexesCount())));
        Log.w("OSTOV", newVertices.get(0).getId()+"");
        boolean isVertexAllowed = true;
        int sum = 0;
        while (isVertexAllowed && newVertices.size() != canvasField.getVertexesCount()) {
            Line minWeightLine = null;
            Vertex newVertex = null;
            int minWeight = Integer.MAX_VALUE;
            for (int i = 0; i < newVertices.size(); i++) {
                Vertex v = newVertices.get(i);
                for (int j = 0; j < v.getRelationsCount(); j++) {
                    if (newVertices.indexOf(v.getRelation(j)) == -1) {
                        Line line = findLine(v.getId(), v.getRelation(j).getId(), GraphType.UNORIENTED);
                        if (minWeight > line.getWeight()) {
                            minWeight = line.getWeight();
                            minWeightLine = line;
                            newVertex = v.getRelation(j);
                        }
                    }
                }
            }
            if (minWeight != Integer.MAX_VALUE) {
                minWeightLine.setPaint(canvasField.paintGreen);
                linesTrace.append("[" + minWeightLine.getStartVertex().getId() + " - "
                        + minWeightLine.getEndVertex().getId() + "]\n");
                sum += minWeightLine.getWeight();
                newVertices.add(newVertex);
            } else
                isVertexAllowed = false;

        }

        canvasField.invalidate();

        String result = "Стартовая вершина: " + newVertices.get(0).getId() +
                        "\nСтоимость дерева: " + sum;

        //Записываем результаты в таблицу логов
        DatabaseHelper.pushNonResultQuery(activity.getBaseContext(), "INSERT INTO `lab8log` " +
                "(`logTime`, `startVertex`, `tree`, `treeCost`) VALUES ('" +
                Calendar.getInstance().getTime().toString() + "', '" +
                newVertices.get(0).getId() + "', '" +
                linesTrace.toString() + "', '" +
                sum + "');");

        //Выводим результаты в сообщении
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Результаты!")
                .setMessage(result)
                .setCancelable(false)
                .setNegativeButton("Ок",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                for(int i = 0; i<canvasField.getLinesCount(); i++)
                                    canvasField.getLine(i).setPaint(canvasField.paintBlack);

                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private Line findLine(int startVertexId, int endVertexId, GraphType graphType) {
        for (int i = 0; i < canvasField.getLinesCount(); i++) {
            if (canvasField.getLine(i).getStartVertex().getId() == startVertexId &&
                    canvasField.getLine(i).getEndVertex().getId() == endVertexId)
                return canvasField.getLine(i);
        }
        if (graphType == GraphType.UNORIENTED) {
            for (int i = 0; i < canvasField.getLinesCount(); i++) {
                if (canvasField.getLine(i).getStartVertex().getId() == endVertexId &&
                        canvasField.getLine(i).getEndVertex().getId() == startVertexId)
                    return canvasField.getLine(i);
            }
        }
        return null;
    }

    /**
     * Метод показывает логи в новом активити
     */

    public void showLogs() {
        Intent intent = new Intent(activity.getApplicationContext(), LogActivity.class);
        intent.putExtra("activityId", ActivityID.GRAPH_PRIMA_ALGORITHM.ordinal());
        activity.startActivity(intent);
    }

    /**
     * Метод зачищает логи
     */
    public void clearLogs() {
        DatabaseHelper.pushNonResultQuery(activity.getApplicationContext(),
                "DROP TABLE `lab8log`;");
        createDatabaseTable(activity.getApplicationContext());
    }

}
