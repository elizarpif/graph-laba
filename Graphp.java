package com.company;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.*;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxStylesheet;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.*;
import org.jgrapht.io.CSVFormat;
import org.jgrapht.io.CSVImporter;
import org.jgrapht.io.ImportException;
import org.jgrapht.io.MatrixExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;


public class Graphp {

    public ListenableGraph<String, DefaultEdge> graph;
    private Graph<String, MyEdge> g;
    private JGraphXAdapter<String, MyEdge> gadap; //
    private mxGraphComponent graphcomp;
    private Map<Integer, String> styles; //
    private mxCircleLayout layoutCircle;//
    private mxParallelEdgeLayout layoutParallel;
    private MatrixExporter<String, MyEdge> matri;
    private int i;
    private static final String NL = System.getProperty("line.separator");
    private JTable table;
    private int tabindex;
    private Vector<DefaultTableModel> graphmatrix;
    private mxUndoManager undoManager; // то что отвечает за "назад" и "вперед", история
    private boolean isSave; // переменная которая хранит значение, юыло ли сохранено
    private boolean isUndo; // переменная определяющая сохранение истории
    private int vertexSize;
    private Algorithms algorithm;

    public boolean BFS() {

        Object[] vs = gadap.getSelectionCells();
        if (vs.length != 2)
            return false;

        Object g1 = vs[0];
        Object g2 = vs[1];
        if (((mxCell) g1).isVertex() && ((mxCell) g2).isVertex()) {
            Object[] verts = gadap.getChildVertices(gadap.getDefaultParent());

            ArrayList<Object> vertices = new ArrayList<>();
            Collections.addAll(vertices, verts);
            int v1 = vertices.indexOf(g1);
            int v2 = vertices.indexOf(g2);
            ArrayList<ArrayList<Integer>> matr = getAdjacencyMatrix();

            ArrayList<Integer> result_verts = algorithm.BFS(v1, v2, matr);
            for (int i = 0; i < result_verts.size() - 1; i++) {
                Object i1 = vertices.get(result_verts.get(i));
                Object i2 = vertices.get(result_verts.get(i+1));
                Object[] edge = gadap.getEdgesBetween(i1, i2);
               // System.out.println(((mxCell)edge).getValue());
                gadap.getModel().beginUpdate();
                String color = "#ff3333";
                gadap.setCellStyles(mxConstants.STYLE_STROKECOLOR, color, edge);
                gadap.getModel().endUpdate();
            }
            System.out.println("verts result :"+result_verts);

        }
        return true;
        //graphcomp.getGraph().removeCells(new Object[]{g})

    }

    Graphp(JTable t, int index, Vector<DefaultTableModel> matr) {
        algorithm = new Algorithms();
        // граф "изнутри"
        g = new DefaultDirectedWeightedGraph(DefaultEdge.class);//SimpleGraph<>(DefaultEdge.class);

        i = 0; // счетчик вершин
        graphmatrix = matr;
        graph = new DefaultListenableGraph(g); //  оболочка для графа, позволяющая отслеживать события вроде добавления ребра
        gadap = new JGraphXAdapter(graph); // оболочка для отрисовки графа

        graphcomp = new mxGraphComponent(gadap); // штука для визуальъизации графа, отличается от предыдущей добавлением полезных фич
        // например, позволяет применить стиль ребра после загрузки из файла, применить цвет к вершине м прочие мелочи
        tabindex = index;
        table = t;
        setDefaultStyles();
        layoutParallel = new mxParallelEdgeLayout(gadap);//mxParallelEdgeLayout(gadap);
        layoutCircle = new mxCircleLayout(gadap);
        isUndo = true;
    }

    public void setDefaultStyles() {
        vertexSize = 50;
        undoManager = new mxUndoManager(10); //определяем количество шагов в истории
        Undo undolistener = new Undo();
        graphcomp.getGraph().getModel().addListener(mxEvent.UNDO, undolistener);
        graphcomp.getGraph().getView().addListener(mxEvent.UNDO, undolistener);
        styles = new HashMap<>();
        // всякие стили для вершин
        styles.put(0, mxConstants.SHAPE_RECTANGLE);
        styles.put(1, mxConstants.SHAPE_ELLIPSE);
        styles.put(2, mxConstants.SHAPE_CYLINDER);
        styles.put(3, mxConstants.SHAPE_TRIANGLE);
        styles.put(4, mxConstants.SHAPE_HEXAGON);
        styles.put(5, mxConstants.SHAPE_RHOMBUS);
        styles.put(6, mxConstants.SHAPE_DOUBLE_ELLIPSE);
        styles.put(7, mxConstants.SHAPE_CLOUD);
        styles.put(8, mxConstants.SHAPE_ACTOR);
        styles.put(9, mxConstants.SHAPE_SWIMLANE);
        styles.put(10, mxConstants.SHAPE_DOUBLE_RECTANGLE);
        matri = new MatrixExporter();
        SetStyle(gadap);
    }

    public class Undo implements mxEventSource.mxIEventListener {
        @Override
        public void invoke(Object o, mxEventObject mxEventObject) {
            if (isUndo) { // если это действие пользователя, то сохранять следующий шаг в истории
                undoManager.undoableEditHappened((mxUndoableEdit) mxEventObject.getProperty("edit"));
                System.out.println(" in indo manager!");
                // обновить таблицу смежности
                updateTable();
                isSave = false;
            }
        }
    }

    // обновление таблицы смежности
    private void updateTable() {
        ArrayList<ArrayList<Integer>> matr = getAdjacencyMatrix();
        System.out.println("in update table");
        if (matr.size() > 0) {
            ArrayList<Integer> m = matr.get(0);
            Object[][] data = new Object[m.size()][m.size()];
            for (int i = 0; i < m.size(); i++)
                for (int j = 0; j < m.size(); j++)
                    data[i][j] = matr.get(i).get(j);
            graphmatrix.get(tabindex).setDataVector(data, new Object[data[0].length]);
            table.setModel(graphmatrix.get(tabindex));
        } else {
            Object[][] data = new Object[0][0];
            graphmatrix.get(tabindex).setDataVector(data, new Object[0]);
            table.setModel(graphmatrix.get(tabindex));
        }
    }

    // скомпоновать параллельные ребра
    public void composeParallel() {
        isUndo = false;
        layoutParallel.execute(graphcomp.getGraph().getDefaultParent());
        SetStyle(gadap);
        updateTable();
        isUndo = true;
    }

    // скомпоновать по кругу
    public void composeCircle() {
        isUndo = false;
        layoutCircle.execute(graphcomp.getGraph().getDefaultParent());
        SetStyle(gadap);
        updateTable();
        isUndo = true;
    }

    public void SetSave() {
        isSave = true;
    }

    public boolean IsSave() {
        return isSave;
    }

    // go to one step back
    public void undo() {
        undoManager.undo();
        updateTable();
        isSave = false;
    }

    // go to one step front
    public void redo() {
        undoManager.redo();
        updateTable();
        isSave = false;
    }

    // импортер из файла, конструктор импортера
    public <E> CSVImporter<String, E> createImporter(
            Graph<String, E> gg, CSVFormat format, Character delimiter) {
        return new CSVImporter<>(
                (l, a) -> l, (f, t, l, a) -> gg.getEdgeSupplier().get(), format, delimiter);
    }

    // отрисовка графа из строки файла матрицы смежности
    public void fromAdjacencyMatrixString(String input1) {
        isUndo = false;

        Graph<String, MyEdge> gg = new DefaultDirectedWeightedGraph(MyEdge.class);

        CSVImporter<String, MyEdge> importer = createImporter(gg, CSVFormat.MATRIX, ',');
        importer.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, true);
        importer.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, true);

        try {
            importer.importGraph(gg, new StringReader(input1));
        } catch (ImportException e) {
            e.printStackTrace();
        }
        Object[] edges = gg.edgeSet().toArray();

        for (Object a : edges) {
            MyEdge e = (MyEdge) a;
            System.out.println((int) gg.getEdgeWeight(e));
        }
        graph = new DefaultListenableGraph(gg);
        gadap = new JGraphXAdapter(graph);
        graphcomp.setGraph(gadap);
        // ниже костыль для размера вершин!
        RedrawVertexSize();
        layoutParallel = new mxParallelEdgeLayout(gadap);
        layoutCircle = new mxCircleLayout(gadap);
        setDefaultStyles();
        updateGraph();
        isUndo = true;
    }

    // отрисовка из файла ребер .edg
    public void fromEdgString(ArrayList<ArrayList<String>> inp) {
        isUndo = false;
        for (int ii = 0; ii < inp.size(); ii++) {
            String aa = inp.get(ii).get(1);
            String kk = inp.get(ii).get(2);
            String ll = inp.get(ii).get(3);
            String dd = inp.get(ii).get(4);

            System.out.println(gadap.getCellToVertexMap());

            Object[] verts = gadap.getChildVertices(gadap.getDefaultParent());

            ArrayList<Object> v = new ArrayList<>();
            Collections.addAll(v, verts);
            ArrayList<String> existcaptions = new ArrayList<>();
            for (Object ab : v) {
                mxCell cell = (mxCell) ab;
                existcaptions.add(cell.getValue().toString());
            }
            Object v1;
            Object v2;
            if (existcaptions.contains(kk))
                v1 = v.get(existcaptions.indexOf(kk));
            else
                v1 = gadap.insertVertex(null, null, kk, 20, 20, 50, 50);

            if (existcaptions.contains(ll))
                v2 = v.get(existcaptions.indexOf(ll));
            else
                v2 = gadap.insertVertex(null, null, ll, 120, 70, 50, 50);

            Object edge = gadap.insertEdge(null, null, aa, v1, v2, null);
            if (dd.equals("0")) {
                gadap.setCellStyles(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR, new Object[]{edge});
                gadap.setCellStyles(mxConstants.STYLE_ENDARROW, "ss", new Object[]{edge});
            }
        }
        updateGraph();
        isUndo = true;
    }

    // отрисовка из файла вершин .vert
    public void fromVertString(ArrayList<ArrayList<String>> inp) {
        isUndo = false;
        for (int ii = 0; ii < inp.size(); ii++) {
            System.out.println(inp.get(ii).get(0));
            int x = Integer.valueOf(inp.get(ii).get(1));
            int y = Integer.valueOf(inp.get(ii).get(2));
            gadap.insertVertex(null, null, inp.get(ii).get(0), x, y, 50, 50);
        }
        graphcomp.setGraph(gadap);
        updateGraph();
        isUndo = true;
    }

    // при загрузке из файла сбивается размер, RedrawVertexSize перерисовывает уже отрисованные вершины
    private void RedrawVertexSize() {
        Object[] verts = gadap.getChildVertices(gadap.getDefaultParent());
        ArrayList<mxCell> vertices = new ArrayList<>();
        for (Object a : verts)
            vertices.add((mxCell) a);
        for (mxCell a : vertices) {
            mxGeometry g = (mxGeometry) a.getGeometry().clone();
            g.setHeight(vertexSize);
            g.setWidth(vertexSize);
            gadap.cellsResized(new Object[]{a}, new mxRectangle[]{g});
        }
        i = vertices.size();
    }

    // отрисовка из файла матрицы инциндентности .inc
    public void fromIncMatrixString(ArrayList<ArrayList<String>> inp) {
        System.out.println("Matrix:" + inp.toString());
        isUndo = false;
        Object parent = gadap.getDefaultParent();
        ArrayList<Object> objs = new ArrayList<>();
        for (int i = 0; i < inp.get(0).size(); i++) {
            Object v = gadap.insertVertex(parent, null, i + 1, 50, 50, 50, 50);
            objs.add(v);
        }

        for (int i = 0; i < inp.size(); i++) {
            int v1 = inp.get(i).indexOf("1");
            int v2 = inp.get(i).indexOf("-1");
            if (v2 == -1)
                v2 = v1;
            gadap.insertEdge(parent, null, "", objs.get(v2), objs.get(v1));
        }
        updateGraph();
        isUndo = true;
    }

    //компоновка лаяутов и обновление таблицы
    private void updateGraph() {
        graphcomp.getGraph().getModel().beginUpdate();
        layoutCircle.execute(graphcomp.getGraph().getDefaultParent());
        layoutParallel.execute(graphcomp.getGraph().getDefaultParent());
        graphcomp.getGraph().getModel().endUpdate();
        graphcomp.refresh();
        updateTable();
    }


    // добавление вершины
    public void AddVertex(int x, int y) {
        Object parent = graphcomp.getGraph().getDefaultParent();
        graphcomp.getGraph().getModel().beginUpdate();
        try {
            i++;
            Object v1 = graphcomp.getGraph().insertVertex(parent, null, i, x, y, vertexSize, vertexSize);
        } finally {
            graphcomp.getGraph().getModel().endUpdate();
        }
        System.out.println("added vertex");
    }

    // удаление вершины
    public void RemoveVertex(int x, int y) {
        Object parent = graphcomp.getGraph().getDefaultParent();
        graphcomp.getGraph().getModel().beginUpdate();

        Object g = graphcomp.getCellAt(x, y);//gcomp.getCellAt(x, y);
        graphcomp.getGraph().removeCells(new Object[]{g});

        graphcomp.getGraph().getModel().endUpdate();
        System.out.println("remove vertex");
    }

    // удаление ребра
    public void RemoveEdge(int x, int y) {
        graphcomp.getGraph().getModel().beginUpdate();

        Object g = graphcomp.getCellAt(x, y);//gcomp.getCellAt(x, y);
        graphcomp.getGraph().removeCells(new Object[]{g});

        graphcomp.getGraph().getModel().endUpdate();
        System.out.println("remove edge");
    }

    public mxGraphComponent getComp() {
        return graphcomp;
    }

    // установка стилей вершин и ребер для графа
    public void SetStyle(mxGraph gr) {
        gr.setAllowLoops(true);
        gr.setAllowDanglingEdges(false);
        gr.setCellsCloneable(true);
        gr.setEdgeLabelsMovable(false);
        gr.setVertexLabelsMovable(false);
        gr.setAutoSizeCells(true);
        gr.setCellsSelectable(true);


        // set default edge style
        Map<String, Object> edge = new HashMap<>();
        edge.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_TOPTOBOTTOM);
        edge.put(mxConstants.STYLE_ROUNDED, true);
        edge.put(mxConstants.STYLE_ORTHOGONAL, true);
        edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        edge.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // default is #6482B9
        edge.put(mxConstants.STYLE_FONTCOLOR, "#430000");

        // set default vertex style
        Map<String, Object> vertex = new HashMap<>();
        vertex.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        vertex.put(mxConstants.STYLE_PERIMETER, mxPerimeter.EllipsePerimeter);
        vertex.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        vertex.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        vertex.put(mxConstants.STYLE_FILLCOLOR, "#9ACD32");
        vertex.put(mxConstants.STYLE_STROKECOLOR, "#6482B9");
        vertex.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        mxStylesheet style = new mxStylesheet();

        style.setDefaultVertexStyle(vertex);
        style.setDefaultEdgeStyle(edge);

        gr.setStylesheet(style);
    }

    // установка стиля вершины для вершины
    public void SetStyleForVertex(int x, int y, int choiceStyle) {
        graphcomp.getGraph().getModel().beginUpdate();
        Object g = graphcomp.getCellAt(x, y);
        graphcomp.getGraph().setCellStyles(mxConstants.STYLE_SHAPE, styles.get(choiceStyle), new Object[]{g});

        graphcomp.getGraph().getModel().endUpdate();
    }

    // установка цвета вершины для вершины
    public void SetColorVertex(int x, int y, String hex) {
        graphcomp.getGraph().getModel().beginUpdate();
        Object g = graphcomp.getCellAt(x, y);

        graphcomp.getGraph().setCellStyles(mxConstants.STYLE_FILLCOLOR, hex, new Object[]{g});
        graphcomp.getGraph().getModel().endUpdate();
    }

    // установка цвета ребра
    public void SetColorEdge(int x, int y, String hex) {
        graphcomp.getGraph().getModel().beginUpdate();
        Object g = graphcomp.getCellAt(x, y);
        graphcomp.getGraph().setCellStyles(mxConstants.STYLE_STROKECOLOR, hex, new Object[]{g});

        graphcomp.getGraph().getModel().endUpdate();
    }

    // добавить петлю к вершине
    public void AddLoop(int x, int y) {
        graphcomp.getGraph().getModel().beginUpdate();

        Object g = graphcomp.getCellAt(x, y);
        Object parent = graphcomp.getGraph().getDefaultParent();
        graphcomp.getGraph().insertEdge(parent, null, "", g, g);

        graphcomp.getGraph().getModel().endUpdate();
    }

    // установка ненаправленности ребра
    public void ChangeDirectionOff(int x, int y) {
        graphcomp.getGraph().getModel().beginUpdate();
        Object g = graphcomp.getCellAt(x, y);
        graphcomp.getGraph().setCellStyles(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR, new Object[]{g});
        graphcomp.getGraph().setCellStyles(mxConstants.STYLE_ENDARROW, "ss", new Object[]{g});

        graphcomp.getGraph().getModel().endUpdate();
    }

    // установка направленности ребра
    public void ChangeDirectionOn(int x, int y) {
        graphcomp.getGraph().getModel().beginUpdate();
        Object g = graphcomp.getCellAt(x, y);
        graphcomp.getGraph().setCellStyles(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR, new Object[]{g});
        graphcomp.getGraph().setCellStyles(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC, new Object[]{g});

        graphcomp.getGraph().getModel().endUpdate();
    }


    // функция отрисовки графа после изменения таблицы смежности
    // удаляет ребра соответствующие вершина row - верщина col, и, если значение не 0, вставляет ребро
    public void DrawFromMatrix(int row, int col, int value) {
        System.out.println(" попал в отрисовку после изменения матрицы");
        ArrayList<ArrayList<Integer>> matr = getAdjacencyMatrix();
        Object parent = graphcomp.getGraph().getDefaultParent();
        Object[] objs = gadap.getChildVertices(parent);
        ArrayList<Integer> vertices = new ArrayList<>();
        ArrayList<mxCell> verts = new ArrayList<>();
        for (Object obj : objs) {
            mxCell v = (mxCell) obj;
            vertices.add(Integer.parseInt(v.getId()));
            verts.add(v);
        }
        isUndo = false;
        int countEdges = verts.get(row).getEdgeCount();
        // удаляем мульти ребра ()
        Object[] removeObjs = new Object[countEdges];
        int j = 0;
        for (int i = 0; i < countEdges; i++) {
            mxCell sourceEdge = (mxCell) verts.get(row).getEdgeAt(i);
            if (sourceEdge.getSource() == verts.get(row) && sourceEdge.getTarget().getId() == verts.get(col).getId()) {
                removeObjs[j] = (Object) sourceEdge;
                j++;
            }
        }
        gadap.removeCells(removeObjs);
        isUndo = true;
        if (value != 0) { //добавим ребрышко
            int old_value = matr.get(row).get(col);
            System.out.println("old value " + old_value + ", new value " + value);
            mxCell source = (mxCell) verts.get(row);
            mxCell target = (mxCell) verts.get(col);
            gadap.insertEdge(parent, null, value, source, target);
        }

        System.out.println("after changing table " + getAdjacencyMatrix());

    }

    // получить матрицу смежности текущего графа
    public ArrayList<ArrayList<Integer>> getAdjacencyMatrix() {

        Object parent = graphcomp.getGraph().getDefaultParent();
        Object[] objs = gadap.getChildVertices(parent);
        ArrayList<Integer> vertices = new ArrayList<>();
        ArrayList<mxCell> verts = new ArrayList<>();
        for (Object obj : objs) {
            mxCell v = (mxCell) obj;
            vertices.add(Integer.parseInt(v.getId()));
            verts.add(v);
        }

        ArrayList<ArrayList<Integer>> adj_matrix = new ArrayList<>();
        for (mxCell v : verts) {
            ArrayList<Integer> values = new ArrayList<>();
            for (int i = 0; i < vertices.size(); i++)
                values.add(0);
            int edgeCount = v.getEdgeCount();
            for (int i = 0; i < edgeCount; i++) {
                mxCell edge = (mxCell) v.getEdgeAt(i);
                String style = (String) graphcomp.getGraph().getCellStyle(edge).get(mxConstants.STYLE_ENDARROW);

                int source = Integer.parseInt(edge.getSource().getId());
                int target = Integer.parseInt(edge.getTarget().getId());
                int id = Integer.parseInt(v.getId());
                int index = -1;

                if (source == id)
                    index = vertices.indexOf(target);
                else if (target == id && style.equals("ss"))
                    index = vertices.indexOf(source);
                if (index > -1) {
                    //values.set(index, 1);
                    int current_weight = values.get(index);
                    int edge_weight = 0;
                    try {
                        edge_weight = Integer.parseInt(edge.getValue().toString());
                    } catch (NumberFormatException e) {
                        edge_weight = 0;
                    } finally {
                        int res;
                        if (current_weight == 0 && edge_weight == 0)
                            res = 1;
                        else
                            res = current_weight + edge_weight;
                        values.set(index, res);

                    }
                }
            }
            adj_matrix.add(values);
        }
        System.out.println("adjacency matrix: " + adj_matrix);
        return adj_matrix;
    }

    // получить текущую матрицу инциндентности
    public ArrayList<ArrayList<Integer>> getIncedenceMatrix() {
        ArrayList<ArrayList<Integer>> matr_incedence = new ArrayList<>();

        Object parent = graphcomp.getGraph().getDefaultParent();
        Object[] edges = gadap.getChildEdges(parent);
        Object[] vertices = gadap.getChildVertices(parent);
        for (int i = 0; i < edges.length; i++) {
            mxCell edge = (mxCell) edges[i];
            ArrayList<Integer> vertices_list = new ArrayList<>();
            for (int j = 0; j < vertices.length; j++)
                vertices_list.add(0);
            int source = Integer.parseInt(edge.getSource().getId());
            int target = Integer.parseInt(edge.getTarget().getId());
            String style = (String) graphcomp.getGraph().getCellStyle(edge).get(mxConstants.STYLE_ENDARROW);
            int directed;
            if (style.equals("ss"))
                directed = 1;
            else
                directed = -1;

            for (int j = 0; j < vertices.length; j++) {
                mxCell vertex = (mxCell) vertices[j];
                int id = Integer.parseInt(vertex.getId());
                if (id == source)
                    vertices_list.set(j, directed);
                if (id == target)
                    vertices_list.set(j, 1);
            }
            matr_incedence.add(vertices_list);
        }
        System.out.println("incedence matrix: " + matr_incedence);

        return matr_incedence;
    }

    // сохранить матрицу смежности в файл
    public void saveAdjacencyMatrix(ArrayList<ArrayList<Integer>> matrix, String filename) {
        String infile = "";
        for (ArrayList<Integer> a : matrix) {
            for (Integer b : a) {
                infile += String.valueOf(b) + ",";
            }
            infile = infile.substring(0, infile.length() - 1);
            infile += "\n";
        }
        try {
            mxUtils.writeFile(infile, filename + ".adj");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // сохранить матрицу инциндентности в файл
    public void saveIncedenceMatrix(ArrayList<ArrayList<Integer>> matrix, String filename) {
        String infile = "";
        for (ArrayList<Integer> a : matrix) {
            for (Integer b : a) {
                infile += String.valueOf(b) + ",";
            }
            infile = infile.substring(0, infile.length() - 1);
            infile += "\n";
        }
        try {
            mxUtils.writeFile(infile, filename + ".inc");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // сохранить вершины в файл вида
    // Vertex{v(x,y), v2(x,y),....}
    public void saveVertices(String filename) {
        String infile = "";
        Object parent = graphcomp.getGraph().getDefaultParent();
        Object[] objs = gadap.getChildVertices(parent);
        for (Object obj : objs) {
            mxCell v = (mxCell) obj;
            infile += v.getValue().toString() + "(";
            infile += String.valueOf((int) v.getGeometry().getX()) + ",";
            infile += String.valueOf((int) v.getGeometry().getY()) + ")\n";
        }
        try {
            mxUtils.writeFile(infile, filename + ".vert");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Сохранить ребра в файл вида
    // Edges{i(a, k, l, d), . . .}, где i — номер ребра, a — вес ребра,
    // k и l — номера или имена вершин, d — может быть 1 если направлено
    public void saveEdges(String filename) {
        String infile = "";
        Object parent = graphcomp.getGraph().getDefaultParent();
        Object[] objs = gadap.getChildEdges(parent);
        for (int i = 0; i < objs.length; i++) {
            mxCell edge = (mxCell) objs[i];

            String style = (String) graphcomp.getGraph().getCellStyle(edge).get(mxConstants.STYLE_ENDARROW);
            int directed;
            if (style.equals("ss"))
                directed = 0;
            else
                directed = 1;
            infile += String.valueOf(i) + "("; //+weight
            int edge_weight = 0;
            try {
                edge_weight = Integer.parseInt(edge.getValue().toString());
            } catch (NumberFormatException e) {
                edge_weight = 0;
            }
            infile += String.valueOf(edge_weight) + ",";
            infile += edge.getSource().getValue() + ",";
            infile += edge.getTarget().getValue() + ",";
            infile += String.valueOf(directed) + ")\n";
        }
        try {
            mxUtils.writeFile(infile, filename + ".edg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

