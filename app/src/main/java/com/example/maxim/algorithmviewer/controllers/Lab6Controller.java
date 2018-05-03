package com.example.maxim.algorithmviewer.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.example.maxim.algorithmviewer.activities.ActivityID;
import com.example.maxim.algorithmviewer.activities.ActivityLab6;
import com.example.maxim.algorithmviewer.activities.LogActivity;
import com.example.maxim.algorithmviewer.database.DatabaseHelper;
import com.example.maxim.algorithmviewer.model.CanvasField;
import com.example.maxim.algorithmviewer.model.Vertex;

import java.util.Calendar;

public class Lab6Controller {

    private boolean[] confirmedVertexes;
    private ActivityLab6 activity;
    private CanvasField canvasField;
    private StringBuilder result;

    public Lab6Controller(ActivityLab6 activity, CanvasField field) {
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
        DatabaseHelper.pushNonResultQuery(context, "CREATE TABLE IF NOT EXISTS `lab6log`(" +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `logTime` TEXT," +
                " `vertexSequence` TEXT," +
                " `DFSResult` TEXT);");
    }

    /**
     * Метод начинает обход построенного графа в глубину
     */
    public void start() {

        if (canvasField.getVertexesCount() == 0) {
            Toast.makeText(activity.getApplicationContext(), "No one vertex!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        result = new StringBuilder();
        confirmedVertexes = new boolean[canvasField.getVertexesCount() + 1];
        for (int i = 0; i < confirmedVertexes.length; i++)
            confirmedVertexes[i] = false;

        StringBuilder sourceGraph = new StringBuilder();
        for (int i = 0; i < canvasField.getVertexesCount(); i++) {
            sourceGraph.append(canvasField.getVertex(i).getId() + ": [");
            for (int j = 0; j < canvasField.getVertex(i).getRelationsCount(); j++) {
                sourceGraph.append(canvasField.getVertex(i).getRelation(j).getId() + " ");
            }
            sourceGraph.deleteCharAt(sourceGraph.length()-1)
                    .append("], ");
        }
        sourceGraph.delete(sourceGraph.length()-2, sourceGraph.length()-1);

        for (int i = 0; i < canvasField.getVertexesCount(); i++) {

            if (!confirmedVertexes[canvasField.getVertex(i).getId()]) {
                touchVertex(canvasField.getVertex(i));
            }

        }

        //Записываем результаты в таблицу логов
        DatabaseHelper.pushNonResultQuery(activity.getBaseContext(), "INSERT INTO `lab6log` " +
                "(`logTime`, `vertexSequence`, `DFSResult`) VALUES ('" +
                Calendar.getInstance().getTime().toString() + "', '" +
                sourceGraph.toString() + "', '" +
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

    /**
     * Рекурсивный метод объода вершин графа
     */
    private void touchVertex(Vertex v) {
        confirmedVertexes[v.getId()] = true;
        result.append(v.getId() + "");
        for (int i = 0; i < v.getRelationsCount(); i++) {
            if (!confirmedVertexes[v.getRelation(i).getId()]) {
                result.append(" -> ");
                touchVertex(v.getRelation(i));
            }
        }

    }

    /**
     * Метод показывает логи в новом активити
     */
    public void showLogs() {
        Intent intent = new Intent(activity.getApplicationContext(), LogActivity.class);
        intent.putExtra("activityId", ActivityID.GRAPH_DFS_ALGORITHM.ordinal());
        activity.startActivity(intent);
    }

    /**
     * Метод зачищает логи
     */
    public void clearLogs() {
        DatabaseHelper.pushNonResultQuery(activity.getApplicationContext(),
                "DROP TABLE `lab6log`;");
        createDatabaseTable(activity.getApplicationContext());
    }
}
