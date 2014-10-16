//MygdxGame this game is a simple representation of all my game with the majority of the game features implemented
//Author : James Foster
//Date: 28th September 2014


package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class SentryGun extends Entity {
	
	public Sound GunSound;
	private static boolean active = false;
	public int counter = 0;
	public boolean switchedOff;

	public SentryGun(MyGdxGame game, float x, float y, int width, int height,float speed, Texture texture, String filename,boolean shootingDirection) {
		super(game, x, y, width, height, speed, texture, filename);
		switchedOff = false;
		setUpSound();
	}
	
	@Override
	public int[][] createBitmask(String filename){
		FileHandle handle = Gdx.files.internal("SentryGun.png");
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
	
	public void Fire() {
		if(isActive()== true && switchedOff == false){
			if((this.x<game.thePlayer.x) && counter%30 == 0){//Right
				Bullet newBullet = new Bullet(game,x+width,y+height-15,450.0f,10,10,Player.bullet,"Bullet.png");
				game.bullets.add(newBullet);
				newBullet.setEnemyFire(true);
				GunSound.setVolume(0,0.5f);
				GunSound.play();
			}
			else if((this.x>game.thePlayer.x) && counter%30 == 0){//Left
				Bullet newBullet = new Bullet(game,x,y+height-15,-450.0f,10,10,Player.bullet,"Bullet.png");
				game.bullets.add(newBullet);
				newBullet.setEnemyFire(true);
				GunSound.setVolume(0,0.5f);
				GunSound.play();
			}
		}
		
		counter++;
	}
	
	public void setUpSound(){
		GunSound = Gdx.audio.newSound(Gdx.files.internal("MACHINE_GUN_FINAL.wav"));
	}

	public static boolean isActive() {
		return active;
	}

	public static void setActive(boolean active) {
		SentryGun.active = active;
	}

}
