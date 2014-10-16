//MygdxGame this game is a simple representation of all my game with the majority of the game features implemented
//Author : James Foster
//Date: 28th September 2014

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class Bullet extends Entity {
	
	public Sound machineGun;
	public boolean active;
	private static boolean enemyFire;
	
	public Bullet(MyGdxGame game,float x,float y,float speed,int width,int height,Texture texture,String filename){
		super(game, x, y, width, height, speed, texture,filename);
		setUpSound();
		setEnemyFire(false);
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	@Override
	public boolean update(float delta) {
		dx = speed* delta;
		return true;
	}
	
	public void setUpSound(){
		 machineGun = Gdx.audio.newSound(Gdx.files.internal("Laser.wav"));
	}
	
	public Sound getSound(){
		return machineGun;
	}

	public boolean isEnemyFire() {
		return enemyFire;
	}

	public void setEnemyFire(boolean enemyFire) {
		Bullet.enemyFire = enemyFire;
	}
	
}
