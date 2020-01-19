package com.company.core.algorithms;

import java.util.ArrayList;

/*--------Ответ-для-лабы-2---------------------------------------------------------------------------------------------*/
public class BFSAnswer{
    private boolean CorrectWork = true;                                                         //Если алгоритм успешно завершен
    private ArrayList<Integer> Matrix;                                                          //Лист с путем

    BFSAnswer(boolean a, ArrayList<Integer> b){
        CorrectWork = a;
        Matrix = b;
    }

    public boolean isCorrectWork(){ return CorrectWork; }

    public ArrayList<Integer> Matrix(){ return Matrix; }
}
