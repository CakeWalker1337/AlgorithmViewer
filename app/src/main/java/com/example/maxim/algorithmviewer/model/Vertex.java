package com.example.maxim.algorithmviewer.model;

import java.util.ArrayList;

/**
 * Created by Maxim on 26.04.2018.
 */

public class Vertex {
    private float x;
    private float y;
    private float radius;
    private int id;
    private ArrayList<Vertex> relations; //массив связных вершин

    public Vertex(int id, float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.id = id;
        relations = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void addRelation(Vertex vertex) {
        relations.add(vertex);
    }

    public void removeRelation(int id) {
        relations.remove(id);
    }

    public Vertex getRelation(int index) {
        return relations.get(index);
    }

    public int getRelationsCount() {
        return relations.size();
    }

    public boolean isValid() {
        if (id == 0)
            return false;
        return true;
    }

    public void clear() {
        x = 0f;
        y = 0f;
        radius = 0f;
        id = 0;
    }

    /**
     * Копирует вершину графа в новый объект
     * @return возвращает копию текущей вершины
     */
    public Vertex copy() {
        Vertex v = new Vertex(id, x, y, radius);
        for (int i = 0; i < relations.size(); i++)
            v.addRelation(relations.get(i));

        return v;
    }

}
