package graph;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    private int cnt;
    private String inputFileName;
    private Logger logger;
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

    public static void main(String[] args) {
        Graph graph = new Graph("src/data/test_data.txt");

    }
}
