package com.company.core.algorithms;

import java.util.ArrayList;

    public class EccentricityRD{
    private ArrayList<Integer> VertexWeight = new ArrayList<Integer>();                         //Вес каждой вершины или ее эксцентриситет
    private int Radius, Diametr;                                                                //Радиус, диаметр
    private int[] PowerVertex;                                                                  //Степени вершин

    EccentricityRD(ArrayList<Integer> verWeight, int rad, int diam, int[] power){
        VertexWeight = verWeight;
        Radius = rad;
        Diametr = diam;
        PowerVertex = power;
    }

    public ArrayList<Integer> GetVertexWeight(){ return VertexWeight; }

    public int GetRadius(){ return Radius; }

    public int GetDiametr(){ return Diametr; }

    public int[] GetPowerVertex(){ return PowerVertex; }
}