/**
 * 实现代码文件
 * 
 * @author XXX
 * @since 2016-3-4
 * @version V1.0
 */
package com.routesearch.route;

import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public final class RouteBankUp
{
	private static class Edge {
		private int index;
		private int weight;
		
		public Edge()
		{
			// TODO Auto-generated constructor stub
			index = -1;
			weight = MAXPATH;
		}
		
		
		public int getIndex()
		{
			return index;
		}
		public void setIndex(int index)
		{
			this.index = index;
		}
		public int getWeight()
		{
			return weight;
		}
		public void setWeight(int weight)
		{
			this.weight = weight;
		}
		
		@Override
		public String toString()
		{
			// TODO Auto-generated method stub
			return "" + weight;
		}
	}
	
	private static class Vertex {
		private boolean isVisited;
		private int index;
		private List<Vertex> nextVertexs = new ArrayList<>();
		
		public int getIndex()
		{
			return index;
		}

		public void setIndex(int index)
		{
			this.index = index;
		}

		public List<Vertex> getNextVertexs()
		{
			return nextVertexs;
		}

		public void setNextVertexs(List<Vertex> nextVertexs)
		{
			this.nextVertexs = nextVertexs;
		}

		
		
		public Vertex()
		{
			// TODO Auto-generated constructor stub
		}
		
		public Vertex(int index, boolean isVisited) 
		{
			this.isVisited = isVisited;
			this.index = index;
		}
		
		public void addNextVertexs(Vertex nextVertex)
		{
			nextVertexs.add(nextVertex);
		}
		
		public boolean hasNextVertex()
		{
			if (nextVertexs.isEmpty())
				return false;
			for (int i = 0; i < nextVertexs.size(); i++)
				if (!nextVertexs.get(i).isVisited)
					return true;
			return false;
		}
		
		public Vertex getNextVertex()
		{
			for (int i = 0; i < nextVertexs.size(); i++) {
				if (!nextVertexs.get(i).isVisited)
					return nextVertexs.get(i);
			}
			return null;
		}
		
		public boolean isVisited()
		{
			return isVisited;
		}

		public void setVisited(boolean isVisited)
		{
			this.isVisited = isVisited;
		}
		
		@Override
		public String toString()
		{
			// TODO Auto-generated method stub
			return index + "-" + isVisited;
		}
	}
	
	/**
	 * 状态类Dcondition
	 */
	private static class Dcondition {
		private int index;
		private ArrayList<Integer> otherVList;
		private ArrayList<Integer> mustvisitedVList;
		
		
		
		@Override
		public String toString()
		{
			return "[index=" + index + ", otherVList=" + otherVList + ", mustvisitedVList="
					+ mustvisitedVList + "]";
		}
		
		public Dcondition(int index, ArrayList<Integer> otherVList, ArrayList<Integer> mustvisitedVList)
		{
			super();
			this.index = index;
			this.otherVList = otherVList;
			this.mustvisitedVList = mustvisitedVList;
		}
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			result = prime * result + ((mustvisitedVList == null) ? 0 : mustvisitedVList.hashCode());
			result = prime * result + ((otherVList == null) ? 0 : otherVList.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Dcondition other = (Dcondition) obj;
			if (index != other.index)
				return false;
			if (mustvisitedVList == null) {
				if (other.mustvisitedVList != null)
					return false;
			} else if (!mustvisitedVList.equals(other.mustvisitedVList))
				return false;
			if (otherVList == null) {
				if (other.otherVList != null)
					return false;
			} else if (!otherVList.equals(other.otherVList))
				return false;
			return true;
		}
		
		
		
	}
	
	private static final int MAXVNUM = 600;
	private static final int MAXWEIGHT = 20;
	private static final int MAXPATH = MAXWEIGHT * MAXVNUM;
	private static Edge[][] graph = new Edge[MAXVNUM][MAXVNUM];
	private static Vertex[] vertexs = new Vertex[MAXVNUM];
	private static ArrayList<Integer> mustvisitedV = new ArrayList<>();
	private static ArrayList<Integer> otherV = new ArrayList<>();
	private static int s, t, maxVertex = -1;
	private static HashMap<Dcondition, Integer> distMap = new HashMap<>();
	
	private static ArrayList<Integer> getVertexCondition(List<Integer> vertexList)
	{
		ArrayList<Integer> vertexArray = new ArrayList<>();
		for (int i = 0; i < vertexList.size(); i++) {
			if (!vertexs[vertexList.get(i)].isVisited)
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
		
		Scanner sc = new Scanner(condition);
		sc.useDelimiter(",");
		s = sc.nextInt();
		t = sc.nextInt();
		
		/*String[] vertexs = sc.next().replace("\n", "").split("\\|");
		for (int i = 0; i < vertexs.length; i++) {
			//System.out.println(vertexs[i]);
			mustvisitedV.add(Integer.valueOf(vertexs[i]));
		}*/
		
		Scanner sc2 = new Scanner(sc.next().replace("\n", ""));
		sc2.useDelimiter("\\|");
		while (sc2.hasNext()) {
			mustvisitedV.add(sc2.nextInt());
		}
		
		sc.close();
		sc2.close();
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
		
		for (int i = 0; i < MAXVNUM; i++)
			for (int j = 0; j < MAXVNUM; j++) {
				graph[i][j] = new Edge();
				if (i == j)
					graph[i][j].setWeight(0);
			}
		
		Scanner sc = new Scanner(graphContent);
		sc.useDelimiter("\n");
		while (sc.hasNext()){
			//System.out.println(sc.next());
			Scanner lineSc = new Scanner(sc.next());
			lineSc.useDelimiter(",");
			Edge edge = new Edge();
			edge.setIndex(lineSc.nextInt());
			int v1 = lineSc.nextInt();
			int v2 = lineSc.nextInt();
			edge.setWeight(lineSc.nextInt());
			graph[v1][v2] = edge;
			if (vertexs[v1] == null)
				vertexs[v1] = new Vertex(v1, false);
			if (vertexs[v2] == null)
				vertexs[v2] = new Vertex(v2, false);
			vertexs[v1].addNextVertexs(vertexs[v2]);
			System.out.println(v1 + ", " + v2);
			
			maxVertex = Math.max(maxVertex, v1);
			maxVertex = Math.max(maxVertex, v2);
			lineSc.close();
		}
		sc.close();
	}
	
	private static boolean isVisitedAllofSet(List<Integer> vertexList)
	{
		for (int i = 0; i < vertexList.size(); i++) {
			if (!vertexs[vertexList.get(i)].isVisited)
				return false;
		}
		return true;
	}
	
	
	private static int dijkstra(int startIndex)
	{
		ArrayList<Integer> vertexList = new ArrayList<>();
		vertexList.add(startIndex);
		for (int i = 0; i < otherV.size(); i++) {
			if (!vertexs[otherV.get(i)].isVisited() && startIndex != otherV.get(i)) {
				vertexList.add(otherV.get(i));
			}
		}
		vertexList.add(t);
		
		boolean[] hasVisited = new boolean[vertexList.size()];
		int[] dist = new int[vertexList.size()];
		for (int i = 0; i < dist.length; i++) {
			dist[i] = graph[startIndex][vertexList.get(i)].getWeight();
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
			
			hasVisited[u] = true;
			//根据当前点u松弛节点
			for (int j = 1; j < dist.length; j++) {
				if (!hasVisited[j] && graph[vertexList.get(u)][vertexList.get(j)].getWeight() < MAXPATH) {
					if (dist[u] + graph[vertexList.get(u)][vertexList.get(j)].getWeight() < dist[j]) {
						dist[j] = dist[u] + graph[vertexList.get(u)][vertexList.get(j)].getWeight();
					}
				}
			}
		}
		
		return dist[vertexList.size() - 1];
	}
	
	private static int findMinDistance(int index, List<Integer> otherV, List<Integer> mustvisitedV)
	{
		if (isVisitedAllofSet(mustvisitedV)) {
			Dcondition dcondition = new Dcondition(index, getVertexCondition(otherV), getVertexCondition(mustvisitedV));
		    int distance = dijkstra(index);
		    distMap.put(dcondition, distance);
		    return distance;
		} else {
			List<Vertex> nextVertexList = vertexs[index].getNextVertexs();
			int minPathDist = MAXPATH;
			for (int i = 0; i < nextVertexList.size(); i++) {
				
				Vertex nextVertex = nextVertexList.get(i);
				
				if (!nextVertex.isVisited() && nextVertex.getIndex() != t) {
					
					int vertexIndex = nextVertex.getIndex();
					nextVertex.setVisited(true);
					int thisPathDist = graph[index][vertexIndex].getWeight();
					
					Dcondition dcondition = new Dcondition(vertexIndex, getVertexCondition(otherV), getVertexCondition(mustvisitedV));
					//System.out.println(dcondition);
					if (distMap.containsKey(dcondition))
						thisPathDist += distMap.get(dcondition);
					else 
						thisPathDist += findMinDistance(vertexIndex, otherV, mustvisitedV);
					
					minPathDist = Math.min(minPathDist, thisPathDist);
					
					nextVertex.setVisited(false);
				}
			}
			
			Dcondition dcondition = new Dcondition(index, getVertexCondition(otherV), getVertexCondition(mustvisitedV));
			distMap.put(dcondition, minPathDist);
			
			return minPathDist;
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
    			System.out.printf("%d ", graph[i][j].getWeight());
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
    	vertexs[s].setVisited(true);
    	int minPathDist = findMinDistance(s, otherV, mustvisitedV);
    	
    	System.out.println("the min Path Distance is : " + minPathDist);

    	System.out.println("---------------------------------");
    	Iterator<Entry<Dcondition, Integer>> itr = distMap.entrySet().iterator();
    	while (itr.hasNext()) {
    		Entry<Dcondition, Integer> entry = itr.next();
    		Dcondition dcondition = entry.getKey();
    		int dist = entry.getValue();
    		System.out.println(dcondition + " : " + dist);
    	}
    	System.out.println("distMap size : " + distMap.size());
    	System.out.println("---------------------------------");
        return "hello world!";
        
    }

}