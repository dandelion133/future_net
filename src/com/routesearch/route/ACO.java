package com.routesearch.route;

import java.io.IOException;
import java.util.ArrayList;

import com.filetool.util.LogUtil;



/**
 * 7: * 8: * @author BIAO YU 9: * 10: * 11:
 */
public class ACO {

	
	public static int antNum; // 蚂蚁数量
	Ant[] ants; // 蚂蚁
	public static int cityNum; // 城市数量
	public static int MAX_GEN; // 运行代数
	private float[][] pheromone; // 信息素矩阵
	//private int[][] distance; // 距离矩阵
	public static Edge[][] graph; 
	private Path bestLength = new Path(); // 最佳长度
	private int[] bestTour; // 最佳路径
	public static int s, t, maxVertex = -1, maxEdge = -1;
	public static ArrayList<Integer> mustvisitedV = new ArrayList<>();//需要包含t
	public static ArrayList<Integer> otherV = new ArrayList<>(); 
	public static final int MAXVNUM = 600;
	public static Vertex[] vertexs = new Vertex[MAXVNUM];
	// 三个参数
	public static float alpha;
	public static float beta;
	public static float rho;
	

	public ACO() {
		
	}
	//ACO aco = new ACO(48, 100, 1000, 1.f, 5.f, 0.5f);
	/*public ACO(int n, int m, int g, float a, float b, float r) {
		cityNum = n;
		antNum = m;
		ants = new Ant[antNum];
		MAX_GEN = g;
		alpha = a;
		beta = b;
		rho = r;

	}*/
	/**
	 * constructor of ACO 33: * @param n 城市数量 34: * @param m 蚂蚁数量 35: * @param g
	 * 运行代数 36: * @param a alpha 37: * @param b beta 38: * @param r rho 39: *
	 * 40:
	 **/
	public ACO(int cityNum, int antNum, int mAX_GEN, float alpha, float beta, float rho) {
		
		
		this.antNum = antNum;
		ants = new Ant[antNum];
		this.cityNum = cityNum;
		MAX_GEN = mAX_GEN;
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
	}

	
	
	/**
	 * 初始化ACO算法类
	 * @param filename 数据文件名，该文件存储所有城市节点       坐标数据   
	 * @throws IOException
	 */
	public void init() {
		ants = new Ant[antNum];//初始化数组
		// 初始化信息素矩阵
		pheromone = new float[cityNum][cityNum];
		for (int i = 0; i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++) {
				pheromone[i][j] = 0.1f; // 初始化为0.1
			}
		}
		//bestLength = Integer.MAX_VALUE;
		bestLength.setLength(Integer.MAX_VALUE);
		bestTour = new int[cityNum + 1];
		// 随机放置蚂蚁
		//initAnt();
		Ant.initWeight(mustvisitedV,otherV, t,vertexs,graph);
		for (int i = 0; i < antNum; i++) {//for (int i = 0; i < antNum; i++) 
			//
			ants[i] = new Ant(mustvisitedV,otherV, t,vertexs,graph);//
			//System.out.println("开始放置蚂蚁");//突然想起不能随机放
			//System.out.println("ants[i].init");
			ants[i].init(graph, alpha, beta);
		}
		//
		/*for (int i = 0;  i < cityNum; i++) {
			for (int j = 0; j < cityNum; j++) {
				
			}
		}
		System.out.println();*/
		//ants[antNum-1].initWeight();
		
	}

	/*private void initAnt() {
		Ant.mustvisitedV = mustvisitedV;
		Ant.otherV = otherV;
		Ant.t = t;
		Ant.vertexs = vertexs;
		Ant.graph = graph;
		
	}*/
	public Path solve() {
		System.out.println("t: " +t);
		System.out.println("------------------------");
		for (int g = 0; g < MAX_GEN; g++) {//for (int g = 0; g < MAX_GEN; g++)
			for (int i = 0; i < antNum; i++) {  //每只蚂蚁
				// LogUtil.printLog("Begin");
				//每只蚂蚁都让他随机选择下一个点  直到t为止
				for (int j = 1; j < cityNum; j++) {//遍历所有必经点
					int selectNextCity = ants[i].selectNextCity(pheromone).getMapIndex();
					//System.out.println("蚂蚁"+i+"已到点" + selectNextCity);
					/*if(selectNextCity == t) {
						System.out.println("已到");
						break;
					}	*/
					if(j == cityNum - 1) {
					//	System.out.println("已到最后一个必经点");
						ants[i].getTabu().add(new IndexMap(ants[i].getTabu().size(), t));
					}
				}
				/*System.out.println("-----------------------------------");
				for (int j = 0; j < ants[i].getTabu().size(); j++) {
					System.out.println("禁表："+ants[i].getTabu().get(j).getAntIndex()+"-"+ants[i].getTabu().get(j).getMapIndex());
				}
				System.out.println("-----------------------------------");*/
				//ants[i].getTabu().add(ants[i].getFirstCity());
				Path tourPath = ants[i].getTourLength();
				//String pathStr = ants[i].getTourLength().getPathStr();
				if (tourPath.getLength() != 0 && tourPath.getLength() < bestLength.getLength()) {
					bestLength = tourPath;
				//	System.out.println("bestLength:"+bestLength.getLength());
				//	System.out.println("路径： " + bestLength.getPathStr());
					
				}
				
				//每走一步 更新Delta()
				for (int j = 0; j < cityNum-1; j++) {//ants[i].getTabu().size() - 2
					int itourLength = ants[i].getTourLength().getLength();
					ants[i].getDelta()[ants[i].getTabu().get(j).getAntIndex()][ants[i].getTabu().get(j + 1).getAntIndex()] 
							= (float) (1. / itourLength);
					ants[i].getDelta()[ants[i].getTabu().get(j + 1).getAntIndex()][ants[i].getTabu().get(j).getAntIndex()] 
							= (float) (1. / itourLength);
				}
				updatePheromone();
			}
			 
			
			// 更新信息素
			
			// 重新初始化蚂蚁
			for (int i = 0; i < antNum; i++) {    //antNum

				ants[i].init(graph, alpha, beta);
			}
			//pheromone = new float[cityNum][cityNum];
			for (int i = 0; i < cityNum; i++) {
				for (int j = 0; j < cityNum; j++) {
					pheromone[i][j] = 0.1f; // 初始化为0.1
				}
			}
			System.out.println("------------------------------");
			//System.out.println("-----------");
			//System.exit(0);
		}
		//LogUtil.printLog("End");
		// 打印最佳结果
		//printOptimal();
		return bestLength;
	}

	// 更新信息素
	private void updatePheromone() {
		// 信息素挥发
		for (int i = 0; i < cityNum; i++)
			for (int j = 0; j < cityNum; j++)
				pheromone[i][j] = pheromone[i][j] * (1 - rho);
		// 信息素更新
		for (int i = 0; i < cityNum-1; i++) {
			for (int j = 0; j < cityNum-1; j++) {
				for (int k = 0; k < antNum; k++) {
				//	System.out.println("i :" + i + "j :" + j + "k:" + k);
					//System.out.println(cityNum);
					pheromone[i][j] += ants[k].getDelta()[i][j];
					
				}
			}
		}
	}

	/*private void printOptimal() {
		//System.out.println("The optimal length is: " + bestLength);
		System.out.println("The optimal:"+bestLength.getLength()+"-\n路径为"+bestLength.getPathStr());
		System.out.println("The optimal tour is: ");
		for (int i = 0; i < cityNum + 1; i++) {
			System.out.println(bestTour[i]);
		}
	}*/

	public Ant[] getAnts() {
		return ants;
	}

	public void setAnts(Ant[] ants) {
		this.ants = ants;
	}

	public int getAntNum() {
		return antNum;
	}

	public void setAntNum(int m) {
		this.antNum = m;
	}

	public int getCityNum() {
		return cityNum;
	}

	public void setCityNum(int cityNum) {
		this.cityNum = cityNum;
	}

	public int getMAX_GEN() {
		return MAX_GEN;
	}

	public void setMAX_GEN(int mAX_GEN) {
		MAX_GEN = mAX_GEN;
	}

	public float[][] getPheromone() {
		return pheromone;
	}

	public void setPheromone(float[][] pheromone) {
		this.pheromone = pheromone;
	}

	public Edge[][] getGraph() {
		return graph;
	}

	public void setGraph(Edge[][] graph) {
		this.graph = graph;
	}

	public Path getBestLength() {
		return bestLength;
	}

	public void setBestLength(Path bestLength) {
		this.bestLength = bestLength;
	}

	public int[] getBestTour() {
		return bestTour;
	}

	public void setBestTour(int[] bestTour) {
		this.bestTour = bestTour;
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getBeta() {
		return beta;
	}

	public void setBeta(float beta) {
		this.beta = beta;
	}

	public float getRho() {
		return rho;
	}

	public void setRho(float rho) {
		this.rho = rho;
	}

	/**
	 * 256: * @param args 257: * @throws IOException 258:
	 *//*
	public static void main(String[] args) throws IOException {
		ACO aco = new ACO(48, 100, 1000, 1.f, 5.f, 0.5f);
		aco.init("c://data.txt");
		aco.solve();
	}
*/
}