package com.company.ui;

import com.company.core.graph.Graphp;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class Form extends JFrame {
    private JTabbedPane tabbedPanel;
    private JPanel panel1;
    private JButton addGraph_btn;
    private JTabbedPane tabPanel;
    private JButton undo_btn;
    private JButton redo_btn;
    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JButton savePNG_btn;
    private JButton circleCompose_btn;
    private JButton parrallelEdge_btn;
    private JScrollPane scrollpane;
    private Vector<DefaultTableModel> graphmatrix; // вектор моделей для таблицы матрицы смежности
    private Vector<Graphp> graph; //вектор Графов, отрисовываемых на панельке табвиджета
    int x, y;

    public Form() {
        setContentPane(panel1);
        setMenu();
        scrollpane.getViewport().setViewPosition(new Point(0, 0));

        graph = new Vector<>();
        setListeners();
        graphmatrix = new Vector<>();

        // сохранить граф изображением
        savePNG_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = JOptionPane.showInputDialog(
                        getContentPane(),
                        "<html><h2>Введите название файла");

                Container c = tabPanel.getSelectedComponent().getParent();//getContentPane();
                BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
                c.paint(im.getGraphics());
                try {
                    ImageIO.write(im, "PNG", new File("D:\\" + result));//png,jng
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(getContentPane(),
                        "<html><h2>Граф сохранен в файл " + result + "</h2>");

            }
        });
        // скомпоновать по кругу
        circleCompose_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int ind = tabPanel.getSelectedIndex();
                    graph.get(ind).composeCircle();
                } catch (Exception err) {
                    // err.printStackTrace();
                }
            }
        });
        // скомпоновать параллельные ребра
        parrallelEdge_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int ind = tabPanel.getSelectedIndex();
                    graph.get(ind).composeParallel();
                } catch (Exception err) {
                    //err.printStackTrace();
                }
            }
        });

        table1.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Редактирование текущей матрицы
                if ("tableCellEditor".equals(evt.getPropertyName())) {
                    if (!table1.isEditing()) {
                        try {
                            DefaultTableModel model = (DefaultTableModel) table1.getModel();

                            int col = table1.getEditingColumn();
                            int row = table1.getEditingRow();
                            Vector<Vector> v = model.getDataVector();

                            String editedValue = String.valueOf(v.get(row).get(col));
                            System.out.println("change in cell (" + row + "," + col + ": " + editedValue);
                            System.out.println("get vector from table " + v);
                            int ind = tabPanel.getSelectedIndex();
                            int value;
                            try {
                                value = Integer.parseInt(editedValue);
                            } catch (NumberFormatException e) {
                                value = 0;
                                table1.setValueAt(0, row, col);
                            }

                            graph.get(ind).DrawFromMatrix(row, col, value);

                        } catch (Exception err) {
                            err.printStackTrace();
                            // System.out.println(ind);
                        }
                    }
                }
            }
        });

        tabPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource() instanceof JTabbedPane) {
                    JTabbedPane pane = (JTabbedPane) e.getSource();
                    System.out.println("Selected paneNo  : " + pane.getSelectedIndex());
                    int ind = tabPanel.getSelectedIndex();
                    table1.setModel(graphmatrix.get(ind));
                }
            }
        });
    }

    public void setListeners() {
        addGraph_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //create new graph
                try {
                    int ind = tabPanel.getTabCount();//tabPanel.getSelectedIndex();
                    System.out.println("новый граф...");

                    DefaultTableModel h = new DefaultTableModel();
                    graphmatrix.add(h);

                    Graphp tmp = new Graphp(table1, ind, graphmatrix);
                    graph.add(tmp);
                    tabPanel.addTab("graph " + String.valueOf(ind), tmp.getComp());
                    tabPanel.setSelectedIndex(ind);
                    setPopupMenu();
                } catch (Exception err) {
                    err.printStackTrace();
                }

            }
        });
        // кнопка назад
        undo_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int ind = tabPanel.getSelectedIndex();
                    graph.get(ind).undo();
                    //  graph.get(ind).composeCircle();
                } catch (Exception err) {
                    // err.printStackTrace();
                }
            }
        });
        // кнопка вперед
        redo_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int ind = tabPanel.getSelectedIndex();
                    graph.get(ind).redo();
                    //graph.get(ind).composeCircle();
                } catch (Exception err) {
                    // err.printStackTrace();
                }
            }
        });
    }


    private void addMenuItem(JMenu m, String name, int choice) {
        Font font = new Font("Chilanka", Font.BOLD, 24);

        JMenuItem menuItem = new JMenuItem(name);

        menuItem.setFont(font);
        m.add(menuItem);

        DialogActionListener l = new DialogActionListener();
        l.setChoiceDialog(choice);
        menuItem.addActionListener(l);
    }

    private boolean IsFileWasChoosen(JFileChooser k) {
        File f2 = new File(".").getAbsoluteFile();

        k.setCurrentDirectory(f2);

        if (k.showSaveDialog(getParent()) == JFileChooser.APPROVE_OPTION)
            return true;
        return false;
    }

    private String getFileName(JFileChooser k, int ind) {
        File f = k.getSelectedFile();

        String filename = f.getName();

        if (filename.equals(""))
            filename = String.valueOf(ind);
        return filename;
    }

    private void saveAdjacencyMatrix() {
        JFileChooser k = new JFileChooser();

        if (!IsFileWasChoosen(k)) {
            return;
        }

        int ind = tabPanel.getSelectedIndex();
        String filename = getFileName(k, ind);

        ArrayList<ArrayList<Integer>> matr = graph.get(ind).getAdjacencyMatrix();

        graph.get(ind).saveAdjacencyMatrix(matr, filename);
        graph.get(ind).SetSave();
    }

    private void saveIncedenceMatrix() {
        JFileChooser k = new JFileChooser();

        if (!IsFileWasChoosen(k)) {
            return;
        }

        int ind = tabPanel.getSelectedIndex();
        String filename = getFileName(k, ind);

        ArrayList<ArrayList<Integer>> matr = graph.get(ind).getIncedenceMatrix();

        graph.get(ind).saveIncedenceMatrix(matr, filename);
        graph.get(ind).SetSave();
    }

    public void setMenu() {

        Font font = new Font("Chilanka", Font.BOLD, 24);
        JMenuBar menuBar = new JMenuBar();


        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(font);

        JMenu aboutMenu = new JMenu("About");
        aboutMenu.setFont(font);

        HashMap<Integer, String> menuItems = new HashMap<>();
        menuItems.put(0, "About Program");
        menuItems.put(1, "About Authors");

        // создаем в цикле элементы меню по хешмапу с дефолтными настройками и диалогами
        for (int i = 0; i < menuItems.size(); i++)
            addMenuItem(aboutMenu, menuItems.get(i), i);


        JMenu newMenu = new JMenu("Save as");
        newMenu.setFont(font);
        fileMenu.add(newMenu);


        JMenuItem adjMatrFileItem = new JMenuItem("Adjacency matrix");
        adjMatrFileItem.setFont(font);

        // сохранить матрицу смежности в файл
        adjMatrFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveAdjacencyMatrix();
            }
        });

        newMenu.add(adjMatrFileItem);

        JMenuItem incMatrFileItem = new JMenuItem("Incendence matrix");
        incMatrFileItem.setFont(font);
        // сохранить матрицу инциндентности в файл
        incMatrFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveIncedenceMatrix();
            }
        });
        newMenu.add(incMatrFileItem);


        JMenuItem vertMatrFileItem = new JMenuItem("Vertices");
        vertMatrFileItem.setFont(font);
        // сохранить вершины в файл
        vertMatrFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser k = new JFileChooser();
                File f2 = new File(".").getAbsoluteFile();
                System.out.println("def dir " + f2.toString());
                k.setCurrentDirectory(f2);

                int isres = k.showSaveDialog(getParent());
                if (isres == JFileChooser.APPROVE_OPTION) {
                    File f = k.getSelectedFile();
                    String filename = f.getName();
                    int ind = tabPanel.getSelectedIndex();
                    if (filename.equals(""))
                        filename = String.valueOf(ind);
                    graph.get(ind).saveVertices(filename);
                    graph.get(ind).SetSave();

                }
            }
        });
        newMenu.add(vertMatrFileItem);

        JMenuItem edgeMatrFileItem = new JMenuItem("Edges");
        edgeMatrFileItem.setFont(font);
        // сохранить ребра в файл
        edgeMatrFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser k = new JFileChooser();
                File f2 = new File(".").getAbsoluteFile();
                System.out.println("def dir " + f2.toString());
                k.setCurrentDirectory(f2);

                int isres = k.showSaveDialog(getParent());
                if (isres == JFileChooser.APPROVE_OPTION) {
                    File f = k.getSelectedFile();
                    String filename = f.getName();
                    int ind = tabPanel.getSelectedIndex();
                    if (filename.equals(""))
                        filename = String.valueOf(ind);
                    graph.get(ind).saveEdges(filename);
                    graph.get(ind).SetSave();

                }
            }
        });
        newMenu.add(edgeMatrFileItem);


        JMenuItem imgFileItem = new JMenuItem("Image file");
        // сохранить граф как изображение
        imgFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser k = new JFileChooser();
                File f2 = new File(".").getAbsoluteFile();
                System.out.println("def dir " + f2.toString());
                k.setCurrentDirectory(f2);

                int isres = k.showSaveDialog(getParent());
                if (isres == JFileChooser.APPROVE_OPTION) {
                    File f = k.getSelectedFile();
                    String filename = f.getName();
                    int ind = tabPanel.getSelectedIndex();
                    if (filename.equals(""))
                        filename = String.valueOf(ind);

                    Container c = tabPanel.getSelectedComponent().getParent();//getContentPane();
                    BufferedImage im = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    c.paint(im.getGraphics());
                    try {
                        ImageIO.write(im, "PNG", new File(filename));//png,jng
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
        imgFileItem.setFont(font);
        newMenu.add(imgFileItem);


        JMenu openItem = new JMenu("Open");
        openItem.setFont(font);
        fileMenu.add(openItem);

        JMenuItem openAdjItem = new JMenuItem("From file");
        openAdjItem.setFont(font);
        openItem.add(openAdjItem);
        openAdjItem.addActionListener(new ActionListener() {      ////////////////////из файла
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser k = new JFileChooser();
//                k.showDialog(getParent(), "Выбрать");
                File f2 = new File(".").getAbsoluteFile();
                System.out.println("def dir " + f2.toString());
                k.setCurrentDirectory(f2);
                int result = k.showOpenDialog(getParent());

                File f = null;
                String filename = "";
                if (result == JFileChooser.APPROVE_OPTION)
                    f = k.getSelectedFile();
                if (f != null) {
                    filename = f.getName();
                    String ext1 = FilenameUtils.getExtension(filename);

                    System.out.println(f.toString());
                    String filepath = f.getAbsolutePath();
                    String strCurrentLine;

                    System.out.println("новый граф...");

                    DefaultTableModel h = new DefaultTableModel();
                    graphmatrix.add(h);
                    //создать счетчик вкладок

                    int ind = tabPanel.getTabCount();//getSelectedIndex();
                    Graphp tmp = new Graphp(table1, ind, graphmatrix);
                    graph.add(tmp);
                    tabPanel.addTab("graph " + String.valueOf(ind), tmp.getComp());

                    tabPanel.setSelectedIndex(ind);
                    setPopupMenu();
                    String input = "";
                    ArrayList<ArrayList<String>> inp = new ArrayList<>();
                    try {
                        BufferedReader objReader = new BufferedReader(new FileReader(filepath));

                        while ((strCurrentLine = objReader.readLine()) != null) {
                            if (ext1.equals("adj") || ext1.equals("inc") || ext1.equals("vert") || ext1.equals("edg")) {
                                if (strCurrentLine.contains("%")) {
                                    int index = strCurrentLine.indexOf("%");
                                    strCurrentLine = strCurrentLine.substring(0, index);

                                }
                                input += strCurrentLine + "\n";
                                if (ext1.equals("inc")) {
                                    String str[] = strCurrentLine.split(",");
                                    ArrayList<String> al = new ArrayList<String>(Arrays.asList(str));
                                    inp.add(al);
                                }
                                if (ext1.equals("vert") || ext1.equals("edg")) {
                                    String s = strCurrentLine.replaceAll("\\(", " ");
                                    s = s.replaceAll("\\)", " ");
                                    s = s.replaceAll(",", " ");
                                    String str[] = s.split(" ");
                                    ArrayList<String> al = new ArrayList<String>(Arrays.asList(str));
                                    inp.add(al);
                                }
                            }

                            System.out.println(strCurrentLine);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (ext1.equals("adj")) {
                        graph.get(ind).fromAdjacencyMatrixString(input);
                    }
                    if (ext1.equals("inc")) {
                        graph.get(ind).fromIncMatrixString(inp);
                    }
                    if (ext1.equals("vert")) {
                        graph.get(ind).fromVertString(inp);

                    }
                    if (ext1.equals("edg")) {
                        graph.get(ind).fromEdgString(inp);
                    }

                }

            }
        });

        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.setFont(font);
        fileMenu.add(closeItem);

        JMenuItem closeAllItem = new JMenuItem("Close all");
        closeAllItem.setFont(font);
        fileMenu.add(closeAllItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(font);
        fileMenu.add(exitItem);

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isexit = true;
                int count = tabPanel.getTabCount() - 1;

                for (int i = count; i >= 0; i--) {
                    tabPanel.setSelectedIndex(i);
                    System.out.println("is save" + graph.get(i).IsSave());
                    if (graph.get(i).IsSave() == false) {
                        int result = JOptionPane.showConfirmDialog(getContentPane(), "сохранить файл?");
                        if (result == 2)
                            isexit = false;
                        if (result == 0) {
                            JFileChooser k = new JFileChooser();
                            File f2 = new File(".").getAbsoluteFile();
                            System.out.println("def dir " + f2.toString());
                            k.setCurrentDirectory(f2);
                            int isres = k.showSaveDialog(getParent());
                            if (isres == JFileChooser.APPROVE_OPTION) {
                                File f = k.getSelectedFile();
                                String filename = f.getName();
                                int ind = tabPanel.getSelectedIndex();
                                if (filename.equals(""))
                                    filename = String.valueOf(ind);
                                ArrayList<ArrayList<Integer>> matr = graph.get(ind).getAdjacencyMatrix();
                                graph.get(ind).saveAdjacencyMatrix(matr, filename);
                                graph.get(ind).SetSave();

                            }
                        }
                        System.out.println("result" + result);
                        //tabPanel.remove(i);
                    }
                }
                if (isexit)
                    System.exit(0);
            }
        });

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);

    }

    // фунция для установки стиля для выбранной вершины
    public void setStyleListener(JMenuItem StyleC, int choiceStyle) {
        StyleC.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int index = tabPanel.getSelectedIndex();
                graph.elementAt(index).SetStyleForVertex(x, y, choiceStyle);
            }
        });
    }

    public void setPopupMenu() {

        System.out.println("popup");
        JPopupMenu popupV = new JPopupMenu();
        JPopupMenu popupE = new JPopupMenu();
        JPopupMenu popupAddV = new JPopupMenu();

        JMenuItem ColorEItem = new JMenuItem("Установить цвет ребра");
        // TODO : use one MouseListener function
        ColorEItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int index = tabPanel.getSelectedIndex();
                Color color = JColorChooser.showDialog(null, "Choose a color", Color.RED);
                String hex = "#" + Integer.toHexString(color.getRGB()).substring(2);
                System.out.println("hex =" + hex);
                graph.elementAt(index).SetColorEdge(x, y, hex);
            }
        });

        JMenuItem ColorVItem = new JMenuItem("Установить цвет вершины");
        // TODO : use one MouseListener function
        ColorVItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                super.mouseReleased(e);

                int index = tabPanel.getSelectedIndex();

                Color color = JColorChooser.showDialog(null, "Choose a color", Color.RED);
                String hex = "#" + Integer.toHexString(color.getRGB()).substring(2);
                graph.elementAt(index).SetColorVertex(x, y, hex);

            }
        });
        JMenu StyleVMenu = new JMenu("Установить стиль вершины");
        JMenuItem StyleR = new JMenuItem("Прямоугольник");
        JMenuItem StyleE = new JMenuItem("Кружок");
        JMenuItem StyleC = new JMenuItem("Цилиндр");
        JMenuItem StyleT = new JMenuItem("Треугольник");
        JMenuItem StyleH = new JMenuItem("Шестиугольник");

        JMenuItem StyleRh = new JMenuItem("Ромб");
        JMenuItem StyleDE = new JMenuItem("Кружок с ободком");
        JMenuItem StyleK = new JMenuItem("Облачко)");
        JMenuItem StyleA = new JMenuItem("Кто-то");
        JMenuItem StyleS = new JMenuItem("Частично окрашенный прямоугольник");
        JMenuItem StyleDR = new JMenuItem("Прямоугольник с ободком");

        setStyleListener(StyleR, 0);
        setStyleListener(StyleE, 1);
        setStyleListener(StyleC, 2);
        setStyleListener(StyleT, 3);
        setStyleListener(StyleH, 4);
        setStyleListener(StyleRh, 5);
        setStyleListener(StyleDE, 6);
        setStyleListener(StyleK, 7);
        setStyleListener(StyleA, 8);
        setStyleListener(StyleS, 9);
        setStyleListener(StyleDR, 10);

        StyleVMenu.add(StyleR);
        StyleVMenu.add(StyleE);
        StyleVMenu.add(StyleC);
        StyleVMenu.add(StyleT);
        StyleVMenu.add(StyleH);
        StyleVMenu.add(StyleRh);
        StyleVMenu.add(StyleDE);
        StyleVMenu.add(StyleK);
        StyleVMenu.add(StyleA);
        StyleVMenu.add(StyleDR);
        StyleVMenu.add(StyleS);


        JMenuItem deleteVItem = new JMenuItem("Удалить вершину");

        deleteVItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int ind = tabPanel.getSelectedIndex();
                graph.elementAt(ind).RemoveVertex(x, y);
            }
        });

        JMenuItem addLoop = new JMenuItem("Добавить петлю");
        addLoop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int ind = tabPanel.getSelectedIndex();
                graph.elementAt(ind).AddLoop(x, y);
            }
        });

        JMenuItem BFSsearch = new JMenuItem("BFS");
        BFSsearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int ind = tabPanel.getSelectedIndex();
                boolean isReal = graph.elementAt(ind).BFS();
                if (!isReal) {
                    JOptionPane.showMessageDialog(null, "BFS не возможен для 2х вершин в данном графе");
                }
                // System.out.println("max val"+Integer.MAX);
            }
        });

        JMenuItem aStar = new JMenuItem("A Star");
        aStar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int ind = tabPanel.getSelectedIndex();
                boolean isReal = graph.elementAt(ind).AStar();
                if (!isReal) {
                    JOptionPane.showMessageDialog(null, "A Star не возможен для 2х вершин в данном графе");
                }
                // System.out.println("max val"+Integer.MAX);
            }
        });

        JMenuItem deleteEItem = new JMenuItem("Удалить ребро");

        deleteEItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int ind = tabPanel.getSelectedIndex();
                graph.elementAt(ind).RemoveEdge(x, y);
            }
        });


        JMenuItem addVItem = new JMenuItem("Добавить вершину");

        addVItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int ind = tabPanel.getSelectedIndex();
                //System.out.println("selected pane" + ind);
                graph.elementAt(ind).AddVertex(x, y);
            }
        });

        JMenuItem delSelectVItem = new JMenuItem("Снять выделения");

        delSelectVItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int ind = tabPanel.getSelectedIndex();
                graph.elementAt(ind).DelSelection();
            }
        });

        JMenuItem directItemOff = new JMenuItem("Сделать ребро ненаправленным");
        directItemOff.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int ind = tabPanel.getSelectedIndex();
                graph.elementAt(ind).ChangeDirectionOff(x, y);

            }
        });

        JMenuItem directItemOn = new JMenuItem("Сделать ребро направленным");
        directItemOn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int ind = tabPanel.getSelectedIndex();
                graph.elementAt(ind).ChangeDirectionOn(x, y);

            }
        });

        popupAddV.add(addVItem);
        popupAddV.add(delSelectVItem);
        popupE.add(deleteEItem);
        popupE.add(ColorEItem);
        popupE.add(directItemOff);
        popupE.add(directItemOn);
        popupV.add(deleteVItem);
        popupV.add(StyleVMenu);
        popupV.add(ColorVItem);
        popupV.add(addLoop);
        popupV.add(BFSsearch);
        popupV.add(aStar);

        mxGraphComponent gc = graph.elementAt(tabPanel.getSelectedIndex()).getComp();

        gc.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                super.mouseReleased(e);
                Object o = gc.getCellAt(e.getX(), e.getY());
                mxCell obj = (mxCell) o;
                //right click
                if (e.getButton() == MouseEvent.BUTTON3) {
                    x = e.getX();
                    y = e.getY();
                    if (obj == null) {
                        popupAddV.show(gc, e.getX(), e.getY());
                    } else if (obj.isEdge()) {
                        popupE.show(gc, e.getX(), e.getY());
                    } else if (obj.isVertex()) {
                        popupV.show(gc, e.getX(), e.getY());
                    }

                }
            }
        });

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        Font panel1Font = this.$$$getFont$$$("Chilanka", -1, 14, panel1.getFont());
        if (panel1Font != null) panel1.setFont(panel1Font);
        tabPanel = new JTabbedPane();
        Font tabPanelFont = this.$$$getFont$$$("Chilanka", -1, 24, tabPanel.getFont());
        if (tabPanelFont != null) tabPanel.setFont(tabPanelFont);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 100.0;
        gbc.weighty = 100.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(tabPanel, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(19, 3, new Insets(0, 0, 0, 0), -1, -1));
        Font panel2Font = this.$$$getFont$$$("Chilanka", -1, 26, panel2.getFont());
        if (panel2Font != null) panel2.setFont(panel2Font);
        panel2.setMaximumSize(new Dimension(2147483647, 2147483647));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        redo_btn = new JButton();
        Font redo_btnFont = this.$$$getFont$$$("Chilanka", Font.BOLD, 24, redo_btn.getFont());
        if (redo_btnFont != null) redo_btn.setFont(redo_btnFont);
        redo_btn.setText("->");
        panel2.add(redo_btn, new com.intellij.uiDesigner.core.GridConstraints(18, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrollpane = new JScrollPane();
        scrollpane.putClientProperty("html.disable", Boolean.TRUE);
        panel2.add(scrollpane, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 13, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        Font tabbedPane1Font = this.$$$getFont$$$("Chilanka", -1, 24, tabbedPane1.getFont());
        if (tabbedPane1Font != null) tabbedPane1.setFont(tabbedPane1Font);
        scrollpane.setViewportView(tabbedPane1);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Adjacency matrix", panel3);
        table1 = new JTable();
        table1.setToolTipText("Матрица смежности");
        panel3.add(table1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(250, 150), null, 0, false));
        addGraph_btn = new JButton();
        Font addGraph_btnFont = this.$$$getFont$$$("Chilanka", Font.BOLD, 24, addGraph_btn.getFont());
        if (addGraph_btnFont != null) addGraph_btn.setFont(addGraph_btnFont);
        addGraph_btn.setText("Add graph");
        panel2.add(addGraph_btn, new com.intellij.uiDesigner.core.GridConstraints(14, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        undo_btn = new JButton();
        Font undo_btnFont = this.$$$getFont$$$("Chilanka", Font.BOLD, 24, undo_btn.getFont());
        if (undo_btnFont != null) undo_btn.setFont(undo_btnFont);
        undo_btn.setText("<-");
        panel2.add(undo_btn, new com.intellij.uiDesigner.core.GridConstraints(17, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        circleCompose_btn = new JButton();
        Font circleCompose_btnFont = this.$$$getFont$$$("Chilanka", Font.BOLD, 24, circleCompose_btn.getFont());
        if (circleCompose_btnFont != null) circleCompose_btn.setFont(circleCompose_btnFont);
        circleCompose_btn.setText("Compose to circle");
        panel2.add(circleCompose_btn, new com.intellij.uiDesigner.core.GridConstraints(13, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        savePNG_btn = new JButton();
        Font savePNG_btnFont = this.$$$getFont$$$("Chilanka", Font.BOLD, 24, savePNG_btn.getFont());
        if (savePNG_btnFont != null) savePNG_btn.setFont(savePNG_btnFont);
        savePNG_btn.setText("Save to png");
        panel2.add(savePNG_btn, new com.intellij.uiDesigner.core.GridConstraints(15, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        parrallelEdge_btn = new JButton();
        Font parrallelEdge_btnFont = this.$$$getFont$$$("Chilanka", Font.BOLD, 24, parrallelEdge_btn.getFont());
        if (parrallelEdge_btnFont != null) parrallelEdge_btn.setFont(parrallelEdge_btnFont);
        parrallelEdge_btn.setText("Parallel edges");
        panel2.add(parrallelEdge_btn, new com.intellij.uiDesigner.core.GridConstraints(16, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}