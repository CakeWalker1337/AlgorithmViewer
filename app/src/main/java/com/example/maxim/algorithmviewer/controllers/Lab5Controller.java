package com.example.maxim.algorithmviewer.controllers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.maxim.algorithmviewer.R;
import com.example.maxim.algorithmviewer.activities.ActivityID;
import com.example.maxim.algorithmviewer.activities.ActivityLab5;
import com.example.maxim.algorithmviewer.activities.LogActivity;
import com.example.maxim.algorithmviewer.database.DatabaseHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

/**
 * Created by Maxim on 22.04.2018.
 */

public class Lab5Controller {

    private final int NUM_OF_RANDOM_ELEMENTS = 20;

    ActivityLab5 activity;
    private final String FILENAMEA = "file1";
    private final String FILENAMEB = "file2";
    private final String FILENAMEC = "file3";
    private final String LOGTAG = "Lab5Controller";
    private int comparesCount = 0, swapsCount = 0;

    public StringBuilder iterationResultB, iterationResultC, mergeResult;

    public Lab5Controller(ActivityLab5 act) {
        activity = act;
        createDatabaseTable(act.getApplicationContext());
    }

    /**
     * Метод создает таблицу в БД для логов
     *
     * @param context - контекст приложения
     */
    private void createDatabaseTable(Context context) {
        DatabaseHelper.pushNonResultQuery(context, "CREATE TABLE IF NOT EXISTS `lab5log`(" +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `logTime` TEXT," +
                " `sourceSequence` TEXT," +
                " `sortedSequence` TEXT," +
                " `swapsCount` INTEGER DEFAULT '0'," +
                " `comparesCount` INTEGER DEFAULT '0');");

        DatabaseHelper.pushNonResultQuery(context, "CREATE TABLE IF NOT EXISTS `lab5logIterations`(" +
                " `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `logId` INTEGER DEFAULT '0'," +
                " `fileOne` TEXT NOT NULL," +
                " `fileTwo` TEXT NOT NULL," +
                " `mergedSequence` TEXT NOT NULL," +
                " `blockSize` INTEGER DEFAULT '0');");

    }

    /**
     * Старт работы алгоритма. Метод парсит строку и вытаскивает последовательность,
     * а также запускает процесс работы алгоритма.
     */
    public void start() {
        String data = ((EditText) activity.findViewById(R.id.inputSequenceBlock)).getText().toString();
        if (Objects.equals(data, "")) return;

        String[] result = data.split(" ");
        if (result.length <= 0) {
            ((TextView) activity.findViewById(R.id.statusBlock)).setText(R.string.wrongInputFormat);
            return;
        }
        int parseRes, i = 0, maxnum = -200, sum = 0;
        int[] mass = new int[result.length];
        for (i = 0; i < result.length; i++)    // parsing data
        {
            try {
                parseRes = Integer.parseInt(result[i]);
                if (parseRes >= -100 && parseRes <= 100) {
                    mass[i] = parseRes;
                } else
                    throw new NumberFormatException(activity.getString(R.string.incorrectFormatException));
            } catch (NumberFormatException e) {
                ((TextView) activity.findViewById(R.id.statusBlock)).setText(R.string.wrongInputFormat);
                return;
            }
        }

        startSorting(mass);

    }

    /**
     * Старт работы алгоритма рандомом. Метод генерирует 1000 случайных чисел, после чего
     * запускает процесс работы алгоритма.
     */
    public void randomStart() {
        Random r = new Random();
        int[] mass = new int[NUM_OF_RANDOM_ELEMENTS];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUM_OF_RANDOM_ELEMENTS; i++) // random massive filling
        {
            mass[i] = r.nextInt(NUM_OF_RANDOM_ELEMENTS + 1) - NUM_OF_RANDOM_ELEMENTS / 2;
            sb.append(mass[i] + " ");
        }
        ((EditText) activity.findViewById(R.id.inputSequenceBlock)).setText(sb.toString());

        startSorting(mass);
    }

    /**
     * Метод, в котором происходит работа алгоритма
     *
     * @param mass - массив, хранящий последовательность
     */
    public void startSorting(int[] mass) {
        try {

            BufferedWriter wA = new BufferedWriter(new OutputStreamWriter(
                    activity.openFileOutput(FILENAMEA, activity.MODE_PRIVATE)));
            StringBuilder sourceSequence = new StringBuilder();
            for (int i = 0; i < mass.length; i++) {

                wA.write(mass[i] + "");
                wA.newLine();
                sourceSequence.append(mass[i] + " ");
            }
            wA.close();

            DatabaseHelper.pushNonResultQuery(activity.getBaseContext(), "INSERT INTO `lab5log`" +
                    " (`logTime`) VALUES ('" + Calendar.getInstance().getTime().toString() + "');");

            int id = DatabaseHelper.getCountOfLogRows(activity.getApplicationContext(), ActivityID.EXTERNAL_SORT_ALGORITHMS);

            int block = 1, length = mass.length;
            while (block < length) {
                split(block);
                merge(block);

                DatabaseHelper.pushNonResultQuery(activity.getBaseContext(), "INSERT INTO `lab5logIterations` " +
                        "(`logId`, `fileOne`, `fileTwo`, `mergedSequence`, `blockSize`) " +
                        "VALUES ('" + id + "', '" + iterationResultB + "', '" + iterationResultC +
                        "', '" + mergeResult + "', '" + block + "');");

                block *= 2;
            }


            DatabaseHelper.pushNonResultQuery(activity.getBaseContext(), "UPDATE `lab5log` SET " +
                    "`sourceSequence` = '" + sourceSequence.toString() + "', " +
                    "`sortedSequence` = '" + mergeResult.toString() + "', " +
                    "`swapsCount` = '" + swapsCount + "', " +
                    "`comparesCount` = '" + comparesCount + "' " +
                    "WHERE `id` = '" + id + "';");

            ((EditText) activity.findViewById(R.id.inputSequenceBlock)).setText(mergeResult.toString());
            ((TextView) activity.findViewById(R.id.swapsCountBlock)).setText(activity.getString(R.string.comparesCount) + ": " + comparesCount);
            ((TextView) activity.findViewById(R.id.comparesCountBlock)).setText(activity.getString(R.string.swapsCount) + ": " + swapsCount);
            swapsCount = 0;
            comparesCount = 0;
            Log.w(LOGTAG, "Success");
        } catch (IOException ex) {
            Log.w(LOGTAG, "Файл не найден");
            ex.printStackTrace();
        }

    }

    /**
     * Метод сливает последовательности из двух файлов в один, параллельно сравнивая числа
     * последовательностей между собой.
     *
     * @param int - размерность блока
     */
    void merge(int block) {
        try {

            BufferedReader fB = new BufferedReader(new InputStreamReader(
                    activity.openFileInput(FILENAMEB)));

            iterationResultB = new StringBuilder();
            while (fB.ready()) {
                iterationResultB.append(fB.readLine() + " ");
            }

            fB.close();

            iterationResultC = new StringBuilder();

            BufferedReader fC = new BufferedReader(new InputStreamReader(
                    activity.openFileInput(FILENAMEC)));

            while (fC.ready()) {
                iterationResultC.append(fC.readLine() + " ");
            }

            fC.close();

            // отрываем потоки для записи и чтения
            BufferedWriter fileA = new BufferedWriter(new OutputStreamWriter(
                    activity.openFileOutput(FILENAMEA, activity.MODE_PRIVATE)));

            BufferedReader fileB = new BufferedReader(new InputStreamReader(
                    activity.openFileInput(FILENAMEB)));

            BufferedReader fileC = new BufferedReader(new InputStreamReader(
                    activity.openFileInput(FILENAMEC)));

            // объявление переменных для отсчета блоков
            int countB = 0, countC = 0;

            // объявление переменных для хранения чисел последовательностей
            int b = 1000, c = 1000;

            //переменные для указания, из какого файла считывать элемент
            int readyB = (fileB.ready()) ? 0 : 1, readyC = (fileC.ready()) ? 0 : 1;
            boolean flag = true;
            String buf;
            //Пока хотя бы один файл не опустошился
            while (fileB.ready() || fileC.ready()) {
                //если нужно считать из файла Б, считываем
                if (readyB == 0) {
                    buf = fileB.readLine();
                    b = Integer.parseInt(buf);
                    readyB = 1;
                }
                //если нужно считать из файла С, считываем
                if (readyC == 0) {
                    buf = fileC.readLine();
                    c = Integer.parseInt(buf);
                    readyC = 1;
                }
                // сравниваем числа, записываем наименьшее и делаем "запрос" на вызов нового числа
                // из файла, в котором был наименьший элемент
                if (b > c) {
                    fileA.write(c + "");
                    fileA.newLine();
                    readyC = 0;
                    c = 1000;
                    countC++;
                    swapsCount++;
                } else {
                    fileA.write(b + "");
                    fileA.newLine();
                    readyB = 0;
                    b = 1000;
                    countB++;
                    swapsCount++;
                }
                //проверка на конец блока в обоих файлах
                if (countB == block || (!fileB.ready())) {
                    readyB = 1;
                }
                if (countC == block || (!fileC.ready())) {
                    readyC = 1;
                }

                if (countC == block && countB == block) {
                    readyB = (fileB.ready()) ? 0 : 1;
                    readyC = (fileC.ready()) ? 0 : 1;
                    countB = 0;
                    countC = 0;
                }

                comparesCount++;

            }

            //Если произошел случай, когда элементы в файлах кончились раньше, чем кончился блок
            // закидываем оставшиеся неразобранные элементы
            if (b < 1000) {
                fileA.write(b + "");
                fileA.newLine();
            }
            if (c < 1000) {
                fileA.write(c + "");
                fileA.newLine();
            }

            //закрываем потоки
            fileA.close();
            fileB.close();
            fileC.close();

            BufferedReader fA = new BufferedReader(new InputStreamReader(
                    activity.openFileInput(FILENAMEA)));

            mergeResult = new StringBuilder();
            while (fA.ready()) {
                mergeResult.append(fA.readLine() + " ");
            }

            fA.close();


            Log.d(LOGTAG, "Файл A записан");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w(LOGTAG, "Файл не найден");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод разбивает последовательность одного файла на 2 файла.
     *
     * @param int - размерность блока
     */
    void split(int block) {

        try {
            // отрываем потоки для записи и чтения
            BufferedReader fileA = new BufferedReader(new InputStreamReader(
                    activity.openFileInput(FILENAMEA)));

            BufferedWriter fileB = new BufferedWriter(new OutputStreamWriter(
                    activity.openFileOutput(FILENAMEB, activity.MODE_PRIVATE)));

            BufferedWriter fileC = new BufferedWriter(new OutputStreamWriter(
                    activity.openFileOutput(FILENAMEC, activity.MODE_PRIVATE)));

            // флаг для указания файла для заполнения
            boolean isSwitched = false;
            String buf;
            int count = 0; //счетчик блока
            while (fileA.ready()) {
                buf = fileA.readLine();

                if (isSwitched) {
                    fileB.write(buf);
                    fileB.newLine();
                    count++;
                } else {
                    fileC.write(buf);
                    fileC.newLine();
                    count++;
                }
                if (count == block) {
                    isSwitched = !isSwitched;
                    count = 0;
                }

            }

            //Закрытие потоков
            fileA.close();
            fileB.close();
            fileC.close();

            Log.d(LOGTAG, "Файлы В и С записаны");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.w(LOGTAG, "Файл не найден");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод открывает новое активити и выводит в него логи.
     */
    public void showLogs() {
        Intent intent = new Intent(activity.getBaseContext(), LogActivity.class);
        intent.putExtra("activityId", ActivityID.EXTERNAL_SORT_ALGORITHMS.ordinal());
        activity.startActivity(intent);

    }

    /**
     * Метод зачищает логи
     */
    public void clearLogs() {
        DatabaseHelper.pushNonResultQuery(activity.getApplicationContext(), "DROP TABLE `lab5logIterations`;");
        DatabaseHelper.pushNonResultQuery(activity.getApplicationContext(), "DROP TABLE `lab5log`;");
        createDatabaseTable(activity.getApplicationContext());
    }


}
