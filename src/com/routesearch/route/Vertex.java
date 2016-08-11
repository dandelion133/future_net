package com.routesearch.route;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
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
