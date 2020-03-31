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
        end = v;
        this.w = w;
    }
}
public class Graph {
    final int MaxEdges = 5100;
    public Edge[] edges = new Edge[MaxEdges];
    public int cnt;
    boolean[] visitedEdges;
    public List<LinkedHashSet<Integer>> path;
    private String inputFileName;
    private Logger logger;
    final private int MinLen = 3;  //环的最小长度
    final private int MaxLen = 7;  //环的最大长度
    Map<Integer, Integer> head = new HashMap<>();
    public Graph(String inputFileName){
        logger = Logger.getLogger("Graph");
        cnt = 0;
        this.inputFileName = inputFileName;
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
            System.out.println(Arrays.toString(data) + "cnt: " + cnt);
            addEdge(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));

        }
    }

    public void addEdge(int u, int v, int w) {
        if (!head.containsKey(u))
            head.put(u, -1);
        Edge e = new Edge(head.get(u), v, w);
        edges[cnt] = e;
        head.put(u, cnt++);
    }

    public void findLoop(){
        for (int node : head.keySet()) {
            LinkedHashSet<Integer> nodeList = new LinkedHashSet<>();
            nodeList.add(node);
            dfs(node, node, nodeList);
        }
        Collections.sort(path, new Comparator<LinkedHashSet<Integer>>() {
            @Override
            public int compare(LinkedHashSet<Integer> o1, LinkedHashSet<Integer> o2) {
                if (o1.size() != o2.size()) return o1.size() - o2.size();
                else {
                    Iterator<Integer> it1, it2;
                    it1 = o1.iterator();
                    it2 = o2.iterator();
                    while (it1.hasNext() && it2.hasNext()) {
                        int n = it1.next() - it2.next();
                        if (n != 0) return n;
                    }
                    return 0;
                }
            }
        });
    }
    //dfs寻找长度为3~7的环路
    public void dfs(int root, int node, LinkedHashSet<Integer> nodeList) {
        if (!head.containsKey(node)) return;
        int index = head.get(node);
        if (visitedEdges[index]) return ;
        while (index != -1) {
            Edge e = edges[index];
            visitedEdges[index] = true;
            if (root == e.end) {
                if (nodeList.size()<=MaxLen && nodeList.size()>=MinLen) path.add(nodeList);
                return;
            } else if (nodeList.contains(e.end)) {  //相当于链表带环但环的入口不在头节点
                LinkedHashSet<Integer> list = new LinkedHashSet<>();
                boolean flag = false;
                for (int x : nodeList) {
                    if (x == e.end) {
                        flag = true;
                    }
                    if (flag) {
                        list.add(x);
                    }
                }
                if (list.size()<=MaxLen && list.size()>=MinLen) path.add(list);
            }
            nodeList.add(e.end);
            dfs(root, e.end, nodeList);
            index = e.next;
        }
    }
    public static void main(String[] args) {
        String inputFile = "src/data/test_data.txt";
        String outputFile = "src/data/answer.txt";
        Graph graph = new Graph(inputFile);
//        System.out.println(graph.cnt);
        graph.findLoop();
        try {
            File file = new File(outputFile);

            if (!file.exists())
                file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fw);
            bufferedWriter.write(String.valueOf(graph.path.size()));
            bufferedWriter.newLine();
            for (LinkedHashSet<Integer> list : graph.path) {
                bufferedWriter.write(list.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            graph.logger.info("Fail: create file!");
        }
        System.out.println(graph.path.size());

    }
}
