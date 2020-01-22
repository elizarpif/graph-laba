package com.company.core.algorithms;

import java.util.ArrayList;


public class ConnectivityGraph{
    public String Connectivity = "Не связный";
    public ArrayList<ArrayList<Integer>> ConnectivityComponent = new ArrayList<ArrayList<Integer>>();
    public int Bridge = 0;
    public int Hinge = 0;

    ConnectivityGraph(){}

    ConnectivityGraph(String connect, ArrayList<ArrayList<Integer>> connectcomponent, int bridge, int hinge){
        Connectivity = connect;
        ConnectivityComponent = connectcomponent;
        Bridge = bridge;
        Hinge = hinge;
    }

    public void AddComponent(ArrayList<Integer> component){ ConnectivityComponent.add(component); }

    public void AddBridge(){ Bridge++; }

    public ArrayList<ArrayList<Integer>> GetComponent(){ return ConnectivityComponent; }

    public int GetBridge(){ return Bridge; }
}
