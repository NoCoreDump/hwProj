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

    public List<LinkedHashSet<Integer>> path;
    private String inputFileName;
    private Logger logger;
    final private int MinLen = 3;  //环的最小长度
    final private int MaxLen = 7;  //环的最大长度
    Map<Integer, Integer> head = new HashMap<>();
    Map<Integer, Boolean> inLoop;
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
        inLoop = new HashMap<>();
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
            if (!endNodesSet.contains(node)) continue;
            LinkedHashSet<Integer> nodeList = new LinkedHashSet<>();
            nodeList.add(node);
            dfs(node, node, nodeList);
        }

        //sort
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
//            if (inLoop.containsKey(node)) {
//                for (LinkedHashSet<Integer> ls : path) {
//                    if (ls.contains(node)) {
//                        LinkedHashSet list = new LinkedHashSet();
//                        merge(node, ls, nodeList, list);
//                        path.add(list);
//                        return;
//                    }
//                }
//            }
            Edge e = edges[index];
            visitedEdges[index] = true;
            if (nodeList.contains(e.end)) {
                LinkedHashSet<Integer> list = new LinkedHashSet<>();
                boolean flag = false;
                for (int x : nodeList) {  //此处可加入路径长度计数器，避免list的构建
                    if (x == e.end) {
                        flag = true;
                    }
                    if (flag) {
                        list.add(x);
                        inLoop.put(x, true);
                    }
                }
                if (list.size()<=MaxLen && list.size()>=MinLen) {
                    path.add(list);
                } else {
                    nodeList.add(e.end);
                    dfs(root, e.end, nodeList);
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

    private void merge(int node, LinkedHashSet<Integer> ls, LinkedHashSet<Integer> nodeList, LinkedHashSet list) {
        int indexNodeInNodeList = -1;
        int indexStartInNodeList = -1;
        int indexNodeInLs = -1;
        int indexStartInLs = -1;
        int startNode = -1;
        int c = -1;
        for (int x : ls) {
            c++;
            if (indexNodeInLs > 0) list.add(x);//添加末尾元素
            if (indexStartInLs < 0 && nodeList.contains(x)) {
                indexStartInLs = c;
                startNode = x;
            }
            if (x == node) {
                indexNodeInLs = c;
            }
        }
        c = -1;
        boolean flag = false;
        for (int x : nodeList) {
            c++;
            if (flag) list.add(x);
            if (!flag && x == startNode) {
                indexStartInNodeList = c;
                flag = true;
            }
            if (x == node) {
                indexNodeInNodeList = c;
                break;
            }
        }
        int len = indexNodeInNodeList-indexStartInNodeList+ls.size()-indexNodeInLs+indexStartInLs;
        if (len < 3 || len > 7) return;
        c = -1;
        for (int x : ls) {
            c++;
            if (c < indexStartInLs) list.add(x);
            else break;
        }

    }

    //将结果输出至文件
    public void output(String filename, List<LinkedHashSet<Integer>> path) {
        try {
            File file = new File(filename);

            if (!file.exists())
                file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fw);
            bufferedWriter.write(String.valueOf(path.size()));
            bufferedWriter.newLine();
            for (LinkedHashSet<Integer> list : path) {
                bufferedWriter.write(list.toString());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Fail: create file!");
        }
    }
    public static void main(String[] args) {
        String inputFile = "src/data/test.txt";
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
