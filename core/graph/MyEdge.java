package com.company.core.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

public class MyEdge extends DefaultWeightedEdge {

    @Override
    public String toString() {
        return String.valueOf((int)getWeight());
    }
}