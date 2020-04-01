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
    boolean[] visitedEdges;
    public List<List<Integer>> path;
    private String inputFileName;
    private Logger logger;
    final private int MinLen = 3;  //环的最小长度
    final private int MaxLen = 7;  //环的最大长度
    Map<Integer, Integer> head = new HashMap<>();

    Set<Integer> visited = new HashSet<>();
//    Map<Integer, LinkedHashSet<Integer>> loop;  //每个点的环路
    Set<Integer> endNodesSet;
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

        visitedEdges = new boolean[cnt];
        path = new LinkedList<>();
    }

    //读取文件，按照链式前向星的方法为图添加边
    public void loadFile() throws IOException {
        File f = new File(inputFileName);
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(f), "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String lineText = null;
        while ((lineText = bufferedReader.readLine()) != null) {
            String[] data = lineText.split(",");
//            System.out.println(Arrays.toString(data) + "cnt: " + cnt);
            addEdge(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));

        }
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
        if (!head.containsKey(node)) return;
        int index = head.get(node);
//        if (visitedEdges[index]) return ;
        if (index < 0) return;
        visited.add(node);
        while (index != -1) {
            Edge e = edges[index];
            visitedEdges[index] = true;
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
                    visitedEdges[index] = false;
                } else {
                    visitedEdges[index] = false;
                }
            } else {
                nodeList.add(e.end);
                dfs(root, e.end, nodeList);
                visitedEdges[index] = false;
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
        try {
            File file = new File(filename);

            if (!file.exists())
                file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fw);
            bufferedWriter.write(String.valueOf(path.size()));
            bufferedWriter.newLine();
            for (List<Integer> list : path) {
                bufferedWriter.write(list.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Fail: create file!");
        }
    }
    public static void main(String[] args) {
        String inputFile = "src/data/test_data.txt";
        String outputFile = "src/data/answer.txt";
        Graph graph = new Graph(inputFile);
        graph.findLoop();
//        for (Edge e : graph.edges) {
//            if (e != null)System.out.println(e.toString());
//        }
//        System.out.println();
        graph.output(outputFile, graph.path);
        System.out.println(graph.path.size());

    }
}
