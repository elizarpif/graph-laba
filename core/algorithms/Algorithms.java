package com.company.core.algorithms;

import java.util.ArrayList;

public class Algorithms {

    /*----Lab-2--------------------------------------------------------------------------------------------------------*/

    public BFSAnswer BFS(int first, int last, ArrayList<ArrayList<Integer>> matrix){            //номер вершины откуда начинаем, где заканчиваем и сам
        //и матрица инцидентности
        if(TestYourMatrix(matrix, 1))return new BFSAnswer(false, new ArrayList<Integer>());

        int size_mat = matrix.get(0).size();                                                    //размерность матрицы

        boolean[] used = new boolean[size_mat];                                                 //если true то вершину уже посетили, false - не посетили

        ArrayList<Integer> queue = new ArrayList<Integer>();                                    //очередь из вершин
        queue.add(first);                                                                       //добавляем вершину из которой начали обход

        int[] ancestor = new int[size_mat];                                                     //массив предков
        for (int i = 0; i < size_mat; i++)                                                      //заполняем -1 чтобы знать у каких вершин нет предков
            ancestor[i] = -1;
        ancestor[first] = first;                                                                //устанавливаем для начальной вершины в качестве предка саму себя

        used[first] = true;                                                                     //помечаем начальную вершину как посещенную

        while(!queue.isEmpty()){

            first = queue.get(0);
            queue.remove(0);                                             //удаляем из очереди
            for(int i = 0; i < size_mat; i++) {
                if (!used[i] &&  matrix.get(first).get(i) != 0) {                               //проверяем были ли там уже и является ли вершина смежной
                    used[i] = true;
                    queue.add(i);                                                               //добавляем в очередь чтобы потом обработать
                    ancestor[i] =  first;                                                       //устанавливаем предка для всех смежных вершин
                }
            }
        }

        if (ancestor[last] != -1)                                                               //если вершина в которую ищем путь -1 значит у нее нет предка
            return new BFSAnswer(true, WayBFS(last, ancestor, new ArrayList<Integer>()));    //и нет пути до этой вершины
        return new BFSAnswer(false, new ArrayList<Integer>());
    }

    public ArrayList<Integer> WayBFS(int last, int[] ancestor, ArrayList<Integer> MinimalLen){  //Записывает кратчайший путь от вершины до вершины
        if(ancestor[last] != last){                                                             //алгоритм поднимается к первому предку
            MinimalLen.add(last);                                                               //каждый раз мы проверяем есть ли предок у вершины
            WayBFS(ancestor[last], ancestor, MinimalLen);
        } else
            MinimalLen.add(last);                                                               //когда предка нет, то добавляем самую первую вершину(откуда искали)

        ArrayList<Integer> for_answer = new ArrayList<Integer>();
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
                    Distance[NVdis] = Math.min(Distance[NVdis] , Distance[Vdis] +               //производим релаксацию или проще улучшаем оценку расстояния спасибо google.com за умные выражения
                            matrix.get(Vdis).get(NVdis));

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

    public EccentricityRD EccentricityRD(ArrayList<ArrayList<Integer>> matrix){                 //Эксцентриситет, радиус, диаметр
        int matrix_size = matrix.size();
        ArrayList<Integer> VertexWeight = new ArrayList<Integer>();                             //Вес вершин или их эксцентриситет


        int[][] findRadius = DeikstraMatrix(matrix);                                            //Матрица Дейкстры(расстояния до вершин)
        int Radius = 0, maxRadInRound = 0;
        for (int i = 0; i < matrix_size; i++) {
            for (int j = 0; j < matrix_size; j++)
                if (maxRadInRound < findRadius[i][j]) maxRadInRound = findRadius[i][j];         //Находим максимальное расстояние между вершинами

            Radius = (Radius == 0 || Radius > maxRadInRound) ? maxRadInRound : Radius;          //Радиус это минимальное из максимальных расстояний для каждой вершины
            VertexWeight.add(maxRadInRound);                                                    //Добавим эксцентриситет
            maxRadInRound = 0;
        }

        int Diametr = 0, maxDiamInRound = 0;
        for (int i = 0; i < matrix_size; i++) {
            for (int j = 0; j < matrix_size; j++)
                if (maxDiamInRound < findRadius[i][j]) maxDiamInRound = findRadius[i][j];       //Ищем максимальное расстояние
            Diametr = (Radius == 0 || Radius < maxDiamInRound) ? maxDiamInRound : Radius;       //Ищем диаметр
            maxDiamInRound = 0;
        }

        int[] PowerVertex = PowerVertex(matrix);                                                //Степени матриц

        return new EccentricityRD(VertexWeight, Radius, Diametr, PowerVertex);                  //Возвращаем ответ
    }

    /*----Lab-5--------------------------------------------------------------------------------------------------------*/

    public void Isomorphism(ArrayList<ArrayList<Integer>> matrix1, ArrayList<ArrayList<Integer>> matrix2){
        int[] PowerMatrix1 = PowerVertex(matrix1);                                              //Степени вершин матриц
        int[] PowerMatrix2 = PowerVertex(matrix2);

        if(PowerMatrix1 != PowerMatrix2)System.out.println("1");                                //Если вектора степеней матриц равны, то сильно неизоморфны

        ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>(), b = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < matrix1.size(); i++){
            //a.get(i).set(i, 1);
            //b.get(i).set(i, 1);
        }

        //b = TMatrix(b);
        //matrix1 = MatrixX(MatrixX(b, matrix1), a);

        if (matrix1 == matrix2)System.out.println("2");

    }

    /*----Lab-6--------------------------------------------------------------------------------------------------------*/

    public ConnectivityGraph ConnectivityGraph(ArrayList<ArrayList<Integer>> matrix, boolean GraphIsOriented){
        ConnectivityGraph ForAnswer = new ConnectivityGraph();

        int matrix_size = matrix.size();

        if(!GraphIsOriented) {
            boolean Connectivity = true;                                                        //Индикатор связности графа
            int[] distance = Deikstra(0, matrix);                                         //Расстояния до каждой вершины в графе

            for (int i = 0; i < distance.length; i++)                                           //Проверяем неориентированный граф на связность
                if(distance[i] >= Integer.MAX_VALUE/2)Connectivity = false;

            if(!Connectivity){                                                                  //Если граф несвязный, то ищем количество компонент связности
                int first = 0;
                int[] ancestor = new int[matrix_size];
                for (int i = 0; i < matrix_size; i++)
                    ancestor[i] = -1;

                while(first != -1) {
                    ancestor[first] = first;

                    ArrayList<Integer> queue = new ArrayList<Integer>();
                    queue.add(first);

                    boolean[] used = new boolean[matrix_size];
                    used[first] = true;

                    while (!queue.isEmpty()) {

                        first = queue.get(queue.size() - 1);
                        queue.remove(queue.size() - 1);
                        for (int i = 0; i < matrix_size; i++) {
                            if (!used[i] && matrix.get(first).get(i) != 0) {
                                used[i] = true;
                                queue.add(i);
                                ancestor[i] = first;
                            }
                        }
                    }

                    ArrayList<Integer> component = new ArrayList<Integer>();
                    for (int i = 0; i < matrix_size; i++)
                        if (ancestor[i] >= 0) {
                            component.add(i);
                            ancestor[i] = -2;
                        }

                    for (int i = 0; i < matrix_size; i++)
                        if (ancestor[i] == -1){
                            first = i;
                            break;
                        } else first = -1;

                    ForAnswer.AddComponent(component);
                }
            }
            for (int i = 0; i < ForAnswer.GetComponent().size(); i++)
            {
                for (int j = i; j < ForAnswer.GetComponent().get(i).size(); j++){
                    int chek = 0;
                    for (int k = j; k < ForAnswer.GetComponent().get(i).size(); k++)
                        if(matrix.get(j).get(k) > 0)chek++;
                    if (chek <= 1)ForAnswer.AddBridge();
                }
            }
        }
        return ForAnswer;
    }

    /*----Lab-7--------------------------------------------------------------------------------------------------------*/

    public AdditionalGraph AdditionGraph(ArrayList<ArrayList<Integer>> matrix){
        int matrix_size = matrix.size();

        for (int i = 0; i < matrix_size; i++)
            for (int j = 0; j < matrix_size; j++)
                matrix.get(i).set(j, (matrix.get(i).get(j) == 0)?1:0);                          //Меняем все значения на противоположные, чтобы получить дополнение

        return new AdditionalGraph(!TestYourMatrix(matrix, 0), matrix);                      //TestYourMatrix проверит всеэлементы и если везде был 0
    }                                                                                           //то начальный граф полный

    /*----Lab-8--------------------------------------------------------------------------------------------------------*/

    public ArrayList<ArrayList<Integer>> BinaryOperations(ArrayList<ArrayList<Integer>> Matrix1, ArrayList<ArrayList<Integer>> Matrix2, int LogicalFunction){
        int matrix_size = Matrix1.size();
        if(matrix_size != Matrix2.size())return new ArrayList<ArrayList<Integer>>(0);
        ArrayList<ArrayList<Integer>> ResultArray = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < matrix_size; i++) {
            ArrayList<Integer> ResultString = new ArrayList<Integer>();
            for (int j = 0; j < matrix_size; j++) {
                switch (LogicalFunction) {
                    case 1:                                                                     //Объединение
                        ResultString.add((Matrix1.get(i).get(j) > 0 || Matrix2.get(i).get(j) > 0)?1:0);
                        break;
                    case 2:
                        ResultString.add((Matrix1.get(i).get(j) > 0 && Matrix2.get(i).get(j) > 0)?1:0);
                        break;
                    case 3:
                        int numba = Matrix2.get(i).get(j) - Matrix1.get(i).get(j);
                        ResultString.add((numba > 0)?1:0);
                        break;
                    case 4:
                        int numab = Matrix1.get(i).get(j) - Matrix2.get(i).get(j);
                        ResultString.add((numab > 0)?1:0);
                        break;
                    case 5:
                        ResultString.add((Matrix1.get(i).get(j) <= Matrix2.get(i).get(j))?1:0);
                        break;
                    case 6:
                        ResultString.add((Matrix2.get(i).get(j) <= Matrix1.get(i).get(j))?1:0);
                        break;
                    case 7:
                        ResultString.add((Matrix1.get(i).get(j) > Matrix2.get(i).get(j))?1:0);
                        break;
                    case 8:
                        ResultString.add((Matrix2.get(i).get(j) > Matrix1.get(i).get(j))?1:0);
                        break;
                    case 9:
                        ResultString.add((Matrix1.get(i).get(j) == Matrix2.get(i).get(j))?0:1);
                        break;
                    case 10:
                        ResultString.add((Matrix2.get(i).get(j) == Matrix1.get(i).get(j))?1:0);
                        break;
                    case 11:
                        ResultString.add((Matrix2.get(i).get(j) * Matrix1.get(i).get(j) > 0)?0:1);
                        break;
                    case 12:
                        ResultString.add((Matrix2.get(i).get(j) == 0 && Matrix1.get(i).get(j) == 0)?1:0);
                        break;
                }
            }
            ResultArray.add(ResultString);
        }
        return ResultArray;
    }

    /*----------------------------------------------------------------------------------------------------------Цэ-кит-*/

    /*----Служебные-функции-пусть-будут-туть---------------------------------------------------------------------------*/
    public boolean TestYourMatrix(ArrayList<ArrayList<Integer>> matrix, int a){
        for (int i = 0; i < matrix.get(0).size(); i++)
            for (int j = 0; j < matrix.get(0).size(); j++)
                if(matrix.get(i).get(j) > a)return true;
        return false;
    }

    public int[] PowerVertex(ArrayList<ArrayList<Integer>> matrix) {                            //Степень матриц
        int[] answer = new int[matrix.size()];

        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.size(); j++)
                if (matrix.get(i).get(j) > 0)                                                   //Если вершина смежная
                    answer[i] += (i == j) ? 2 : 1;                                              //Если вершина смежна себе то +2, если смежна с другой +1
        }
        return answer;
    }

    public ArrayList<ArrayList<Integer>> TMatrix(ArrayList<ArrayList<Integer>> matrix){
        for (int i = 0; i < matrix.size(); i++)
            for (int j = i; j < matrix.size(); j++){
                int a = matrix.get(i).get(j);
                matrix.get(i).set(j, matrix.get(j).get(i));
                matrix.get(j).set(i, a);
            }
        return matrix;
    }

    public ArrayList<ArrayList<Integer>> MatrixX(ArrayList<ArrayList<Integer>> a, ArrayList<ArrayList<Integer>> b){
        for (int i = 0; i < a.size(); i++)
            for (int j = 0; j < a.size(); j++){
                int z = 0;
                for (int k = 0; k < a.size(); k++){
                    z += a.get(i).get(k) * b.get(j).get(k);
                }
                a.get(i).set(j, z);
            }
        return a;
    }

}

/*--------Ответ-для-лабы-4---------------------------------------------------------------------------------------------*/

/*--------Ответ-для-лабы-6---------------------------------------------------------------------------------------------*/
/*--------Ответ-для-лабы-7---------------------------------------------------------------------------------------------*/

