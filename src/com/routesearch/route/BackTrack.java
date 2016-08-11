package com.routesearch.route;

import java.util.ArrayList;
import java.util.List;

public class BackTrack {
	
	
	public static final int MAXVNUM = 600;
	public static final int MAXWEIGHT = 20;
	public static final int MAXPATH = MAXWEIGHT * MAXVNUM;
	
	public static Edge[][] graph = new Edge[MAXVNUM][MAXVNUM];
	public static Vertex[] vertexs = new Vertex[MAXVNUM];
	public static ArrayList<Integer> mustvisitedV = new ArrayList<>();
	public static ArrayList<Integer> otherV = new ArrayList<>();
	public static int s, t, maxVertex = -1;
	
	
	public static Path dijkstra(int startIndex)
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
	
	public static Path findMinDistance(int index, List<Integer> otherV, List<Integer> mustvisitedV)
	{
		if (Route.isVisitedAllofSet(mustvisitedV)) {
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

}
