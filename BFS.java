package com.company;

import jdk.nashorn.internal.ir.IfNode;

import java.util.ArrayList;
import java.util.Map;

public class Algorithms {

    /*----Lab-2--------------------------------------------------------------------------------------------------------*/

    public BFSAnswer BFS(int first, int last, ArrayList<ArrayList<Integer>> matrix){            //номер вершины откуда начинаем, где заканчиваем и сам
                                                                                                //и матрица инцидентности
        if(TestYourMatrix(matrix))return new BFSAnswer(false, new ArrayList<>());

        int size_mat = matrix.get(0).size();                                                    //размерность матрицы
        ArrayList<Integer> for_answ = new ArrayList<>();                                        //матрица для ответа
        for_answ.add(first);                                                                    //добавляем в нее вершину откуда ищем

        boolean[] used = new boolean[size_mat];                                                 //если true то вершину уже посетили, false - не посетили

        int[] queue = new int[size_mat];                                                        //очередь обработки вершин
        int queueHead = 0, queueTet = 0;                                                        //на какой вершине мы сейчас, где конец очереди

        used[first] = true;
        queue[queueTet++] = first;                                                              //добавляем первую вершину в очередь

        while(queueHead < queueTet){
            first = queue[queueHead++];                                                         //обрабатываем то что находится в очереди первым

            for(int i = 0; i < size_mat; i++) {                                                 //проходимся по всем элементам матрицы смежности
                if (!used[i] && matrix.get(first).get(i) != 0) {                                //проверяем были ли там уже и является ли вершина смежной
                    if(i == last) {                                                             //если можем попасть в конечную вершину
                        for (int j = 1; j < queueHead; j++)
                            for_answ.add(queue[j]);                                             //записываем что было в очереди и ответ
                        for_answ.add(last);
                        return new BFSAnswer(true, for_answ);
                    }
                    used[i] = true;                                                             //отмечаем что были в вершине
                    queue[queueTet++] = i;                                                      //добавляем в очередь чтобы потом обработать
                }
            }
        }
        for_answ = new ArrayList<>();
        return new BFSAnswer(false, for_answ);                                                                        //а вот как вернуть то что путь не был найен я не придумал)
    }

    /*----Lab-3--------------------------------------------------------------------------------------------------------*/

    public int[] Deikstra(int start, ArrayList<ArrayList<Integer>> matrix) {                    //Алгоритм Дейкстры

        int INFINITY = Integer.MAX_VALUE/2;                                                     //Представьте что это бесконечность
        int MatrixSize = matrix.get(0).size();                                                  //Размер матрицы смежности
        boolean[] Visited = new boolean[MatrixSize];                                            //Посещенные/непосещенные вершины
        int[] Distance = new int[MatrixSize];                                                   //Расстояние между начальной и остальными вершинами

        for(int i = 0; i < MatrixSize; i++)                                                     //Заполняем вееееесь массив расстояний бесконечностью
            Distance[i] = INFINITY;                                                             //кроме той с которой начинаем
        Distance[start] = 0;

        for(int i = 0; i < MatrixSize; i++)                                                     //Пройдемся по всей матрице
        {
            int Vdis = -1;

            for(int NVdis = 0; NVdis < MatrixSize; NVdis++)                                     //Перебираем вершины
                if(!Visited[NVdis] && Distance[NVdis] < INFINITY                                //и выбираем самую близкую + не посещенную
                        && (Vdis == -1 || Distance[Vdis] > Distance[NVdis]))
                    Vdis = NVdis;

            if(Vdis == -1) break;                                                               //Если так и осталось -1 значит либо уже рассмотрели,
            Visited[Vdis] = true;                                                               //либо вершина одинока

            for(int NVdis = 0; NVdis < MatrixSize; NVdis++)                                     //Снова перебор
                if (!Visited[NVdis] && matrix.get(Vdis).get(NVdis) < INFINITY &&                //Выбираем все смежные, не посещенные вершины и
                        matrix.get(Vdis).get(NVdis) != 0)
                    Distance[NVdis] = (Distance[NVdis] < Distance[Vdis] +                       //производим релаксацию или проще улучшаем оценку расстояния спасибо google.com за умные выражения
                            matrix.get(Vdis).get(NVdis))?Distance[NVdis]:
                            Distance[Vdis]+matrix.get(Vdis).get(NVdis);

        }
        return  Distance;
    }

    public int[][] DeikstraMatrix(ArrayList<ArrayList<Integer>> matrix){                        //Получаем матрицу Дейкстры
        int MatrixSize = matrix.get(0).size();                                                  //Ничего интересного, то же самое, только для каждой вершины
        int[][] for_result = new int[MatrixSize][MatrixSize];
        for(int i = 0; i < MatrixSize; i++)
            for_result[i] = Deikstra(i, matrix);

        return for_result;
    }

    /*----Lab-4--------------------------------------------------------------------------------------------------------*/


    /*----------------------------------------------------------------------------------------------------------Цэ-кит-*/
    //Служебные функции пусть будут туть
    public boolean TestYourMatrix(ArrayList<ArrayList<Integer>> matrix){
        for (int i = 0; i < matrix.get(0).size(); i++)
            for (int j = 0; j < matrix.get(0).size(); j++)
                if(matrix.get(i).get(j) > 1)return true;
        return false;
    }

}

class BFSAnswer{
    private boolean CorrectWork = true;
    private ArrayList<Integer> Matrix;

    BFSAnswer(boolean a, ArrayList<Integer> b){
        CorrectWork = a;
        Matrix = b;
    }
}
