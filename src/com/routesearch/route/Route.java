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

public final class Route
{
	public static final int MAXVNUM = 600;
	public static final int MAXWEIGHT = 20;
	public static final int MAXPATH = MAXWEIGHT * MAXVNUM;
	
	public static Edge[][] graph = new Edge[MAXVNUM][MAXVNUM];
	public static Vertex[] vertexs = new Vertex[MAXVNUM];
	public static ArrayList<Integer> mustvisitedV = new ArrayList<>();
	public static ArrayList<Integer> otherV = new ArrayList<>();
	public static int s, t, maxVertex = -1, maxEdge = -1;
	public static Path minPath = new Path();
	
	private static void initBackTrack()
	{
		BackTrack.graph = graph;
		BackTrack.vertexs = vertexs;
		BackTrack.mustvisitedV = mustvisitedV;
		BackTrack.otherV = otherV;
		BackTrack.s = s;
		BackTrack.t = t;
	}
	
	private static void initPermGreedy()
	{
		PermGreedy.graph = graph;
		PermGreedy.vertexs = vertexs;
		PermGreedy.mustvisitedV = mustvisitedV;
		PermGreedy.otherV = otherV;
		PermGreedy.s = s;
		PermGreedy.t = t;
		PermGreedy.minPath = minPath;
	}
	private static void initACO(int antNum)
	{
		ACO.antNum = antNum;//蚂蚁数量
		ArrayList<Integer> temp = new ArrayList<>();
		temp.add(new Integer(s));
		for (Integer must : mustvisitedV) {
			temp.add(must);
		}
		// = mustvisitedV;
		//temp.add(new Integer(t));
		
		//temp.
		ACO.mustvisitedV = temp;
		//System.out.println(otherV.size());
		ACO.otherV = otherV;
		ACO.vertexs = vertexs;
		ACO.t = t;
		ACO.graph = graph;
		ACO.MAX_GEN = 6;
		ACO.cityNum = mustvisitedV.size() + 1;
		ACO.alpha = 2f;//1
		ACO.beta = 5f;//5
		ACO.rho = 0.57f;//0.5
		//PermGreedy.minPath = minPath;
	}
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
			int edgeIndex = Integer.valueOf(nums[0]);
			maxEdge = Math.max(maxEdge, edgeIndex);
			edge.setIndex(edgeIndex);
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
	
	public static boolean isVisitedAllofSet(List<Integer> vertexList)
	{
		for (int i = 0; i < vertexList.size(); i++) {
			if (!vertexs[vertexList.get(i)].isVisited())
				return false;
		}
		return true;
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
    	System.out.println("maxVertex : " + maxVertex);
    	System.out.println("maxEdge : " + maxEdge);
    	
    	
    	
    	/*测试dijstra算法*/
/*    	ArrayList<Integer> pathVertexs = new ArrayList<>();
    	Path path = dijkstra(2, 19, pathVertexs);
    	System.out.println("path's length : " + path.getLength());
    	System.out.println("pathStr : " + path.getPathStr());
    	System.out.println(pathVertexs);*/
    	
    	
    	if (false) {
    		initBackTrack();
    		System.out.println("回溯");//最优解
	    	vertexs[s].setVisited(true);
	    	Path path = BackTrack.findMinDistance(s, otherV, mustvisitedV);
	    	
	    	System.out.println("the min Path Distance is : " + path.getLength());
	    	if (path.getLength() != MAXPATH) 
	    		System.out.println("path is : " + path.getPathStr());
	    	else 
	    		System.out.println("NA");
    	} else if(false){
    		System.out.println("贪婪");//局部最优
    		int[] perm = new int[mustvisitedV.size()];
        	boolean[] hasUsed = new boolean[mustvisitedV.size()];
        	minPath.setLength(MAXPATH);
        	
        	initPermGreedy();
        	
        	PermGreedy.genPerm(perm, hasUsed, 0, new Path());
        	
        	System.out.println("minPath length: " + minPath.getLength());
        	if (minPath.getLength() == MAXPATH) {
        		System.out.println("NA");
        	} else {
        		System.out.println("minPath pathStr: " + minPath.getPathStr());
        	}	
    	} else {
    	//	System.out.println("蚁群");
    		
    		//Ant ants = new Ant(mustvisitedV,otherV , t);
    		
    		ACO aco = new ACO();
    		initACO(20);
    		aco.init();
    		Path minPath = aco.solve();
    		//System.out.println();
    		//System.out.println("The optimal:"+minPath.getLength()+"\n路径为"+minPath.getPathStr());
    		if(minPath.getLength() >= MAXPATH) {
    			return "NA";
    		}else {
    			return minPath.getPathStr();
    		}
    		
    	}
    	
    	
    	

        return "hello world!";
        
    }

}