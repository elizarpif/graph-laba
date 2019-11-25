package com.company;

import java.util.ArrayList;

public class Algorithms {

    /*----Lab-2--------------------------------------------------------------------------------------------------------*/

    public BFSAnswer BFS(int first, int last, ArrayList<ArrayList<Integer>> matrix){            //номер вершины откуда начинаем, где заканчиваем и сам
                                                                                                //и матрица инцидентности
        if(TestYourMatrix(matrix))return new BFSAnswer(false, new ArrayList<>());

        int size_mat = matrix.get(0).size();                                                    //размерность матрицы

        boolean[] used = new boolean[size_mat];                                                 //если true то вершину уже посетили, false - не посетили

        ArrayList<Integer> queue = new ArrayList<>();                                           //очередь из вершин
        queue.add(first);                                                                       //добавляем вершину из которой начали обход

        int[] ancestor = new int[size_mat];                                                     //массив предков
        for (int i = 0; i < size_mat; i++)                                                      //заполняем -1 чтобы знать у каких вершин нет предков
            ancestor[i] = -1;
        ancestor[first] = first;                                                                //устанавливаем для начальной вершины в качестве предка саму себя

        used[first] = true;                                                                     //помечаем начальную вершину как посещенную

        while(!queue.isEmpty()){

            first = queue.get(queue.size()-1);                                                  //обрабатываем то что находится в очереди первым
            queue.remove(queue.size()-1);                                                       //удаляем из очереди
            for(int i = 0; i < size_mat; i++) {
                if (!used[i] &&  matrix.get(first).get(i) != 0) {                               //проверяем были ли там уже и является ли вершина смежной
                    used[i] = true;
                    queue.add(i);                                                               //добавляем в очередь чтобы потом обработать
                    ancestor[i] =  first;                                                       //устанавливаем предка для всех смежных вершин
                }
            }
        }

        if (ancestor[last] != -1)                                                               //если вершина в которую ищем путь -1 значит у нее нет предка
            return new BFSAnswer(true, WayBFS(last, ancestor, new ArrayList<>()));              //и нет пути до этой вершины
        return new BFSAnswer(false, new ArrayList<>());
    }

    public ArrayList<Integer> WayBFS(int last, int[] ancestor, ArrayList<Integer> MinimalLen){  //Записывает кратчайший путь от вершины до вершины
        if(ancestor[last] != last){                                                             //алгоритм поднимается к первому предку
            MinimalLen.add(last);                                                               //каждый раз мы проверяем есть ли предок у вершины
            WayBFS(ancestor[last], ancestor, MinimalLen);                                           
        } else
            MinimalLen.add(last);                                                               //когда предка нет, то добавляем самую первую вершину(откуда искали)

        ArrayList<Integer> for_answer = new ArrayList<>();
        for (int i = MinimalLen.size() - 1; i >= 0; i--)                                        //разворачиваем список вершин
            for_answer.add(MinimalLen.get(i));

        return for_answer;                                                          
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

    public boolean isCorrectWork(){
        return CorrectWork;
    }

    public ArrayList<Integer> Matrix(){
        return Matrix;
    }
}
