package graph;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

class Edge {
    public int next;
    public int end;
    public int w;
    public Edge(int next, int v, int w) {
        this.next = next;
        this.end = v;
        this.w = w;
    }
    public String toString() {
        return " " + next + " " + end + " " + w;
    }
}
public class Graph {
    final int MaxEdges = 5100;
    public Edge[] edges = new Edge[MaxEdges];
    public int cnt;
//    boolean[] visitedEdges;
    public List<List<Integer>> path;
    public LinkedHashSet<String> strPath;
    private String inputFileName;
    private Logger logger;
    final private int MinLen = 3;  //环的最小长度
    final private int MaxLen = 7;  //环的最大长度
    Map<Integer, Integer> head = new HashMap<>();

    Set<Integer> visited = new HashSet<>();
    Set<Integer> endNodesSet;


    public int dfsCount = 0;
    public Graph(String inputFileName){
        logger = Logger.getLogger("Graph");
        cnt = 0;
        this.inputFileName = inputFileName;
        endNodesSet = new HashSet<>();
        try {
            loadFile();
        } catch (IOException e) {
            logger.info("Fail: loadFile...");
        }
        path = new LinkedList<>();
        strPath = new LinkedHashSet<>();
    }

    //读取文件，按照链式前向星的方法为图添加边
    public void loadFile() throws IOException {
        long startTime = System.currentTimeMillis();
        File f = new File(inputFileName);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(f), "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineText = null;
        while ((lineText = bufferedReader.readLine()) != null) {
            String[] data = lineText.split(",");
            addEdge(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));

        }
        long endTime = System.currentTimeMillis();
        System.out.println("read file and create graph: " + (double) (endTime - startTime) / 1000);
    }

    public void addEdge(int u, int v, int w) {
        if (!head.containsKey(u))
            head.put(u, -1);
        Edge e = new Edge(head.get(u), v, w);
        edges[cnt] = e;
        head.put(u, cnt++);
        endNodesSet.add(v);
    }

    public void findLoop(){
        for (int node : head.keySet()) {
            if (!endNodesSet.contains(node) || visited.contains(node)) continue;
            LinkedHashSet<Integer> nodeList = new LinkedHashSet<>();
            nodeList.add(node);
            dfs(node, node, nodeList);
        }
        sort(path);

    }

    //dfs寻找长度为3~7的环路
    public void dfs(int root, int node, LinkedHashSet<Integer> nodeList) {
        dfsCount++;
        if (!head.containsKey(node)) return;
        int index = head.get(node);
        if (index < 0) return;
        visited.add(node);
        while (index != -1) {
            Edge e = edges[index];
            if (nodeList.contains(e.end)) {

                List<Integer> list = new ArrayList<>(7);
                boolean flag = false;
                for (int x : nodeList) {  //此处可加入路径长度计数器，避免list的构建
                    if (!flag && x == e.end) {
                        flag = true;
                    }
                    if (flag) {
                        list.add(x);
                        visited.add(x);
                    }
                }
                if (list.size()<=MaxLen && list.size()>=MinLen) {
                    path.add(change(list));
                }
            } else {
                nodeList.add(e.end);
                dfs(root, e.end, nodeList);
            }
            index = e.next;

            if (index < 0) {
                nodeList.remove(node);
                return;
            }
        }


    }

    private List<Integer> change(List<Integer> list) {
        int min = 0;
        for (int i = 1; i < list.size();i++) {
            if (list.get(i) < list.get(min))
                min = i;
        }
        List<Integer> l = new ArrayList<>(list.size());
        for (int i = 0; i < list.size() - min; i++) {
            l.add(list.get(min+i));
        }
        for (int i = 0; i < min; i++) {
            l.add(list.get(i));
        }
        return l;
    }

    private void sort(List<List<Integer>> path) {
        Collections.sort(path, new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> o1, List<Integer> o2) {
                if (o1.size() != o2.size()) return o1.size()-o2.size();
                else {
                    for (int i = 0; i < o1.size(); i++) {
                        if (o1.get(i) != o2.get(i) ) return o1.get(i) - o2.get(i);
                    }
                }
                return 0;
            }
        });
    }

    //将结果输出至文件
    public void output(String filename, List<List<Integer>> path) {
        long start = System.currentTimeMillis();
        for (List<Integer> l : path) {
            String s = l.toString();
            strPath.add(s.substring(1, s.length()- 1));
        }
        long e1 = System.currentTimeMillis();
        System.out.println("del the same list: " + (double)(e1 - start)/1000);
        try {
            File file = new File(filename);

            if (!file.exists())
                file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fw);
            bufferedWriter.write(String.valueOf(strPath.size()));
            bufferedWriter.newLine();
            for (String s : strPath) {
                bufferedWriter.write(s);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Fail: create file!");
        }
        long e2 = System.currentTimeMillis();
        System.out.println("output file: " + (double)(e2 - start)/1000);
    }

    public static void main(String[] args) {
        String inputFile = "src/data/BigData1k_6944.txt";
        String outputFile = "src/data/answer.txt";
//        String inputFile = "/data/test_data.txt";
//        String outputFile = "/projects/student/result.txt";
        Graph graph = new Graph(inputFile);
        graph.findLoop();
        graph.output(outputFile, graph.path);
        System.out.println("dfs调用次数：" + graph.dfsCount);

    }
}
