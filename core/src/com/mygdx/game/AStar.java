//This class is the A star class that finds the shortest path to the target around obsticals
//Author: James Foster
//Date: 10th October 2014

package com.mygdx.game;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class AStar {
	
	public ArrayList<TileNode> closedList = new ArrayList<TileNode>();
	public PriorityQueue<TileNode> openList = new PriorityQueue<TileNode>();//open list maintains nodes to be checked
	public ArrayList<TileNode> path = new ArrayList<TileNode>();//the path that will be followed
	public int[][] theMap;
	public int mapWidth;
	public int mapHeight;
	
	public TileNode[][] theNodeMap;//map of nodes based on the underlying grid
	
	public String heurstic;//the type of heuristic used
	
	public AStar(int[][] theMap,int mapHeight,int mapWidth){
		this.theMap = theMap;
		this.mapHeight = mapHeight;
		this.mapWidth = mapWidth;
		theNodeMap  = new TileNode[mapWidth][mapHeight];
		setUpNodeMap();
	}
	
	public void setUpNodeMap(){//this function sets where the map has collidable blocks to collidable nodes
		for(int i = 0;i<mapWidth;i++){
			for(int j = 0; j<mapHeight;j++){
				if(theMap[i][j] == 1){
					theNodeMap[i][j] = new TileNode(i,j);
					theNodeMap[i][j].blocked = true;
				}
				else{
					theNodeMap[i][j] = new TileNode(i,j);
				}
			}
		}
	}
	
	public ArrayList<TileNode> findThePath(int StartX,int StartY,int EndX,int EndY){//this function preforms A star pathfinding to find the shortest path
		
		
		setUpNodeMap();//make sure the map is set up each time
		path.clear();
		
		TileNode currentNode;
		//reset lists
		closedList.clear();
		openList.clear();
		
		theNodeMap[StartX][StartY].TileCost = 0;//first tile
		theNodeMap[StartX][StartY].g = 0;//base level
		
		theNodeMap[StartX][StartY].parent = null;
		
		openList.add(theNodeMap[StartX][StartY]);//add starting node to the open list
		
		
		while(openList.size() !=0){//while there are still elements in the open list
			
			currentNode = openList.peek();//get the current node from the queue
			
			if(currentNode == theNodeMap[EndX][EndY]){//if the current node is the target node then end
				break;
			}
			
			openList.remove(currentNode);
			closedList.add(currentNode);//place in the closed list
			
			for(int x = -1;x<2;x++){//check neighbouring nodes to the current node
				for(int y = -1;y<2;y++){
					
					if(x == 0 && y == 0){//don't check the node itself
						continue;
					}
					
					int NeighBourX = currentNode.x+x;
					int NeighBourY = currentNode.y+y;
					
					if(checkOutOfBounds(NeighBourX,NeighBourY) && currentNode.blocked != true){//check that the neighbour is not a collidable block
						
						float nextNodeCost = currentNode.TileCost+1;//get the new cost
						
						TileNode theNeighbourNode  = theNodeMap[NeighBourX][NeighBourY];//get the neighbour
					
						if(nextNodeCost< theNeighbourNode.TileCost){
							
							if(openList.contains(NeighBourX)){
								openList.remove();
								
							}
							if(closedList.contains(theNeighbourNode)){
								closedList.remove(theNeighbourNode);
							}
							
						}
					
						if(!openList.contains(theNeighbourNode) && !closedList.contains(theNeighbourNode)){// if the neighbour is not in the open or closed add it to the open list
							theNeighbourNode.TileCost = nextNodeCost;//record its cost
							theNeighbourNode.h = getheuristicCost(theMap,NeighBourX,NeighBourY,EndX,EndY);//check the heuristic costs
							theNeighbourNode.setTileParent(currentNode);//make sure a path can be tracked through parent nodes
							openList.add(theNeighbourNode);//add node to open list
						
						}
					}
					
				}
			}
		}
		
		if(theNodeMap[EndX][EndY].parent == null){//if the targets parent node is null then there is no path
			return null;
		}
		
		TileNode theTarget = theNodeMap[EndX][EndY];
		TileNode theStart = theNodeMap[StartX][StartY];
		
		while(theTarget!=theStart){//create the path by going back through the parents
			path.add(theTarget);
			theTarget = theTarget.parent;
		}
		
		return path;
		
	}
	
	public float getheuristicCost(int[][] theMap,int x,int y, int futureX,int futureY){//calculates the heurstic cost based on the manhatten heuristic
		float dx = Math.abs(futureX-x);
		float dy = Math.abs(futureY-y);
		return  dx+dy;
	}
	
	public boolean checkOutOfBounds(int FutureX,int FutureY){//checks the node doesnt fall outside the map
		if(FutureX>=mapWidth || FutureY>= mapHeight || FutureX<0 || FutureY<0){
			return false;
		}
		else{
			return true;
		}
	}
	

}
