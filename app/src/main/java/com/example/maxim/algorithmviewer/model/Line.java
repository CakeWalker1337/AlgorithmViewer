package com.example.maxim.algorithmviewer.model;

import android.graphics.Paint;

/**
 * Created by Maxim on 26.04.2018.
 */

public class Line {
    private Vertex startVertex, endVertex;
    private Paint paint;
    private int weight;

    public Line(Vertex startVertex, Vertex endVertex, Paint paint)
    {
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.paint = paint;
        this.weight = 0;
    }

    public void setStartVertex(Vertex startVertex){this.startVertex = startVertex;}
    public Vertex getStartVertex(){return this.startVertex;}

    public void setEndVertex(Vertex endVertex){this.endVertex = endVertex;}
    public Vertex getEndVertex(){return this.endVertex;}

    public void setPaint(Paint paint){this.paint = paint;}
    public Paint getPaint(){return this.paint;}

    public void setWeight(int weight){this.weight = weight;}
    public int getWeight(){return this.weight;}

    public Line copy()
    {
        Vertex v1 = new Vertex(startVertex.getId(), startVertex.getX(), startVertex.getY(), startVertex.getRadius());
        Vertex v2 = new Vertex(endVertex.getId(), endVertex.getX(), endVertex.getY(), endVertex.getRadius());
        Line newLine = new Line(v1, v2, paint);
        newLine.setWeight(weight);
        return newLine;

    }

    public boolean isValid()
    {
        if(startVertex.isValid() && endVertex.isValid())
            return true;
        return false;

    }

    public void clear()
    {
        startVertex.clear();
        endVertex.clear();
    }
}
