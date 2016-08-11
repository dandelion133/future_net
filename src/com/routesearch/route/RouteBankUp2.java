/**
 * 实现代码文件
 * 
 * @author XXX
 * @since 2016-3-4
 * @version V1.0
 */
package com.routesearch.route;

import java.util.List;
import java.util.ArrayList;

public final class RouteBankUp2
{
	public static final int MAXVNUM = 600;
	public static final int MAXWEIGHT = 20;
	public static final int MAXPATH = MAXWEIGHT * MAXVNUM;
	
	private static Edge[][] graph = new Edge[MAXVNUM][MAXVNUM];
	private static Vertex[] vertexs = new Vertex[MAXVNUM];
	private static ArrayList<Integer> mustvisitedV = new ArrayList<>();
	private static ArrayList<Integer> otherV = new ArrayList<>();
	private static int s, t, maxVertex = -1;
	
	private static ArrayList<Integer> getVertexCondition(List<Integer> vertexList)
	{
		ArrayList<Integer> vertexArray = new ArrayList<>();
		for (int i = 0; i < vertexList.size(); i++) {
			if (!vertexs[vertexList.get(i)].isVisited())
				vertexArray.add(vertexList.get(i));
		}
		return vertexArray;
	}
	
	private static void init(String graphContent, String condition)
	{
		initGraph(graphContent);
    	initMustVisitedArray(condition);
    	initOtherVertexArray();
	}
	
	private static void initMustVisitedArray(String condition)
	{
		
		String[] numset = condition.split(",");
		s = Integer.valueOf(numset[0]);
		t = Integer.valueOf(numset[1]);
		numset[2] = numset[2].replace("\n", "");
		String[] nums = numset[2].split("\\|");
		for (int i = 0; i < nums.length; i++) {
			/*System.out.println(nums[i]);*/
			mustvisitedV.add(Integer.valueOf(nums[i]));
		}

	}
	
	private static void initOtherVertexArray()
	{
		for (int i = 0; i <= maxVertex; i++) {
			if (!mustvisitedV.contains(i) && i != s && i != t)
				otherV.add(i);
		}
	}
	
	private static void initGraph(String graphContent)
	{
		
		String[] lines = graphContent.split("\n");
		
		for (int i = 0; i < lines.length; i++) {
			String[] nums = lines[i].split(",");
			Edge edge = new Edge();
			edge.setIndex(Integer.valueOf(nums[0]));
			int v1 = Integer.valueOf(nums[1]);
			int v2 = Integer.valueOf(nums[2]);
			edge.setWeight(Integer.valueOf(nums[3]));
			
			if (graph[v1][v2] == null) {
				if (vertexs[v1] == null)
					vertexs[v1] = new Vertex(v1, false);
				if (vertexs[v2] == null)
					vertexs[v2] = new Vertex(v2, false);
				vertexs[v1].addNextVertexs(vertexs[v2]);
				
				maxVertex = Math.max(maxVertex, v1);
				maxVertex = Math.max(maxVertex, v2);
				
			}
			
			if (graph[v1][v2] == null || (graph[v1][v2] != null && graph[v1][v2].getWeight() > edge.getWeight())) {
				graph[v1][v2] = edge;
			}

		}
		
		for (int i = 0; i <= maxVertex; i++)
			for (int j = 0; j <= maxVertex; j++) {
				if (graph[i][j] == null)
					graph[i][j] = new Edge();
				if (i == j)
					graph[i][j].setWeight(0);
			}
		
	}
	
	private static boolean isVisitedAllofSet(List<Integer> vertexList)
	{
		for (int i = 0; i < vertexList.size(); i++) {
			if (!vertexs[vertexList.get(i)].isVisited())
				return false;
		}
		return true;
	}
	
	
	private static Path dijkstra(int startIndex)
	{
		ArrayList<Integer> vertexList = new ArrayList<>();
		vertexList.add(startIndex);
		Path path = new Path();
		for (int i = 0; i < otherV.size(); i++) {
			if (!vertexs[otherV.get(i)].isVisited() && startIndex != otherV.get(i)) {
				vertexList.add(otherV.get(i));
			}
		}
		vertexList.add(t);
		
		/*System.out.println(vertexList);*/
		
		boolean[] hasVisited = new boolean[vertexList.size()];
		
		//构建prev[]数组，保存该点对应的先前节点
		int[] prev = new int[vertexList.size()];
		
		int[] dist = new int[vertexList.size()];
		for (int i = 0; i < dist.length; i++) {
			dist[i] = graph[startIndex][vertexList.get(i)].getWeight();
			if (dist[i] == MAXPATH)
				prev[i] = -1;
			else
				prev[i] = 0;
		}
		
		
		hasVisited[0] = true;
		dist[0] = 0;
		
		
		for (int i = 1; i < dist.length; i++) {
			int minDist = MAXPATH;
			int u = 0;
			//找到当前距离startIndex最近并且为访问过的点
			for (int j = 1; j < dist.length; j++) {
				if (!hasVisited[j] && dist[j] < minDist) {
					u = j;
					minDist = dist[j];
				}
			}
			
			if (u == 0)
				break;
			
			hasVisited[u] = true;
			//根据当前点u松弛节点
			for (int j = 1; j < dist.length; j++) {
				if (!hasVisited[j] && graph[vertexList.get(u)][vertexList.get(j)].getWeight() < MAXPATH) {
					if (dist[u] + graph[vertexList.get(u)][vertexList.get(j)].getWeight() < dist[j]) {
						dist[j] = dist[u] + graph[vertexList.get(u)][vertexList.get(j)].getWeight();
						prev[j] = u;
					}
				}
			}
		}
/*		System.out.printf("prev : ");
		for (int i = 0; i < prev.length; i++) {
			System.out.printf("%d ", prev[i]);
		}
		System.out.println();*/
		
		if (dist[vertexList.size() - 1] == MAXPATH)
			path.setLength(MAXPATH);
		else {
			int i = vertexList.size() - 1;
			String pathStr = "";
			while (i != 0) {
				if (i == vertexList.size() - 1)
					pathStr = String.valueOf(graph[vertexList.get(prev[i])][vertexList.get(i)].getIndex());
				else {
					pathStr = String.valueOf(graph[vertexList.get(prev[i])][vertexList.get(i)].getIndex()) + "|" + pathStr;
				}
				i = prev[i];
			}
			
			path.setLength(dist[vertexList.size() - 1]);
			path.setPathStr(pathStr);
		}
		
		return path;
	}
	
	private static Path findMinDistance(int index, List<Integer> otherV, List<Integer> mustvisitedV)
	{
		if (isVisitedAllofSet(mustvisitedV)) {
		    Path path = dijkstra(index);
/*		    System.out.println("dijkstra startIndex: " + index);
		    System.out.println("dijkstra length: " + path.getLength());
		    System.out.println("dijkstra pathStr: " + path.getPathStr());*/
		    return path;
		} else {
			List<Vertex> nextVertexList = vertexs[index].getNextVertexs();
			Path path = new Path();
			path.setLength(MAXPATH);
			for (int i = 0; i < nextVertexList.size(); i++) {
				
				Vertex nextVertex = nextVertexList.get(i);
				
				if (!nextVertex.isVisited() && nextVertex.getIndex() != t) {
					
					int vertexIndex = nextVertex.getIndex();
					nextVertex.setVisited(true);
					int thisEdgeLength = graph[index][vertexIndex].getWeight();
					
					Path nextPath = findMinDistance(vertexIndex, otherV, mustvisitedV);
					
					if (nextPath.getLength() != MAXPATH && thisEdgeLength + nextPath.getLength() < path.getLength()) {
						path.setLength(thisEdgeLength + nextPath.getLength());
						path.setPathStr(String.valueOf(graph[index][vertexIndex].getIndex()) + "|" + nextPath.getPathStr());
					}
					
					nextVertex.setVisited(false);
				}
			}
			
			return path;
		}
	}
	
	
	
	/**
	 * 寻找题目中进过必经点集的最短路径主过程
	 * @param graphContent
	 * @param condition
	 * @return
	 */
    public static String searchRoute(String graphContent, String condition)
    {
    	init(graphContent, condition);
    	
    	System.out.println("-----------------------------");
    	for (int i = 0; i <= maxVertex; i++) {
    		for (int j = 0; j <= maxVertex; j++)
    			System.out.printf("%d(%d) ", graph[i][j].getWeight(), graph[i][j].getIndex());
    		System.out.println();
    	}
    	
    	System.out.println("-----------------------------");
    	for (int i = 0; i <= maxVertex; i++) {
    		System.out.println(vertexs[i] + ":" + vertexs[i].getNextVertexs());
    	}
    	
    	
    	System.out.println("-------------------------------");
    	System.out.println("mustvisited : " + mustvisitedV);
    	System.out.println("otherV : " + otherV);
    	
    	System.out.println("--------------------------------");
    	System.out.println(maxVertex);
    	vertexs[s].setVisited(true);
    	Path path = findMinDistance(s, otherV, mustvisitedV);
    	
    	System.out.println("the min Path Distance is : " + path.getLength());
    	
    	if (path.getLength() != MAXPATH) 
    		System.out.println("path is : " + path.getPathStr());
    	else 
    		System.out.println("NA");

        return "hello world!";
        
    }

}