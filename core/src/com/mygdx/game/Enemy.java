//MygdxGame this game is a simple representation of all my game with the majority of the game features implemented
//Author : James Foster
//Date: 28th September 2014

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.MyGdxGame.Direction;

public class Enemy extends Entity {

	public boolean direction = false;
	float gravitySpeed = 1.5f;
	float terminal_velocity= 300;
	float gravityConstant = 0;
	boolean falling = true;
	public int hitcount;
	public int gunCounter;
	public Texture penguinRight;
	public Texture penguinLeft;
	
	public Enemy(MyGdxGame game, float x, float y, int width, int height,float speed, Texture texture,String filename) {
		super(game, x, y, width, height, speed, texture,filename);
		bitmask = createBitmask(filename);
		hitcount = 0;
		createTextures();
		//printBitmask();
		
	}
	
	private void createTextures() {
		penguinRight = new Texture("PenguinRight.png");
		penguinLeft = new Texture("Penguin.png");
	}

	public boolean update(float delta) {
		
		dx = 0;
		dy = 0;
		
		if(falling == true){//while the sprite is in falling mode and not jumping move it down
			dy=fall(dy);
		}
		
		if(game.tileCollision(this, Direction.D, x, y+dy)){//if the sprite is touching the tiles it may jump
			gravityConstant = 0;
			dy = 0;
			falling = false;
		}
		else{
			falling = true;
		}
	
		if(Math.abs(game.thePlayer.x - this.x)<(game.screenWidth/2) && Math.abs(game.thePlayer.y - this.y)<game.thePlayer.height*2){//if the player is within 50m chase else roam
			if(game.thePlayer.x > this.x){//follow the player
				dx = (speed * delta);
				texture = penguinRight;
				if(gunCounter%60== 0){
					Bullet newBullet = new Bullet(game,x+width,(y+height/2)-12,200.0f+dx,10,10,Player.bullet,"Bullet.png");
					game.bullets.add(newBullet);
					newBullet.setEnemyFire(true);
					newBullet.getSound().setVolume(0,0.5f);
					newBullet.getSound().play();
				}
			}
			else{
				texture = penguinLeft;
				dx = -1* (speed * delta);
				if(gunCounter%60 == 0){//shoot one bullet every second
					Bullet newBullet = new Bullet(game,x,(y+height/2),-200.0f,10,10,Player.bullet,"Bullet.png");
					game.bullets.add(newBullet);
					newBullet.setEnemyFire(true);
					newBullet.getSound().setVolume(0,0.5f);
					newBullet.getSound().play();
				}
			}
			gunCounter++;
		}
		else{
			if(direction==true){//Right
				texture = penguinRight;
				dx = (speed * delta);
			}
			else{
				texture = penguinLeft;
				dx = -1* (speed * delta);
			}
			gunCounter = 0;
		}
		
		if(game.wallCheck(this, x+dx, y+dy)){
			if(direction ==true){
				direction = false;
			}
			else{
				direction = true;
			}
		}
		return true;
		
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public float fall(float dy){//this function accelerates the sprites movement down until it reaches a terminal velocity or the ground
		gravityConstant = (gravityConstant)+0.1f;
		if(gravityConstant>terminal_velocity){
			dy = terminal_velocity;
		}
		else{
			dy = dy-gravityConstant-gravitySpeed;
		}
		return dy;
	}
	
	@Override
	public int[][] createBitmask(String filename){
		FileHandle handle = Gdx.files.internal("Penguin.png");
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

}
