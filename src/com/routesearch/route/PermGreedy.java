package com.routesearch.route;

import java.util.ArrayList;
import java.util.List;

public class PermGreedy {
	public static final int MAXVNUM = 600;
	public static final int MAXWEIGHT = 20;
	public static final int MAXPATH = MAXWEIGHT * MAXVNUM;
	
	public static Edge[][] graph = new Edge[MAXVNUM][MAXVNUM];
	public static Vertex[] vertexs = new Vertex[MAXVNUM];
	public static ArrayList<Integer> mustvisitedV = new ArrayList<>();
	public static ArrayList<Integer> otherV = new ArrayList<>();
	public static int s, t, maxVertex = -1, maxEdge = -1;
	public static Path minPath = new Path();
	
	/**
	 *  迪杰斯特拉
	 * @param startIndex  起点 
	 * @param endIndex    终点
	 * @param pathVertexs   可以经过的点集
	 * @return
	 */
	public static Path dijkstra(int startIndex, int endIndex, List<Integer> pathVertexs)
	{
		ArrayList<Integer> vertexList = new ArrayList<>();
		vertexList.add(startIndex);
		Path path = new Path();
		for (int i = 0; i < otherV.size(); i++) {
			if (!vertexs[otherV.get(i)].isVisited()) {
				vertexList.add(otherV.get(i));
			}
		}
		vertexList.add(endIndex);
		
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
		
		/*System.out.printf("prev : ");
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
				//将otherV中的点保存下来    将已经经过的otherV中的点保存下来
				pathVertexs.add(vertexList.get(i));
				
			}
			
			path.setLength(dist[vertexList.size() - 1]);
			path.setPathStr(pathStr);
		}
		
		
		
		return path;
	}
	
	public static void genPerm(int[] perm, boolean[] hasUsed, int index, Path path)
	{
		if (index == mustvisitedV.size()) {//排列到了最后两个
			
			/*for (int i = 0; i < index; i++)
				System.out.printf("%d ", perm[i]);
			System.out.println();*/
			
			ArrayList<Integer> pathVertexs = new ArrayList<>();
			Path thisPath = dijkstra(perm[index - 1], t, pathVertexs);//pathVertexs传引用  dijkstra改变pathVertexs值 那么久真的改变了
			
			if (thisPath.getLength() != MAXPATH && path.getLength() + thisPath.getLength() < minPath.getLength()) {
				minPath.setPathStr(path.getPathStr() + "|" + thisPath.getPathStr());
				minPath.setLength(path.getLength() + thisPath.getLength());
				
				System.out.println("thisPath's length : " + thisPath.getLength());
				System.out.println("thisPath's pathStr : " + thisPath.getPathStr());
				System.out.println("pathVertexs : " + pathVertexs);
				System.out.println("minPath : " + minPath.getLength());
				System.out.println("minPath pathStr : " + minPath.getPathStr());
				
			}
			
			
			
		} else {
			for (int i = 0; i < mustvisitedV.size(); i++) {
				if (!hasUsed[i]) {   //如果没有被用过
					Path thisPath;
					ArrayList<Integer> pathVertexs = new ArrayList<>();
					//采用贪心算法求上个必经点到当前顶点的最短路径（路径只经过otherV）
					if (index == 0) {
						thisPath = dijkstra(s, mustvisitedV.get(i), pathVertexs);
					} else {
						thisPath = dijkstra(perm[index - 1], mustvisitedV.get(i), pathVertexs);
					}
					
					
					if (thisPath.getLength() != MAXPATH && path.getLength() + thisPath.getLength() < minPath.getLength()) {
						perm[index] = mustvisitedV.get(i);//将必经点的索引赋值给perm
						hasUsed[i] = true;
						Path tempPath = new Path();
						//保留前面的路径状态
						tempPath.setLength(path.getLength());
						tempPath.setPathStr(path.getPathStr());
						
						//改变当前路径状态，增加index点的最短路径
						if (index == 0) {
							path.setPathStr(thisPath.getPathStr());
						} else {
							path.setPathStr(path.getPathStr() + "|" + thisPath.getPathStr());
						}
						
						path.setLength(path.getLength() + thisPath.getLength());
						
						//将dijstra经过的路径点标记为true
						for (int j = 0; j < pathVertexs.size(); j++) {
							vertexs[pathVertexs.get(j)].setVisited(true);
						}
							
						
						genPerm(perm, hasUsed, index+1, path);
						
						/*System.out.println("-----------------------");
						for (int j = 0; j <= index; j++) 
							System.out.printf("%d ", perm[j]);
						System.out.println();*/
						
						
						//清除各种状态
						path = tempPath;  //恢复之前的路径
						hasUsed[i] = false;  //清除排列信息
						//清除dijstra访问点
						for (int j = 0; j < pathVertexs.size(); j++) {
							vertexs[pathVertexs.get(j)].setVisited(false);
						}
					}

				}
			}
				
		}
	}

	
}
