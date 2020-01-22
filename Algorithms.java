package com.company;

import java.util.ArrayList;

public class Algorithms {

    /*----Lab-2--------------------------------------------------------------------------------------------------------*/

    public BFSAnswer BFS(int first, int last, ArrayList<ArrayList<Integer>> matrix){            //номер вершины откуда начинаем, где заканчиваем и сам
        //и матрица инцидентности
        if(TestYourMatrix(matrix, 1))return new BFSAnswer(false, new ArrayList<Integer>());

        int size_mat = matrix.get(0).size();                                                    //размерность матрицы

        boolean[] used = new boolean[size_mat];                                                 //если true то вершину уже посетили, false - не посетили

        ArrayList<Integer> queue = new ArrayList<Integer>();                                          //очередь из вершин
        queue.add(first);                                                                       //добавляем вершину из которой начали обход

        int[] ancestor = new int[size_mat];                                                     //массив предков
        for (int i = 0; i < size_mat; i++)                                                      //заполняем -1 чтобы знать у каких вершин нет предков
            ancestor[i] = -1;
        ancestor[first] = first;                                                                //устанавливаем для начальной вершины в качестве предка саму себя

        used[first] = true;                                                                     //помечаем начальную вершину как посещенную

        boolean exit = false;
        while(!queue.isEmpty()){

            first = queue.get(0);                                                  //обрабатываем то что находится в очереди первым
            queue.remove(0);                                                 //удаляем из очереди
            for(int i = 0; i < size_mat; i++) {
                if (!used[i] && matrix.get(first).get(i) != 0) {                               //проверяем были ли там уже и является ли вершина смежной
                    used[i] = true;
                    queue.add(i);                                                               //добавляем в очередь чтобы потом обработать
                    ancestor[i] = first;                                                       //устанавливаем предка для всех смежных вершин

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
            Diametr = (Diametr == 0 || Diametr < maxDiamInRound) ? maxDiamInRound : Diametr;       //Ищем диаметр
            maxDiamInRound = 0;
        }

        int[] PowerVertex = PowerVertex(matrix);                                                //Степени матриц

        return new EccentricityRD(VertexWeight, Radius, Diametr, PowerVertex);                  //Возвращаем ответ
    }

    /*----Lab-5--------------------------------------------------------------------------------------------------------*/

    public IsomorphismAnswer Isomorphism(ArrayList<ArrayList<Integer>> matrix1, ArrayList<ArrayList<Integer>> matrix2){
        int[] PowerMatrix1 = PowerVertex(matrix1);                                              //Степени вершин матриц
        int[] PowerMatrix2 = PowerVertex(matrix2);

        if(PowerMatrix1.equals(PowerMatrix2))return new IsomorphismAnswer("Сильно неизоформны");
        ArrayList<ArrayList<Integer>> ulman = new ArrayList<ArrayList<Integer>>();

        for(int i = 0; i < PowerMatrix1.length; i++){
            ArrayList<Integer> rec = new ArrayList<Integer>();
            for (int j = 0; j < PowerMatrix1.length; j++)
                rec.add((PowerMatrix1[i] != 0 || PowerMatrix2[i] != 0)?1:0);
            ulman.add(rec);
        }

        for(int i = 0; i < PowerMatrix1.length; i++){
            for(int j = 0; j < PowerMatrix1.length; j++){
                if(ulman.get(i).get(j) == 0)return new IsomorphismAnswer("Неизоморфны");
            }
        }
        return new IsomorphismAnswer("Изоморфны");
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

        if(!TestYourMatrix(matrix, 0)){
            for (int i = 0; i < matrix_size; i++)
                for (int j = 0; j < matrix_size; j++)
                    matrix.get(i).set(j, (matrix.get(i).get(j) == 0)?1:0);

            return new AdditionalGraph(false, matrix);
        }

        return new AdditionalGraph(true, matrix);
    }

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

    /*----Lab-9--------------------------------------------------------------------------------------------------------*/

    public PlanarityAnswer Planarity(ArrayList<ArrayList<Integer>> matrix){
        int matrix_size = matrix.size();

        return new PlanarityAnswer(true, true, new ArrayList<ArrayList<Integer>>());
    }

    /*----Lab-10--------------------------------------------------------------------------------------------------------*/

    /*----Lab-11--------------------------------------------------------------------------------------------------------*/

    /*----Lab-12--------------------------------------------------------------------------------------------------------*/

    public SpanningTreeAnswer SpanningTree(ArrayList<ArrayList<Integer>> matrix, int VersionAlgorithm){
        int matrix_size = matrix.size();
        VersionAlgorithm = 1;
        switch (VersionAlgorithm){
            case 1:
                ArrayList<Integer> first = new ArrayList<Integer>(), second = new ArrayList<Integer>();
                ArrayList<Boolean> used = new ArrayList<Boolean>(matrix_size);
                ArrayList<Integer> min_e = new ArrayList<Integer>(matrix_size), sel_e  = new ArrayList<Integer>(matrix_size);
                for(int i = 0; i < matrix_size; i++) {
                    used.add(false);
                    min_e.add(Integer.MAX_VALUE/2);
                    sel_e.add(-1);
                }

                min_e.set(0, 0);
                for(int i = 0; i < matrix_size; i++){
                    int vertex = -1;
                    for (int j = 0; j < matrix_size; j++)
                        if(!used.get(j) && (vertex == -1 || min_e.get(j) < min_e.get(vertex)))
                            vertex = j;

                    if(min_e.get(vertex) == Integer.MAX_VALUE/2){
                        System.out.println("Нельзя построить");
                        return new SpanningTreeAnswer(false, new ArrayList<Integer>(), new ArrayList<Integer>());
                    }

                    used.set(vertex, true);

                    if(sel_e.get(vertex) != -1) {
                        first.add(sel_e.get(vertex));
                        second.add(vertex);
                    }

                    for (int j = 0; j < matrix_size; j++)
                        if(matrix.get(vertex).get(j) != 0 && matrix.get(vertex).get(j) < min_e.get(j)){
                            min_e.set(j, matrix.get(vertex).get(j));
                            sel_e.set(j, vertex);
                        }
                }
                return new SpanningTreeAnswer(true, first, second);

        }
        return new SpanningTreeAnswer(false, new ArrayList<Integer>(), new ArrayList<Integer>());
    }

    /*----Lab-13--------------------------------------------------------------------------------------------------------*/

    public CycleProblemAnswer CycleProblem(ArrayList<ArrayList<Integer>> matrix){

        int size_mat = matrix.get(0).size();
        int first = 0, cycle = 0;
        boolean[] used = new boolean[size_mat];

        ArrayList<Integer> queue = new ArrayList<Integer>();
        queue.add(first);

        int[] ancestor = new int[size_mat];
        for (int i = 0; i < size_mat; i++)
            ancestor[i] = -1;
        ancestor[first] = first;

        used[first] = true;

        boolean exit = false;
        ArrayList<Integer> forAnsw = new ArrayList<Integer>();
        while(!queue.isEmpty()) {

            first = queue.get(0);
            queue.remove(0);
            for (int i = 0; i < size_mat; i++) {
                if (!used[i] && matrix.get(first).get(i) != 0) {
                    used[i] = true;
                    queue.add(i);
                    ancestor[i] = first;
                }
            }

            for (int i = 0; i < queue.size()-1; i++) {
                for (int j = i; j < queue.size(); j++) {
                    forAnsw.add(queue.get(j));
                    if (queue.get(i) == queue.get(i+1) && used[j]) {
                        forAnsw.add(queue.get(0));
                        return new CycleProblemAnswer(true, 0, 0, 0, forAnsw);
                    }
                }
            }
        }
        float b = 0;
        for (int i = 2; i < ancestor.length; i++)
            b += b *10 + ancestor[i];

        int[][] TreeCenter = DeikstraMatrix(matrix);
        int vertexnum = -1, min = Integer.MAX_VALUE/2;

        for (int i = 0; i < TreeCenter.length; i++) {
            int max = 0;
            for (int j = 0; j < TreeCenter.length; j++)
                if(TreeCenter[i][j] > max)
                    max = TreeCenter[i][j];

            if(min > max){
                min = max;
                vertexnum = i;
            }
        }
        return new CycleProblemAnswer(false, vertexnum, min, b, new ArrayList<Integer>());
    }

    /*----Lab-14--------------------------------------------------------------------------------------------------------*/
    public ColoringGraphAnswer ColoringGraph(ArrayList<ArrayList<Integer>> matrix){
        int size_mat = matrix.get(0).size();
        int color_index = 0;
        boolean[] used = new boolean[size_mat];
        int[] colored = new int[size_mat];

        for (int i = 0; i < size_mat; i++)
            used[i] = false;

        for(int i = 0; i < size_mat; i++){
            if(!used[i]){
                used[i] = true;
                colored[i] = color_index;
                for(int j = i + 1; j < size_mat; j++)
                {
                    if(matrix.get(i).get(j) == 0){
                        used[j] = true;
                        colored[j] = color_index;
                        for(int k = j; k < size_mat; k++)
                            matrix.get(i).set(k, (matrix.get(i).get(k) == 0 && matrix.get(j).get(k) == 0)?0:1);
                    }
                }
                color_index++;
            }
        }
        return new ColoringGraphAnswer(color_index, colored);
    }

    /*----Lab-15--------------------------------------------------------------------------------------------------------*/

    public WeddingTaskAnswer WeddingTask(ArrayList<ArrayList<Integer>> matrix, int[] FirstShare, int[] SecondShare){
        int size_mat = matrix.get(0).size();

        boolean[] used = new boolean[size_mat];
        int[] modtwo = new int[size_mat];
        int first = FirstShare[0];
        ArrayList<Integer> queue = new ArrayList<Integer>();
        queue.add(first);
        modtwo[first] = 1;


        int[] ancestor = new int[size_mat];
        for (int i = 0; i < size_mat; i++)
            ancestor[i] = -1;
        ancestor[first] = first;

        used[first] = true;

        while(!queue.isEmpty()){
            System.out.println(queue);
            first = queue.get(0);
            queue.remove(0);
            for(int i = 0; i < size_mat; i++) {
                if (!used[i] && matrix.get(first).get(i) != 0) {
                    used[i] = true;
                    queue.add(i);
                    ancestor[i] = first;
                    modtwo[i] = (modtwo[first] == 1)?2:1;
                }
            }
        }
        for(int i = 0; i < size_mat; i++)
            if(modtwo[i] == 1){
                if(!FirstShare.equals(i))return new WeddingTaskAnswer(false, false);
            }
        return new WeddingTaskAnswer(true, FirstShare.length == SecondShare.length);

    }
    /*----Lab-20--------------------------------------------------------------------------------------------------------*/

    public DominanceOverwhelmingMultitudeAnswer DominanceOverwhelmingMultitude(ArrayList<ArrayList<Integer>> matrix){
        int size_mat = matrix.get(0).size(), DominCounter = 0, PowerVertexCounter = 0, PowerEdgesCounter = -1, MinPEC = 0, MinPVC = 0;
        int[] Domin = new int[size_mat], powerVertex = new int[size_mat], powerEdges = new int[size_mat];
        boolean[] used = new boolean[size_mat], powerused = new boolean[size_mat], minpowerused = new boolean[size_mat];

        for (int i = 0; i < size_mat; i++)
            for (int j = 0; j < size_mat; j++) {
                Domin[i] += (matrix.get(i).get(j) == 0) ? 0 : 1;
                powerVertex[i] += (matrix.get(i).get(j) == 0) ? 0 : 1;
                powerEdges[i] += (matrix.get(i).get(j) == 0) ? 0 : 1;
            }

        for (int i = 0; i < size_mat; i++){
            int max = 0, nummax = 0;
            for(int k = 0; k < size_mat; k++)
                if(Domin[k] > max && !used[k]) {
                    max = Domin[k];
                    nummax = k;
                }

            Domin[nummax] = 0;
            used[nummax] = true;
            if(max == 0){
                break;
            } else {
                for (int k = 0; k < size_mat; k++) {
                    if (matrix.get(nummax).get(k) != 0) used[k] = true;
                }
                DominCounter++;
            }
        }

        for (int i = 0; i < size_mat; i++){
            int max = Integer.MAX_VALUE/2, nummax = Integer.MAX_VALUE/2;
            for(int k = 0; k < size_mat; k++)
                if(powerVertex[k] < max && !powerused[k]) {
                    max = powerVertex[k];
                    nummax = k;
                }

            if(max == Integer.MAX_VALUE/2){
                break;
            } else {
                powerVertex[nummax] = 0;
                for (int k = 0; k < size_mat; k++)
                    if (matrix.get(nummax).get(k) != 0 && !powerused[k]){
                        powerused[k] = true;
                        PowerEdgesCounter++;
                    }

                PowerVertexCounter++;
            }
        }

        for (int i = 0; i < size_mat; i++){
            int max = 0, nummax = 0;
            for(int k = 0; k < size_mat; k++)
                if(powerEdges[k] > max && !minpowerused[k]) {
                    max = powerEdges[k];
                    nummax = k;
                }

            powerEdges[nummax] = 0;
            minpowerused[nummax] = true;
            if(max == 0){
                break;
            } else {
                for (int k = 0; k < size_mat; k++) {
                    if (matrix.get(nummax).get(k) != 0 && !minpowerused[k]){
                        minpowerused[k] = true;
                        MinPEC++;
                    }
                }
                MinPVC++;
            }
        }

        return new DominanceOverwhelmingMultitudeAnswer(DominCounter, PowerVertexCounter, PowerEdgesCounter, MinPVC, MinPEC);
    }

    /*----------------------------------------------------------------------------------------------------------Цэ-кит-*/

    /*----Служебные-функции-пусть-будут-туть---------------------------------------------------------------------------*/
    public boolean TestYourMatrix(ArrayList<ArrayList<Integer>> matrix, int a){
        for (int i = 0; i < matrix.get(0).size(); i++)
            for (int j = 0; j < matrix.get(0).size(); j++)
                if(!(matrix.get(i).get(j) > a))return false;
        return true;
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

/*--------Ответ-для-лабы-2---------------------------------------------------------------------------------------------*/
class BFSAnswer{
    private boolean CorrectWork = true;                                                         //Если алгоритм успешно завершен
    private ArrayList<Integer> Matrix;                                                          //Лист с путем

    BFSAnswer(boolean a, ArrayList<Integer> b){
        CorrectWork = a;
        Matrix = b;
    }

    public boolean isCorrectWork(){ return CorrectWork; }
    public ArrayList<Integer> Matrix(){ return Matrix; }
}
/*--------Ответ-для-лабы-4---------------------------------------------------------------------------------------------*/
class EccentricityRD{
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
/*--------Ответ-для-лабы-5---------------------------------------------------------------------------------------------*/
class IsomorphismAnswer{
    String isomorph;

    IsomorphismAnswer(String isom){
        isomorph = isom;
    }

    public String GetIsomorph(){ return isomorph; }
}
/*--------Ответ-для-лабы-6---------------------------------------------------------------------------------------------*/
class ConnectivityGraph{
    private String Connectivity = "Не связный";
    private ArrayList<ArrayList<Integer>> ConnectivityComponent = new ArrayList<ArrayList<Integer>>();
    private int Bridge = 0;
    private int Hinge = 0;

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
/*--------Ответ-для-лабы-7---------------------------------------------------------------------------------------------*/
class AdditionalGraph{
    private boolean FullGraph;
    private ArrayList<ArrayList<Integer>> Matrix;

    AdditionalGraph(boolean fullgraph, ArrayList<ArrayList<Integer>> matrix){
        FullGraph = fullgraph;
        Matrix = matrix;
    }

    public boolean isFullGraph(){ return FullGraph; }
    public ArrayList<ArrayList<Integer>> GetGraph(){ return Matrix; }
}
/*--------Ответ-для-лабы-9---------------------------------------------------------------------------------------------*/
class PlanarityAnswer{
    private boolean Flat, Planarity;
    private ArrayList<ArrayList<Integer>> Matrix;

    PlanarityAnswer(boolean flat, boolean planarity, ArrayList<ArrayList<Integer>> matrix){
        Flat = flat;
        Planarity = planarity;
        Matrix = matrix;
    }

    public boolean isFlat(){ return Flat;}
    public boolean isPlanarity(){ return Planarity;}
    public ArrayList<ArrayList<Integer>> GetDualGraph(){ return Matrix;}
}
/*--------Ответ-для-лабы-12---------------------------------------------------------------------------------------------*/
class SpanningTreeAnswer{
    private boolean AlghorithmWork;
    private ArrayList<Integer> FirstVertex, SecondVertex;

    SpanningTreeAnswer(boolean workstat, ArrayList<Integer> first, ArrayList<Integer> second){
        AlghorithmWork = workstat;
        FirstVertex = new ArrayList<Integer>(first);
        SecondVertex = new ArrayList<Integer>(second);
    }

    public ArrayList<Integer> GetFirstVertex(){ return FirstVertex; }
    public ArrayList<Integer> GetSecondVertex(){ return SecondVertex; }
    public boolean isCorrectWork(){ return AlghorithmWork; }

}
/*--------Ответ-для-лабы-13---------------------------------------------------------------------------------------------*/
class CycleProblemAnswer{
    private boolean HavingCycle;
    private int VertexCenter, Depth;
    private float PrefuerCode;
    private ArrayList<Integer> MinCycle;

    CycleProblemAnswer(boolean havecycle, int center, int depth, float code, ArrayList<Integer> mincycle){
        HavingCycle = havecycle;
        VertexCenter = center;
        Depth = depth;
        PrefuerCode = code;
        MinCycle = mincycle;
    }

    public boolean HavingCycle(){ return HavingCycle; }
    public int GetVertexCenter(){ return VertexCenter; }
    public int GetDepth(){ return Depth; }
    public float GetPrefuerCode(){ return PrefuerCode; }
    public ArrayList<Integer> GetMinCycle(){ return MinCycle; }
}
/*--------Ответ-для-лабы-14---------------------------------------------------------------------------------------------*/
class ColoringGraphAnswer{
    private int ColorIndex;
    private int[] ColoredNum;

    ColoringGraphAnswer(int index, int[] colored){
        ColorIndex = index;
        ColoredNum = colored;
    }

    public int GetColorIndex(){ return ColorIndex; }
    public int[] GetColoredNum(){ return ColoredNum; }
}
/*--------Ответ-для-лабы-15---------------------------------------------------------------------------------------------*/
class WeddingTaskAnswer{
    boolean bipartite, wedding;

    WeddingTaskAnswer(boolean bip, boolean wed){
        bipartite = bip;
        wedding = wed;
    }

    public boolean isBipartite(){ return bipartite; }
    public boolean isWedding(){ return wedding; }

}
/*--------Ответ-для-лабы-20---------------------------------------------------------------------------------------------*/
class DominanceOverwhelmingMultitudeAnswer{
    private int Dominance, PowerVertex, PowerEdges, MinPowerGraph, MinPowerVertex;

    DominanceOverwhelmingMultitudeAnswer(int dom, int pv, int pe, int mpg, int mpv){
        Dominance = dom;
        PowerVertex = pv;
        PowerEdges = pe;
        MinPowerGraph = mpg;
        MinPowerVertex = mpv;
    }

    public int GetDominance(){ return Dominance; }
    public int GetPowerVertex(){ return PowerVertex; }
    public int GetPowerEdges(){ return PowerEdges; }
    public int GetMinPowerGraph(){ return MinPowerGraph; }
    public int GetMinPowerVertex(){ return MinPowerVertex; }
}
