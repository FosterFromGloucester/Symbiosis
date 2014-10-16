//MygdxGame this game is a simple representation of all my game with the majority of the game features implemented
//Author : James Foster
//Date: 28th September 2014

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Entity;
import com.mygdx.game.MyGdxGame.Direction;

public class Player extends Entity {
	
	  boolean jumpNow = false;
	  boolean jumpOld = false;
	  boolean canJump = false;
	  float jumpSpeed = 20.0f;
	  
	  float velocityY;
	  
	  float gravitySpeed = 1.5f;
	  
	  float terminal_velocity= 300;
	  
	  float gravityConstant = 0;
	  
	  boolean falling = true;
	  boolean jumping = false;
	  boolean flying = false;
	  
	  float jumpTime;
	  
	  public Texture texture;
	  
	  float limit = 2.0f;
	  float accum = 0;
	  
	  private static int GunState = 0;
	  public static boolean direction;
	  public int counter;
	  
	  public static Texture gunStateTextureLeft;
	  public static Texture gunStateTextureRight;
	  public static Texture textureLeft;
	  public static Texture textureRight;
	  public static Texture batTexture;
	  public static Texture bullet;
	  
	  public static int[][] bitmaskGun;
	  
	  public static boolean batState;
	  
	  public int batStateCounter;
	  
	  public boolean Alive = true;
	  
	
	public Player(MyGdxGame game, float x, float y, int width, int height, float speed, Texture texture,String filename) {
		super(game, x, y, width, height, speed, texture,filename);
		this.texture = texture;
		counter = 0;
		setTextures();
		Alive  =true;
	}

	@Override
	public boolean update(float delta) {
		
		dx = 0;
		dy = 0;
		counter+=1;
		
		jumpNow = Gdx.input.isKeyPressed(Keys.SPACE);
		canJump = false;
		
		if(batState == true ){
			if(jumpNow && !jumpOld && batStateCounter<400) {
				jumping = true;
				jumpTime = 1f;
				jumpSpeed = 10f;
			}
			else{
				falling = true;
				jumping = false;
			}
			batStateCounter++;
		}
	
		
		if(falling == true && jumping == false){//while the sprite is in falling mode and not jumping move it down
			dy=fall(dy);
		}
		else if(jumping == true){//if the sprite is currently jumping make sure it is being acted on by gravity and is slowly decelerating
			dy= dy + jumpSpeed/(jumpTime*jumpTime) - gravitySpeed;//decelerate over time
			jumpTime+=0.1f;//increase time jumping
			
			if(dy<0){//if the sprite has reached the top of its jump make it fall
				jumping = false;
				falling = true;
			}
		}
		
		if(game.tileCollision(this, Direction.D, x, y+dy)){//if the sprite is touching the tiles it may jump
			canJump= true;
			batState = false;
			jumpSpeed = 20.0f;
			gravityConstant = 0;
			dy = 0;
			falling = false;
			batStateCounter = 0;
		}
		else{
			falling = true;
		}
		
		//accum += Gdx.graphics.getDeltaTime();
		
		if((counter%10 == 0) && (Gdx.input.isKeyPressed(Keys.X)) && getGunState() ==1) {//if the Player has a gun
			if(direction == true){
				Bullet newBullet = new Bullet(game,x,y+height,-200.0f-dx,10,10,bullet,"Bullet.png");
				game.bullets.add(newBullet);
				newBullet.getSound().setVolume(0,0.5f);
				newBullet.getSound().play();
				
			}
			else{
				Bullet newBullet = new Bullet(game,x+width,y+height,200.0f+dx,10,10,bullet,"Bullet.png");
				game.bullets.add(newBullet);
				newBullet.getSound().setVolume(0,0.5f);
				newBullet.getSound().play();
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			dx = -speed * delta;
			Player.direction = true;
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			dx = speed * delta;
			Player.direction = false;
		}
		
		if(jumpNow && canJump) {//see if th e sprite can jump
		      falling = true;
		      jumping = true;
		      jumpTime = 1f;
		    }
		
		//jumpOld = jumpNow;
		
		if(dx == 0 && dy == 0){
			return false;
		}
		else{
			return true;
		}
		
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public float fall(float dy){//this function accelerates the sprites movement down until it reaches a terminal velocity or the ground
		if(batState == true){
		}
		else{
			gravityConstant = (gravityConstant)+0.1f;
		}
		if(gravityConstant>terminal_velocity){
			dy = terminal_velocity;
		}
		else{
			dy = dy-gravityConstant-gravitySpeed;
		}
		return dy;
	}
	
	public void setTextures(){
		textureRight = new Texture("Alien.png");
		textureLeft = new Texture("AlienLeft.png");
		gunStateTextureRight = new Texture("AlienGun.png");
		gunStateTextureLeft = new Texture("AlienGunLeft.png");
		bullet = new Texture("Bullet.png");
		batTexture = new Texture("AlienBat.png");
		bitmaskGun =super.createBitmask("AlienGun.png");
	}
	
	public void setTextureGunLeft(){
		texture= gunStateTextureLeft;
		setFilename("AlienGunLeft.png");
	}
	
	public void setTextureGunRight(){
		texture = gunStateTextureRight;
		setFilename("AlienGun.png");
	}

	public void setTextureLeft(){
		texture = textureLeft;
		setFilename("Alien.png");
	}

	public void setTextureRight(){
		texture = textureRight;
		setFilename("AlienLeft.png");
	}
	
	public void setBatTexture(){
		texture = batTexture;
		setFilename("AlienBat.png");
	}

	public int getGunState() {
		return GunState;
	}

	public void setGunState(int gunState) {
		GunState = gunState;
	}
	
}
