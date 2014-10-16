//MygdxGame this game is a simple representation of all my game with the majority of the game features implemented
//Author : James Foster
//Date: 28th September 2014

package com.mygdx.game;

import java.util.Arrays;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.MyGdxGame.Direction;

public class Entity {
	public MyGdxGame game;
	public float x;
	public float y;
	
	public float dx;
	public float dy;
	
	public int width;
	public int height; 
	
	public float speed;
	
	public Texture texture;
	public Pixmap pixmap;
	public int[][] bitmask;
	
	public int offsetRight;
	public int offsetLeft;
	public int offsetUp;
	public int offsetDown;
	
	private static String filename;
	
	public Entity(MyGdxGame game, float x, float y, int width, int height, float speed, Texture texture,String filename) {
		this.game = game;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.texture = texture;
		this.setFilename(filename);
		
		bitmask = createBitmask(filename);
		setOffsets();
	}

	/** This function goes through the sprites bitmasks and sees how much 
	 * 	empty space there is on each side of the sprite and saving these number of pixels
	 * 	as offsets to be used in tile collisions and entity collisions
	 */
	
	private void setOffsets() {
		for(int i = 0;i<height/2;i++){
			boolean check = false;
			for(int j = 0;j<width;j++){
				if(bitmask[i][j] == 1){
					check = true;
				}
			}
			if(check == false){
				offsetUp+=1;
			}
		}
		
		for(int i = width-1;i>width/2;i--){
			boolean check = false;
			for(int j = 0;j<height;j++){
				if(bitmask[j][i] == 1){
					check = true;
				}
			}
			if(check == false){
				offsetRight+=1;
			}
		}
		
		for(int i = 0;i<width/2;i++){
			boolean check = false;
			for(int j = 0;j<height;j++){
				if(bitmask[j][i] == 1){
					check = true;
				}
			}
			if(check == false){
				offsetLeft+=1;
			}
		}
		
		for(int i = height-1;i>height/2;i--){
			boolean check = false;
			for(int j = 0;j<width;j++){
				if(bitmask[i][j] == 1){
					check = true;
				}
			}
			if(check == false){
				offsetDown+=1;
			}
		}
		
//		System.out.println(offsetUp);
//		System.out.println(offsetDown);
//		System.out.println(offsetLeft);
//		System.out.println(offsetRight);
	}

	public boolean update(float delta) {
		return false;

	}
	
	public Pixmap getPixmap(){//returns a pixmap of the sprite
		return pixmap;
	}
	
	public void move(float newX, float newY) {
		
		if(Player.direction==true && game.thePlayer.getGunState() == 0 && Player.batState != true){
			game.thePlayer.setTextureLeft();//animations
		}
		else if(Player.direction==false && game.thePlayer.getGunState() == 0 && Player.batState != true){
			game.thePlayer.setTextureRight();
		}
		else if(Player.direction==true && game.thePlayer.getGunState() == 1 && Player.batState != true){
			game.thePlayer.setTextureGunLeft();
			//bitmask = Player.bitmaskGun;
		}
		else if(Player.direction == false && game.thePlayer.getGunState() == 1 && Player.batState != true){
			game.thePlayer.setTextureGunRight();
			 //bitmask = Player.bitmaskGun;
		}
		x = newX;
		y = newY;		
	}
	
	public void render() {
	}

	public void tileCollision(int tile, int tileX, int tileY, float newX, float newY, Direction direction) {
		//System.out.println("tile collision at: " + tileX + " " + tileY);	
	}

	public void entityCollision(Entity e2, Entity e1,float newX, float newY, Direction direction) {//deals with all the different possible collisions
		
		
		System.out.println("Actual e2 "+e2.getClass()+ "Actual e1 "+ e1.getClass());
		if(e2 instanceof PowerUp && e1 instanceof Player){
			game.thePlayer.texture = new Texture("AlienGun.png");
			game.thePlayer.setGunState(1);
		}
		else if(e2 instanceof Enemy && e1 instanceof Bullet){
			//System.out.println("e2 Enemy e1 Bullet");
			if(((Bullet) e1).isEnemyFire() == false){
				if(((Enemy)e2).hitcount == 3){
					game.entities.remove(e2);
				}
				else{
					((Enemy)e2).hitcount+=1;
				}
			}
			else{
			}
		}
		else if(e1 instanceof Bullet && e2 instanceof Player){
			//System.out.println("e1 Bullet e2 Player");
			game.entities.remove(e2);
			game.revivePlayer(((Player)e2).getGunState());
			SentryGun.setActive(false);
		}
		else if(e1 instanceof CaveBoss && e2 instanceof Player){
			//System.out.println("e1 CaveBoss e2 Player");
			game.entities.remove(e2);
			game.revivePlayer(((Player)e2).getGunState());
			SentryGun.setActive(false);
			((CaveBoss)e1).resetCaveBoss();
		}
		else if(e2 instanceof CaveBoss && e1 instanceof Bullet){
			System.out.println("e2 CaveBoss e1 Bullet");
			if(((CaveBoss)e2).hitcount == 7){
				System.out.println("Game over");
				game.entities.remove(e2);
				((CaveBoss)e2).futurePath.clear();
				game.entities.remove(e1);
			}
			else{
				System.out.println("hit");
				((CaveBoss)e2).hitcount+=1;
			}
		}
		move(newX,newY);
	}
	
	public int[][] createBitmask(String filename){
		FileHandle handle = Gdx.files.internal(filename);
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
	
	
	public int[][] getBitmask(){
		return bitmask;
	}
	
	public void printBitmask(){//helpermethod
		for(int i = 0; i<height;i++){
			System.out.println(Arrays.toString(bitmask[i]));
		}
	}

	public static String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		Entity.filename = filename;
	}
	
}
