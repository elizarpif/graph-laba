package com.company.core.algorithms;

import java.util.ArrayList;

public  class AdditionalGraph{
    private boolean FullGraph;
    private ArrayList<ArrayList<Integer>> Matrix;

    AdditionalGraph(boolean fullgraph, ArrayList<ArrayList<Integer>> matrix){
        FullGraph = fullgraph;
        Matrix = matrix;
    }

    public boolean isFullGraph(){ return FullGraph; }

    public ArrayList<ArrayList<Integer>> GetGraph(){ return Matrix; }
}