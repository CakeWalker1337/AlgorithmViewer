package com.example.maxim.algorithmviewer.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.maxim.algorithmviewer.R;

import java.util.ArrayList;



public class CanvasField extends View {

    private final float VERTEX_RADIUS = 70f;
    private final float MARK_END_RADIUS = 10f;
    public Paint paintBlack, paintWhite, paintGreen;
    private Line tracer;
    private ArrayList<Line> lines;

    boolean isOriented = false, isWeightsEnabled = false;

    private Canvas canvas = null;
    private ArrayList<Vertex> vertexes;

    public CanvasField(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CanvasAttrs);

        isOriented = attributes.getBoolean(R.styleable.CanvasAttrs_isOriented, false);
        isWeightsEnabled = attributes.getBoolean(R.styleable.CanvasAttrs_isWeightsEnabled, false);

        attributes.recycle();

        paintBlack = new Paint();
        paintBlack.setColor(Color.BLACK);
        paintBlack.setStrokeWidth(7f);
        paintBlack.setTextSize(40f);

        paintGreen = new Paint();
        paintGreen.setColor(Color.argb(255, 20, 240, 20));
        paintGreen.setStrokeWidth(7f);

        paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setTextSize(40f);

        tracer = new Line(new Vertex(0, 0f, 0f, 0f), new Vertex(0, 0f, 0f, 0f), paintBlack);

        vertexes = new ArrayList<>();
        lines = new ArrayList<>();
    }

    public Vertex getVertex(int index) {
        return vertexes.get(index);
    }

    public int getVertexesCount() {
        return vertexes.size();
    }

    public Line getLine(int index) {
        return lines.get(index);
    }

    public int getLinesCount() {
        return lines.size();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        if (vertexes.size() == 0)
            return;

        //Отрисовка линий - в первую очередь. Так они будут под всеми остальными фигурами
        for(int i = 0; i < lines.size(); i++)
        {
            Vertex startVertex = lines.get(i).getStartVertex();
            Vertex endVertex = lines.get(i).getEndVertex();
            canvas.drawLine(startVertex.getX(),
                    startVertex.getY(),
                    endVertex.getX(),
                    endVertex.getY(),
                    lines.get(i).getPaint());
        }
        if(isOriented)
        {
            for (int i = 0; i < lines.size(); i++) {
                Vertex startVertex = lines.get(i).getStartVertex();
                Vertex endVertex = lines.get(i).getEndVertex();
                float x = endVertex.getX() - startVertex.getX();
                float y = endVertex.getY() - startVertex.getY();

                double distance = getDistanceBetweenVertexes(startVertex, endVertex);
                x = x / (float) distance;
                y = y / (float) distance;

                x = x * ((float) distance - lines.get(i).getStartVertex().getRadius() - MARK_END_RADIUS - 2f) + startVertex.getX();
                y = y * ((float) distance - lines.get(i).getStartVertex().getRadius() - MARK_END_RADIUS - 2f) + startVertex.getY();

                canvas.drawCircle(x, y, MARK_END_RADIUS + 3f, paintBlack);
                canvas.drawCircle(x, y, MARK_END_RADIUS, paintGreen);



            }
        }

        if(isWeightsEnabled)
        {
            for(int i = 0; i < lines.size(); i++)
            {
                Vertex startVertex = lines.get(i).getStartVertex();
                Vertex endVertex = lines.get(i).getEndVertex();
                float x = (startVertex.getX() + endVertex.getX()) / 2f;
                float y = (startVertex.getY() + endVertex.getY()) / 2f;

                Rect blackRect = new Rect((int) (x - 52f), (int) (y - 42f), (int) (x + 52f), (int) (y + 42f));
                Rect rect = new Rect((int) (x - 50f), (int) (y - 40f), (int) (x + 50f), (int) (y + 40f));
                canvas.drawRect(blackRect, paintBlack);
                canvas.drawRect(rect, paintWhite);

                paintBlack.getTextBounds(lines.get(i).getWeight()+"",
                        0,
                        (lines.get(i).getWeight()+"").length(),
                        rect);

                // Используем measureText для измерения ширины
                float textWidth = paintBlack.measureText(lines.get(i).getWeight()+"");
                float textHeight = rect.height();
                canvas.drawText(lines.get(i).getWeight() + "",
                        x - (textWidth / 2f),
                        y + (textHeight / 2f),
                        paintBlack);
            }

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
            canvas.drawCircle(vertex.getX(), vertex.getY(), vertex.getRadius(), paintBlack);

            //Изменение положения текста относительно центра вершины
            //в зависимости от величины текста
            Rect rect = new Rect();
            paintBlack.getTextBounds((i+1)+"",0, ((i+1)+"").length(), rect);
            float textWidth = paintBlack.measureText((i+1)+"");
            float textHeight = rect.height();

            canvas.drawText((i+1) + "",
                    vertex.getX() - (textWidth / 2f),
                    vertex.getY() + (textHeight / 2f),
                    paintWhite);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
                tracer.getEndVertex().setX(event.getX());
                tracer.getEndVertex().setY(event.getY());
                invalidate();
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP && tracer.getStartVertex().isValid()) {
            //Определяем, довели ли линию до какой-либо вершины
            //Если довели, то сохраняем новую линию, а вершины связываем

            final float x = event.getX();
            final float y = event.getY();


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
                    if(isWeightsEnabled) {
                        final int currentVertexId = touchedVertexIndex;
                        LayoutInflater li = LayoutInflater.from(getContext());
                        final View numberView = li.inflate(R.layout.dialog_layout, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Введите вес ребра")
                                .setMessage("Вес ребра:")
                                .setCancelable(false)
                                .setView(numberView)
                                .setNegativeButton("Создать",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String text = ((EditText) numberView.findViewById(R.id.numberView)).getText().toString();
                                                if (text.equals(""))
                                                    Toast.makeText(getContext(), "Неверный вес ребра!", Toast.LENGTH_LONG).show();
                                                else{
                                                    createLine(currentVertexId);
                                                    lines.get(lines.size() - 1).setWeight(Integer.parseInt(text));
                                                }
                                                tracer.clear();
                                                invalidate();
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else {
                        createLine(touchedVertexIndex);
                        tracer.clear();
                        invalidate();
                    }
                }
            }
            else
            {
                tracer.clear();
                invalidate();
            }

        }
        return true;
    }

    public void createLine(int currentVertexId)
    {
        tracer.setEndVertex(vertexes.get(currentVertexId).copy());
        if (!isLineAlreadyExists(tracer)) {
            if(!isOriented) {
                vertexes.get(currentVertexId)
                        .addRelation(findVertexById(tracer.getStartVertex().getId()));
            }
            findVertexById(tracer.getStartVertex().getId())
                    .addRelation(findVertexById(tracer.getEndVertex().getId()));
            lines.add(tracer.copy());
        }

    }

    /**
     * Метод, осуществляющий поиск точки по id
     *
     * @param id - идентификатор точки для поиска
     * @return точку, если поиск успешен, иначе null
     */
    public Vertex findVertexById(int id) {
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
