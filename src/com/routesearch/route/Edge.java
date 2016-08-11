package com.routesearch.route;

import com.routesearch.route.Route;

public class Edge {
	private int index;
	private int weight;
	
	public Edge()
	{
		// TODO Auto-generated constructor stub
		index = -1;
		weight = Route.MAXPATH;
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
