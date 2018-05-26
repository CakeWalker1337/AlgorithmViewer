package com.example.maxim.algorithmviewer.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.example.maxim.algorithmviewer.activities.ActivityID;
import com.example.maxim.algorithmviewer.activities.ActivityLab7;
import com.example.maxim.algorithmviewer.activities.LogActivity;
import com.example.maxim.algorithmviewer.database.DatabaseHelper;
import com.example.maxim.algorithmviewer.model.CanvasField;
import com.example.maxim.algorithmviewer.model.GraphType;
import com.example.maxim.algorithmviewer.model.Line;
import com.example.maxim.algorithmviewer.model.Vertex;

import java.util.Calendar;

/**
 * Created by Maxim on 24.05.2018.
 */

public class Lab7Controller {

    private boolean[] confirmedVertexes;
    private int[] paths;
    private ActivityLab7 activity;
    private CanvasField canvasField;

    public Lab7Controller(ActivityLab7 activity, CanvasField field) {
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
        DatabaseHelper.pushNonResultQuery(context, "CREATE TABLE IF NOT EXISTS `lab7log`(" +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `logTime` TEXT," +
                " `result` TEXT);");
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
        int[] pathCosts = new int[canvasField.getVertexesCount() + 1];
        confirmedVertexes = new boolean[canvasField.getVertexesCount()+1];
        paths = new int[canvasField.getVertexesCount()+1];
        for(int i = 2; i<pathCosts.length; i++)
            pathCosts[i] = Integer.MAX_VALUE-10000;

        pathCosts[1] = 0;

        for(int i = 0; i<confirmedVertexes.length; i++) {
            confirmedVertexes[i] = false;
            paths[i] = 0;
        }

        int minWeight, currentVertexIndex = 0;
        for(int i = 0; i<canvasField.getVertexesCount(); i++)
        {
            minWeight = 9999999;
            currentVertexIndex = 0;
            for(int k = 0; k<canvasField.getVertexesCount(); k++)
            {
                if(!confirmedVertexes[canvasField.getVertex(k).getId()]
                        && pathCosts[canvasField.getVertex(k).getId()]<minWeight)
                {
                    minWeight = pathCosts[canvasField.getVertex(k).getId()];
                    currentVertexIndex = k;
                }
            }
            Vertex vertex = canvasField.getVertex(currentVertexIndex);
            for(int j = 0; j<vertex.getRelationsCount(); j++)
            {
                int relId = vertex.getRelation(j).getId();
                int lineWeight = findLine(vertex.getId(), relId, GraphType.ORIENTED).getWeight();
                if(pathCosts[vertex.getId()] + lineWeight  < pathCosts[relId]) {
                    pathCosts[relId] = pathCosts[vertex.getId()] + lineWeight;
                    paths[relId] = vertex.getId();
                }
            }
            confirmedVertexes[vertex.getId()] = true;
        }

        StringBuilder result = new StringBuilder();
        result.append("Кратчайшие расстояния: \n");
        for(int i = 1; i<pathCosts.length; i++)
            result.append("1 -> " + i + " = " + ((pathCosts[i] < Integer.MAX_VALUE-10000)?pathCosts[i]+"":"-1") + "\n");

        result.append("\nКратчайшие пути: \n");

        for(int i = 1; i<pathCosts.length; i++)
        {
            StringBuilder buf = new StringBuilder();
            int ind = i;
            buf.insert(0, ind);
            while(paths[ind] > 0)
            {
                buf.insert(0, paths[ind] + " -> ");
                ind = paths[ind];
            }
            result.append(buf.toString() + "\n");
        }

        //Записываем результаты в таблицу логов
        DatabaseHelper.pushNonResultQuery(activity.getBaseContext(), "INSERT INTO `lab7log` " +
                "(`logTime`, `result`) VALUES ('" +
                Calendar.getInstance().getTime().toString() + "', '" +
                result.toString() + "');");

        //Выводим результаты в сообщении
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Результаты!")
                .setMessage(result.toString())
                .setCancelable(false)
                .setNegativeButton("Ок",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
        intent.putExtra("activityId", ActivityID.GRAPH_DJIKSTRA_ALGORITHM.ordinal());
        activity.startActivity(intent);
    }

    /**
     * Метод зачищает логи
     */
    public void clearLogs() {
        DatabaseHelper.pushNonResultQuery(activity.getApplicationContext(),
                "DROP TABLE `lab7log`;");
        createDatabaseTable(activity.getApplicationContext());
    }
}
