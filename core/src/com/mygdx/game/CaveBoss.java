//MygdxGame this game is a simple representation of all my game with the majority of the game features implemented
//Author : James Foster
//Date: 28th September 2014

package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class CaveBoss extends Entity {
	
	public int hitcount;
	public AStar aStar; 
	int counter = 0;
	public static ArrayList<TileNode> thePath = new ArrayList<TileNode>();
	public  ArrayList<Integer> futurePath = new ArrayList<Integer>();
	public static Texture futureTexture;
	public static FSM BossBrain;
	public int wanderX;
	public int wanderY;
	public Random randMachine;
	public int gunResetCounter;
	
	public CaveBoss(MyGdxGame game, float x, float y, int width, int height,float speed, Texture texture, String filename) {
		super(game, x, y, width, height, speed, texture, filename);
		aStar = new AStar(game.map,game.mapHeight,game.mapWidth);
		futureTexture  = new Texture("darkPath.png");
		BossBrain = new FSM();
		randMachine = new Random();
		gunResetCounter = 0;
	}
	
	@Override
	public int[][] createBitmask(String filename){
		FileHandle handle = Gdx.files.internal("Monster.png");
		pixmap = new Pixmap(handle);
		int[][] bitmask = new int[height][width];//create a bitmask of ints for every pixel in the sprite
		for(int i = 0;i<width;i++){//start at the top of the sprite moving down
			for(int j = 0;j<height;j++){//from 0 to the width of the sprite
				 Color c1 = new Color(0,0,0,0);
				 Color.rgba8888ToColor(c1,getPixmap().getPixel(i,j));
				 bitmask[j][i] = (int)c1.a;
			}
		}
		
		return bitmask;
	}
	
	public boolean update(float delta) {
		System.out.println(game.furthestSaveX*game.tileSize);
		if(game.thePlayer.x>game.furthestSaveX*game.tileSize){
			BossBrain.transitionToAttack();
			if(gunResetCounter == 0){
				game.thePlayer.setGunState(0);
				gunResetCounter++;
			}
		}
		else{
			BossBrain.transitionToWander();
		}
		if(BossBrain.getCurrentState().equals("Attack")){//if the player is within 50m chase else roam
			if(counter%speed ==0){
				thePath = aStar.findThePath((int)x/game.tileSize, (int)y/game.tileSize, (int)game.thePlayer.x/game.tileSize, (int)game.thePlayer.y/game.tileSize);
				//displayPath();
				chase();
			}
		}
		
		counter++;
		return true;
	}
	
	public void chase(){
		if(thePath.size()!=0){
			TileNode temp = thePath.get(thePath.size()-1);
			x = temp.x*game.tileSize;
			y = temp.y*game.tileSize;
			thePath.remove(temp);	
		}
	}
	
	public void wander(){
		if(thePath.size()!=0){
			TileNode temp = thePath.get(thePath.size()-1);
			x = temp.x*game.tileSize;
			y = temp.y*game.tileSize;
			thePath.remove(temp);	
		}
		else{
			setWanderCoords();
		}
	}
	
	public void displayPath(){
		game.batch.begin();
		for(int i = 0;i<thePath.size()-1;i++){
			game.batch.draw(futureTexture,thePath.get(i).x*game.tileSize, thePath.get(i).y*game.tileSize); 
		}
		game.batch.end();
	}
	
	public void setWanderCoords(){
	}
	
	public void resetCaveBoss(){
		game.entities.remove(this);
		game.thePlayer.setGunState(0);
		game.entities.add(new CaveBoss(game, 330*game.tileSize,28*game.tileSize,100 , 80, 20f, new Texture("Monster.png"),"Monster.png"));
	}

}
