import java.util.ArrayList;

public class func {
    public ArrayList<Integer> BFS(int first, int last, ArrayList<ArrayList<Integer>> matrix){   //номер вершины откуда начинаем, где заканчиваем и сам
                                                                                                //и матрица инцидентности
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
                        return for_answ;
                    }
                    used[i] = true;                                                             //отмечаем что были в вершине
                    queue[queueTet++] = i;                                                      //добавляем в очередь чтобы потом обработать
                }
            }
        }
        for_answ = new ArrayList<Integer>();
        return for_answ;                                                                   
    }
    
    
    public int[] Deikstra(int start, ArrayList<ArrayList<Integer>> matrix) {

        int INFINITY = Integer.MAX_VALUE/2;
        int MatrixSize = matrix.get(0).size();
        boolean[] Visited = new boolean[MatrixSize];
        int[] Distance = new int[MatrixSize];

        fill(Distance, INFINITY, start);


        for(int i = 0; i < MatrixSize; i++)
        {
            int Vdis = -1;

            for(int NVdis = 0; NVdis < MatrixSize; NVdis++)
                if(!Visited[NVdis] && Distance[NVdis] < INFINITY
                        && (Vdis == -1 || Distance[Vdis] > Distance[NVdis]))
                    Vdis = NVdis;

                if(Vdis == -1) break;
                Visited[Vdis] = true;

                for(int NVdis = 0; NVdis < MatrixSize; NVdis++)
                    if (!Visited[NVdis] && matrix.get(Vdis).get(NVdis) < INFINITY &&
                        matrix.get(Vdis).get(NVdis) != 0)
                        Distance[NVdis] = (Distance[NVdis] < Distance[Vdis] +
                                matrix.get(Vdis).get(NVdis))?Distance[NVdis]:
                                Distance[Vdis]+matrix.get(Vdis).get(NVdis);

        }
        return  Distance;
    }

    public int[][] DeikstraMatrix(ArrayList<ArrayList<Integer>> matrix){
        int MatrixSize = matrix.get(0).size();
        int[][] for_result = new int[MatrixSize][MatrixSize];
        for(int i = 0; i < MatrixSize; i++)
            for_result[i] = Deikstra(i, matrix);

        return for_result;
    }
    
  
    /*----------------------------------------------------------------------------------------------------------Цэ-кит----*/
    //Служебные функции пусть будут туть
    public void fill(int[] a, int b, int excomunicato){
        for(int i = 0; i < a.length; i++)
            a[i] = b;
        a[excomunicato] = 0;
    }
}
