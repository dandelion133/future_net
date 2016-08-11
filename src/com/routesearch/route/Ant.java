package com.routesearch.route;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

 public class Ant implements Cloneable {

////private Vector<Integer> tabu; //禁忌表
//private Vector<Integer> allowedCities; //允许搜索的城市
//ArrayList<Integer> tabu = ArrayList<Integer>(); //禁忌表
//private ArrayList<Integer> allowedCities = ArrayList<Integer>; //允许搜索的城市

private ArrayList<IndexMap> tabu = new ArrayList<IndexMap>();
private ArrayList<IndexMap> histTabu = new ArrayList<IndexMap>();
private ArrayList<IndexMap> allowedCities = new ArrayList<IndexMap>(); 
private Path hisPath = new Path();
private float[][] delta; //信息数变化矩阵
//private int[][] distance; //距离矩阵    Edge
public static Edge[][] graph; 

public static final int MAXVNUM = 600;
public static final int MAXWEIGHT = 20;
public static final int MAXPATH = MAXWEIGHT * MAXVNUM;
public static Vertex[] vertexs = new Vertex[MAXVNUM];
public static int s, t, maxVertex = -1, maxEdge = -1;




private float alpha; 
private float beta;
 
private Path tourLength; //路径长度
public static int cityNum; //城市数量   也就是必经点的数量
 
private IndexMap firstCity = new IndexMap(); //起始城市
private IndexMap currentCity = new IndexMap(); //当前城市
//private int currentCityIndex; 
public static ArrayList<Integer> mustvisitedV = new ArrayList<>();//需要包含t
public static ArrayList<Integer> otherV = new ArrayList<>();
public static ArrayList<Integer> pathVertexs = new ArrayList<>();
public static int[][] wights; 


public Ant(){
	
   }
   
  
   public Ant(ArrayList<Integer> mustvisitedV,ArrayList<Integer> otherV,int t,Vertex[] vertexs,Edge[][] graph){
    
     this.mustvisitedV = mustvisitedV;
     //this.mustvisitedV.add(new Integer(t));
     cityNum = this.mustvisitedV.size();
     this.otherV = otherV;
     this.vertexs = vertexs;
     this.graph = graph;
     this.t = t;
    // tourLength = 0;
     
   }
   
   public static Path dijkstra(int startIndex, int endIndex, List<Integer> pathVertexs) {
		ArrayList<Integer> vertexList = new ArrayList<>();
		vertexList.add(startIndex);
		
		Path path = new Path();
		for (int i = 0; i < otherV.size(); i++) {
			if (!vertexs[otherV.get(i)].isVisited()) {
				vertexList.add(otherV.get(i));
			}
		}
		vertexList.add(endIndex);
		//pathVertexs.add(new Integer(startIndex));
		/*System.out.println(vertexList);*/
		
		boolean[] hasVisited = new boolean[vertexList.size()];
		
		//构建prev[]数组，保存该点对应的先前节点
		int[] prev = new int[vertexList.size()];
		
		int[] dist = new int[vertexList.size()];
		for (int i = 0; i < dist.length; i++) {
			//System.out.println("test");
			//System.out.println(graph[startIndex][vertexList.get(i)] == null);
			dist[i] = graph[startIndex][vertexList.get(i)].getWeight();
			if (dist[i] == MAXPATH)
				prev[i] = -1;
			else
				//prev[i] = 0;
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
   
   
   
   
   
   public static void initWeight(ArrayList<Integer> mustvisitedV,ArrayList<Integer> otherV,int t,Vertex[] vertexs,Edge[][] graph) {
	   	Ant.mustvisitedV = mustvisitedV;
	     //this.mustvisitedV.add(new Integer(t));
	     cityNum = Ant.mustvisitedV.size();
	     Ant.otherV = otherV;
	     Ant.vertexs = vertexs;
	     Ant.graph = graph;
	     Ant.t = t;
		wights = new int[cityNum][cityNum];
		System.out.println("cityNum:"+cityNum);
		for (int i = 0; i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++) {
				wights[i][j] = dijkstra(mustvisitedV.get(i), mustvisitedV.get(j), pathVertexs).getLength();
				System.out.print(wights[i][j]+",");
			}
			System.out.println();
		}
		pathVertexs.clear();
	}
   
   
   
   
   /**
    * 初始化蚂蚁，随机选择起始位置 不能放在t点
    * @param graph 距离矩阵   
    * @param a alpha
    * @param b beta
    */
   public void init(Edge[][] graph, float a, float b){
    // System.out.println("初始化蚂蚁");
	 alpha = a;
     beta = b;
     
     this.graph = graph;
     tabu.clear();
     allowedCities.clear();
     delta = new float[cityNum][cityNum];
    // System.out.println("delta矩阵初始化");
     IndexMap indexMap;
     for (int i = 0; i < cityNum; i++) {  //将所有必经点赋值给allowedCities
       
      // allowedCities.add(mustvisitedV.get(i));
    	 indexMap = new IndexMap();
    	 indexMap.setAntIndex(i);
    	 indexMap.setMapIndex(mustvisitedV.get(i));
    	 
    	 allowedCities.add(indexMap);
       //allowedCitiesIndex.add(new Integer(i));
    	 for (int j = 0; j < cityNum; j++) {
    		 delta[i][j] = 0.f;
    	 }
     }
     
    	 
    	// Random random = new Random(System.currentTimeMillis());
         int currentCityIndex = 0;//random.nextInt(cityNum);
         //System.out.println( "random:"+currentCityIndex);
         firstCity.setAntIndex(currentCityIndex);
         firstCity.setMapIndex(mustvisitedV.get(currentCityIndex));
         
         for (IndexMap i:allowedCities) {
           if (i.getMapIndex() == firstCity.getMapIndex()) {
             allowedCities.remove(i);//移除第一个点
             break;
           }
         }
         IndexMap firstCityIndexMap = new IndexMap();
         firstCityIndexMap.setAntIndex(currentCityIndex);
         firstCityIndexMap.setMapIndex(mustvisitedV.get(currentCityIndex));
         tabu.add(firstCityIndexMap);
         currentCity = firstCity;
       
         
         
         for (int i = 0; i < 50; i++) {
        	 histTabu.add(new IndexMap());
		 }
         
        // System.out.println(histTabu.size());
         //initWeight();
     
    
    /* System.out.println("第一次选择城市："+ currentCity.getMapIndex());
     for (Integer edges : mustvisitedV) {
		System.out.println("mustvisitedV:" + edges);
	}
     System.out.println();*/
   }
   
 
   
	 


/**
    * 选择下一个城市
    * @param pheromone 信息素矩阵
 * @return 
    */
   public IndexMap selectNextCity(float[][] pheromone){
     float[] p = new float[cityNum];
     float sum = 0.0f;
     //计算分母部分
    //List<Integer> pathVertexs = new ArrayList<Integer>;
     pathVertexs.clear();
     for (IndexMap i:allowedCities) {
			sum += Math.pow(pheromone[currentCity.getAntIndex()][i.getAntIndex()],alpha)
					* //需要用dijkstra
					//Math.pow(1.0 / wight[currentCity.getAntIndex()][i.getAntIndex()], beta));
					Math.pow(1.0 /wights[currentCity.getAntIndex()][i.getAntIndex()], beta);		
					
			//Math.pow(1.0 /dijkstra(currentCity.getMapIndex(), i.getMapIndex(), pathVertexs).getLength(), beta);
			//[currentCity.getMapIndex()][i.getMapIndex()].getWeight(), beta);
			/*	   //Math.pow(1.0 /graph[currentCity.getMapIndex()][i.getMapIndex()].getWeight());
			for (int j = 0; j < pathVertexs.size(); j++) {
				vertexs[pathVertexs.get(j)].setVisited(true);
			}*/
     }
  //清除dijstra访问点
	/*for (int j = 0; j < pathVertexs.size(); j++) {
		vertexs[pathVertexs.get(j)].setVisited(false);
	}*/
     //计算概率矩阵
     for (int i = 0; i < cityNum; i++) {
       boolean flag = false;
       pathVertexs.clear();
      for (IndexMap j:allowedCities) {
         
         if (i == j.getAntIndex()) {
           p[i] = (float) (Math.pow(pheromone[currentCity.getAntIndex()][i], alpha)
        		   *
        		   Math.pow(1.0 /wights[currentCity.getAntIndex()][i], beta))/sum;	
           // Math.pow(1.0/dijkstra(currentCity.getMapIndex(), mustvisitedV.get(i), pathVertexs).getLength(), beta))/sum;//	   
           flag = true;
           /*for (int k = 0; k < pathVertexs.size(); k++) {
				vertexs[pathVertexs.get(k)].setVisited(true);
			}*/
           break;
         }
       }
      //清除dijstra访问点
	  /*	for (int j = 0; j < pathVertexs.size(); j++) {
	  		vertexs[pathVertexs.get(j)].setVisited(false);
	  	}*/
       if (flag == false) {
         p[i] = 0.f;
      }
     }
     
   //轮盘赌选择下一个城市
     Random random = new Random(System.currentTimeMillis());
     float sleectP = random.nextFloat();
     int selectCity = 0;   //只是一个索引   mustVisitedV的索引
     float sum1 = 0.f;
     for (int i = 0; i < cityNum; i++) {
       sum1 += p[i];
       if (sum1 >= sleectP) {
	     selectCity = i;
	   //  System.out.println("选中城市：" + mustvisitedV.get(i));
	     break;
	 	}
     }
    
     //从允许选择的城市中去除select city
     for (IndexMap i:allowedCities) {
      if (i.getAntIndex() == selectCity) {
         boolean isRemove = allowedCities.remove(i);
         break;
       }
     }
     //在禁忌表中添加select city
     if(mustvisitedV.get(selectCity) == t) {
			
		} else {
			tabu.add(new IndexMap(selectCity, mustvisitedV.get(selectCity)));
		}
			
     //将当前城市改为选择的城市
    // currentCity = selectCity;
     currentCity = new IndexMap(selectCity, mustvisitedV.get(selectCity)) ;
     //currentCityIndex = selectCity;
     return currentCity;
  }
  
  /**
    * 计算蚂蚁总共走的路径长度
    * @return 路径长度
    */
	private Path calculateTourLength() {
		Path path = new Path();
		
		pathVertexs.clear();
		ArrayList<Integer> pathVertexs = new ArrayList<>();
		int len = 0;
		/*System.out.println("必经点");
		for (IndexMap ta : tabu) {
			System.out.print(ta.getMapIndex()+",");
		}
		System.out.println();*/
		//判断是否与上一次的路径相同
		//System.out.println(histTabu.size());
		/*System.out.print("这次的禁忌表： ");
		for (int i = 0; i < tabu.size() - 1; i++) {
			System.out.print(tabu.get(i).getMapIndex() + ",");
			
		}
		System.out.println();
		System.out.print("上一次禁忌表： ");
		for (int i = 0; i < tabu.size() - 1; i++) {
			System.out.print(histTabu.get(i).getMapIndex() + ",");
			
		}
		System.out.println();
		*/
	
		for (int i = 0; i < tabu.size() - 1; i++) {
			
			if(histTabu.get(i).getMapIndex() != tabu.get(i).getMapIndex()) {
				//System.out.println("不一样");
				//System.out.println("histTabu["+i+"]"+histTabu.get(i).getMapIndex());
				
				//System.out.println("tabu["+i+"]"+tabu.get(i).getMapIndex());	
				break;
			}else{
				if(i == tabu.size() - 2) {
					//System.out.println("与上次一样  跳过计算");
					return hisPath;
				}
			}
			
			
		}
		histTabu.clear();
		//计算当前的路径
		for (int i = 0; i < tabu.size() - 1; i++) {
			
			Path tmpPath = dijkstra(this.tabu.get(i).getMapIndex(), this.tabu.get(i + 1).getMapIndex(), pathVertexs);
			
			histTabu.add(tabu.get(i));//赋值给历史值
			
			String tempPathStr = tmpPath.getPathStr();
			if (i == 0) {
				path.setPathStr(tempPathStr);
			} else {
				path.setPathStr(path.getPathStr() + "|" + tempPathStr);
			}
			if(tempPathStr == null) {
				//System.out.println("空指针-------");
				len = MAXPATH;
				len += tmpPath.getLength();
				/*if(tmpPath == null) {
					System.out.println("tmpPath空指针-------");
				}*/
				break;
			}
			if(tmpPath.getPathStr().contains("-1")) {
				//System.out.println(tmpPath.getPathStr());
				len = MAXPATH;
				//len += tmpPath.getLength();
				//System.out.println("没有路径");
				//return path;
				break;
			}
			len += tmpPath.getLength();
			//将dijstra经过的路径点标记为true
			for (int j = 0; j < pathVertexs.size(); j++) {
				vertexs[pathVertexs.get(j)].setVisited(true);
			}
		}
		
		while(histTabu.size() < tabu.size() - 1) {
			histTabu.add(new IndexMap());
		}
		path.setLength(len);
		//清除dijstra访问点
		for (int j = 0; j < pathVertexs.size(); j++) {
			vertexs[pathVertexs.get(j)].setVisited(false);
		}
		hisPath = path;
		return path;
	}

	public ArrayList<IndexMap> getAllowedCities() {
		return allowedCities;
	}

	public void setAllowedCities(ArrayList<IndexMap> allowedCities) {
		this.allowedCities = allowedCities;
	}

	public Path getTourLength() {
		tourLength = calculateTourLength();
		return tourLength;
	}

	public void setTourLength(Path tourLength) {
		this.tourLength = tourLength;
	}

	public int getCityNum() {
		return cityNum;
	}

	public void setCityNum(int cityNum) {
		this.cityNum = cityNum;
	}

	public ArrayList<IndexMap> getTabu() {
		return tabu;
	}

	public void setTabu(ArrayList<IndexMap> tabu) {
		this.tabu = tabu;
	}

	public float[][] getDelta() {
		return delta;
	}

	public void setDelta(float[][] delta) {
		this.delta = delta;
	}

	public IndexMap getFirstCity() {
		return firstCity;
	}

	public void setFirstCity(IndexMap firstCity) {
		this.firstCity = firstCity;
	}
   
 }
