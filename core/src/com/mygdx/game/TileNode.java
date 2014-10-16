package com.mygdx.game;

public class TileNode implements Comparable<Object> {

	public  int x;
	public  int y;
	public  float TileCost;
	public  float h;
	public  int g;
	public  TileNode parent;
	boolean blocked = false;
	
	public TileNode(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	public int setTileParent(TileNode tileParent){
		g = tileParent.g+1;
		this.parent = tileParent;
		return g;
	}

	@Override
	public int compareTo(Object arg0) {
		
		TileNode toComp = (TileNode) arg0;
		float Compf = toComp.h+toComp.TileCost;
		float f = this.h+this.TileCost;
		
		if(f>Compf){
			return 1;
		}
		else if(f<Compf){
			return -1;
		}
		else{
			return 0;
				
		}
	}

}
