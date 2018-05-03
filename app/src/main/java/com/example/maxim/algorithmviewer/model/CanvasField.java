package com.example.maxim.algorithmviewer.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class CanvasField extends View {

    private final float VERTEX_RADIUS = 70f;
    Paint paintVertex, paintText;
    Line tracer;
    ArrayList<Line> lines;
    Canvas canvas = null;
    private ArrayList<Vertex> vertexes;

    public CanvasField(Context context, AttributeSet attrs) {
        super(context, attrs);
        paintVertex = new Paint();
        paintVertex.setColor(Color.BLACK);
        paintVertex.setStrokeWidth(7f);

        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(40f);


        tracer = new Line(new Vertex(0, 0f, 0f, 0f), new Vertex(0, 0f, 0f, 0f), paintVertex);


        vertexes = new ArrayList<>();
        lines = new ArrayList<>();
    }

    public Vertex getVertex(int index) {
        return vertexes.get(index);
    }

    public int getVertexesCount() {
        return vertexes.size();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        if (vertexes.size() == 0)
            return;

        //Отрисовка линий - в первую очередь. Так они будут под всеми остальными фигурами
        for (int i = 0; i < lines.size(); i++) {
            canvas.drawLine(lines.get(i).getStartVertex().getX(),
                    lines.get(i).getStartVertex().getY(),
                    lines.get(i).getEndVertex().getX(),
                    lines.get(i).getEndVertex().getY(),
                    lines.get(i).getPaint());

        }

        //Отрисовка незаконченной линии, которую тянут сейчас
        if (tracer.getStartVertex().isValid())
            canvas.drawLine(tracer.getStartVertex().getX(),
                    tracer.getStartVertex().getY(),
                    tracer.getEndVertex().getX(),
                    tracer.getEndVertex().getY(),
                    tracer.getPaint());

        //Отрисовка вершин
        for (int i = 0; i < vertexes.size(); i++) {
            Vertex vertex = vertexes.get(i);
            canvas.drawCircle(vertex.getX(), vertex.getY(), vertex.getRadius(), paintVertex);

            //Изменение положения текста относительно центра вершины
            //в зависимости от величины текста
            if (i + 1 >= 10)
                canvas.drawText(vertex.getId() + "", vertex.getX() - 20f, vertex.getY() + 10f, paintText);
            else
                canvas.drawText(vertex.getId() + "", vertex.getX() - 10f, vertex.getY() + 10f, paintText);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.w("TOUCH", "DOWN");
            float x = event.getX();
            float y = event.getY();
            boolean canPaste = true;
            int touchedVertexIndex = -1;
            //Определяем, ткнули ли на какую-то вершину. Если да, то начинаем рисовать линию
            // иначе рисуем новую вершину
            Vertex currentVertex = new Vertex(vertexes.size() + 1, x, y, VERTEX_RADIUS);
            for (int i = 0; i < vertexes.size(); i++) {
                if (getDistanceBetweenVertexes(vertexes.get(i), currentVertex) < VERTEX_RADIUS * 2) {
                    canPaste = false;
                    touchedVertexIndex = i;
                    break;
                }
            }
            if (canPaste) {
                vertexes.add(currentVertex);
                invalidate();
            } else {
                tracer.setStartVertex(vertexes.get(touchedVertexIndex).copy());
            }

        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //Если тянут линию сейчас, меняем конец линии на текущую координату и отрисовываем
            if (tracer.getStartVertex().isValid()) {
                Log.w("DRAW", "DRAWING WHILE MOVING");
                tracer.getEndVertex().setX(event.getX());
                tracer.getEndVertex().setY(event.getY());
                invalidate();
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP && tracer.getStartVertex().isValid()) {
            //Определяем, довели ли линию до какой-либо вершины
            //Если довели, то сохраняем новую линию, а вершины связываем
            float x = event.getX();
            float y = event.getY();
            boolean isOnVertex = false;
            int touchedVertexIndex = -1;
            Vertex currentVertex = new Vertex(0, x, y, 0f);
            for (int i = 0; i < vertexes.size(); i++) {
                if (getDistanceBetweenVertexes(vertexes.get(i), currentVertex) < VERTEX_RADIUS &&
                        vertexes.get(i).getId() != tracer.getStartVertex().getId()) {
                    isOnVertex = true;
                    touchedVertexIndex = i;
                    break;
                }
            }
            if (isOnVertex) {
                if (tracer.getStartVertex().isValid()) {
                    tracer.setEndVertex(vertexes.get(touchedVertexIndex).copy());
                    if (!isLineAlreadyExists(tracer)) {
                        vertexes.get(touchedVertexIndex)
                                .addRelation(findVertexById(tracer.getStartVertex().getId()));
                        findVertexById(tracer.getStartVertex().getId())
                                .addRelation(findVertexById(tracer.getEndVertex().getId()));
                        lines.add(tracer.copy());
                    }
                }
            }
            tracer.clear();
            invalidate();
        }
        return true;
    }

    /**
     * Метод, осуществляющий поиск точки по id
     *
     * @param id - идентификатор точки для поиска
     * @return точку, если поиск успешен, иначе null
     */
    private Vertex findVertexById(int id) {
        for (int i = 0; i < vertexes.size(); i++) {
            if (vertexes.get(i).getId() == id)
                return vertexes.get(i);
        }
        return null;
    }

    public void clearCanvas() {
        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            lines.clear();
            vertexes.clear();
            tracer.clear();
            invalidate();
        }
    }

    /**
     * Метод, определяющий расстояние между двумя точками
     *
     * @param vertex1 - линия для проверки
     * @param vertex2 - линия для проверки
     * @return расстояние между точками
     */
    private double getDistanceBetweenVertexes(Vertex vertex1, Vertex vertex2) {
        return Math.sqrt((vertex1.getX() - vertex2.getX()) * (vertex1.getX() - vertex2.getX()) +
                (vertex1.getY() - vertex2.getY()) * (vertex1.getY() - vertex2.getY()));
    }

    /**
     * Метод, проверяющий, есть ли такая линия в массиве линий
     *
     * @param line - линия для проверки
     * @return true если есть, иначе false
     */
    private boolean isLineAlreadyExists(Line line) {
        for (int i = 0; i < lines.size(); i++) {
            if (isLinesEquals(lines.get(i), line))
                return true;
        }
        return false;
    }

    /**
     * Метод, проверяющий 2 линии на равенство
     *
     * @param line1 Первая линия для проверки
     * @param line2 Вторая линия для проверки
     * @return true если равны, иначе false
     */
    private boolean isLinesEquals(Line line1, Line line2) {
        if (line1.getStartVertex().getId() == line2.getStartVertex().getId() &&
                line1.getEndVertex().getId() == line2.getEndVertex().getId())
            return true;
        return false;
    }
}
