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
    private String outputFileName;
    private Logger logger;
    final private int MinLen = 3;  //环的最小长度
    final private int MaxLen = 7;  //环的最大长度
    Map<Integer, Integer> head = new HashMap<>();

    Set<Integer> visited = new HashSet<>();
    Set<Integer> endNodesSet;
    //tarjan
    int visitTime = 0;
    Deque<Integer> stack = new ArrayDeque<>();
    Set<Integer> stackSet = new HashSet<>();
    public List<List<Integer>> tarRes = new LinkedList<>();
    Map<Integer, Integer> dfn = new HashMap<>(head.size());
    Map<Integer, Integer> low = new HashMap<>(head.size());
    Set<Integer> tarVisited = new HashSet<>();
    public int dfsCount = 0;
    public Graph(String inputFileName, String outputFileName){
        init(inputFileName, outputFileName);

        try {
            loadFile();
        } catch (IOException e) {
            logger.info("Fail: loadFile...");
        }
        long s = System.currentTimeMillis();

        for (int node : head.keySet()) {
            if (!endNodesSet.contains(node) || tarVisited.contains(node)) continue;
            tarjan(node);
        }
        long e = System.currentTimeMillis();
        System.out.println("tarjan time: " + (double) (e - s) / 1000);
//        findLoop();
//        output();
//        System.out.println("dfs调用次数：" + dfsCount);
    }

    public void init(String inputFileName, String outputFileName) {
        logger = Logger.getLogger("Graph");
        cnt = 0;
        path = new LinkedList<>();
        strPath = new LinkedHashSet<>();
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
        endNodesSet = new HashSet<>();
    }
    //读取文件，按照链式前向星的方法为图添加边
    public void loadFile() throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("inputFile: " + inputFileName);
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
        System.out.println("edges num: " + cnt);
    }
    /*
    * @Description add edge set
    * @param null
    * @Return
    * @Author sunwb
    * @Date 2020/3/29
    */
    public void addEdge(int u, int v, int w) {
        if (!head.containsKey(u))
            head.put(u, -1);
        Edge e = new Edge(head.get(u), v, w);
        edges[cnt] = e;
        head.put(u, cnt++);
        endNodesSet.add(v);
    }

    public void findLoop(){
        long start = System.currentTimeMillis();
        for (int node : head.keySet()) {
            if (!endNodesSet.contains(node) || visited.contains(node)) continue;
            LinkedHashSet<Integer> nodeList = new LinkedHashSet<>();
            nodeList.add(node);
            dfs(node, node, nodeList);
        }
        long end = System.currentTimeMillis();
        System.out.println("findLoop time: " + (double) (end - start) / 1000);
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
    /*
    * @Description 改变list的顺序，以最小节点开始
    * @param list ：一条循环路径
    * @Return java.util.List<java.lang.Integer>
    * @Author sunwb
    * @Date 2020/4/2 23:08
    **/
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

    /*
    * @Description 将结果输出至文件
    * @param filename
    * @param path
    * @Return void
    * @Author sunwb
    * @Date 2020/4/2 23:09
    **/
    public void output() {
        long start = System.currentTimeMillis();
        for (List<Integer> l : path) {
            String s = l.toString();
            strPath.add(s.substring(1, s.length()- 1));
        }
        long e1 = System.currentTimeMillis();
        System.out.println("del the same list: " + (double)(e1 - start)/1000);
        try {
            File file = new File(outputFileName);
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

    /*tarjan(u){
　　DFN[u]=Low[u]=++Index // 为节点u设定次序编号和Low初值
　　Stack.push(u)   // 将节点u压入栈中
　　for each (u, v) in E // 枚举每一条边
　　　　if (v is not visted) // 如果节点v未被访问过
　　　　　　　　tarjan(v) // 继续向下找
　　　　　　　　Low[u] = min(Low[u], Low[v])
　　　　else if (v in S) // 如果节点u还在栈内
　　　　　　　　Low[u] = min(Low[u], DFN[v])
　　if (DFN[u] == Low[u]) // 如果节点u是强连通分量的根
　　repeat v = S.pop  // 将v退栈，为该强连通分量中一个顶点
　　print v
　　until (u== v)
    }*/

    public void tarjan(int u) {

            dfn.put(u, visitTime);
            low.put(u, visitTime);
            visitTime++;
            stack.push(u);
            stackSet.add(u);
            tarVisited.add(u);
            if (!head.containsKey(u)) return;
            int index = head.get(u);
            if (index < 0) return;
            while (index != -1) {
                if (!tarVisited.contains(edges[index].end)) {
                    tarjan(edges[index].end);
                    low.put(u, Math.min(low.get(u), low.get(edges[index].end)));
                } else if (stackSet.contains(edges[index].end)) {
                    low.put(u, Math.min(low.get(u), dfn.get(edges[index].end)));
                }
                index = edges[index].next;
            }

            if (dfn.get(u).equals(low.get(u))) {

                List<Integer> list = new LinkedList<>();
                int n = stack.peek();
                if (n == u) {
                    list.add(0, n);
                    stack.pop();
                    stackSet.remove(n);
                    tarRes.add(list);
                    return;
                }
                while (n != u) {
                    n = stack.pop();
                    stackSet.remove(n);
                    list.add(0, n);
                }
                if (list.size()>0) tarRes.add(list);
            }

    }
    public static void main(String[] args) {
        String inputFile = "src/data/test_data.txt";
        String outputFile = "src/data/answer.txt";
//        String inputFile = "/data/test_data.txt";
//        String outputFile = "/projects/student/result.txt";
        Graph graph = new Graph(inputFile, outputFile);

    }
}
