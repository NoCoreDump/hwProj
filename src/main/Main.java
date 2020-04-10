package main;

import graph.Graph;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String inputFile = "src/data/test_data.txt";
        String outputFile = "src/data/answer.txt";
        //        String inputFile = "/data/test_data.txt";
//        String outputFile = "/projects/student/result.txt";
        Graph graph = new Graph(inputFile, outputFile);


    }

}
