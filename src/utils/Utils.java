package utils;

import java.util.*;

/**
 * @program: hwProj
 * @description: 工具类
 * @author: sunwb
 * @create: 2020-04-07 21:40
 */
public class Utils {
    public static List<Map.Entry<Integer, Integer>> mapSort (Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> list = new LinkedList<Map.Entry<Integer, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o1.getValue() - o2.getValue();
            }
        });
        return list;
    }

}
