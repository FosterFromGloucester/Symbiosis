//MygdxGame this game is a simple representation of all my game with the majority of the game features implemented
//Author : James Foster
//Date: 28th September 2014

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.MyGdxGame.Direction;

public class PowerUp extends Entity{
	
	public String Type;
	
	public PowerUp(MyGdxGame game, float x, float y, int width, int height, Texture texture,String filename) {
		super(game, x, y, width, height, 0, texture,filename);
	}
	
	@Override
	public void entityCollision(Entity e2,Entity e1, float newX, float newY, Direction direction) {
		game.thePlayer.texture = new Texture("AlienGun");
		game.thePlayer.setGunState(1);
		
	}
}
