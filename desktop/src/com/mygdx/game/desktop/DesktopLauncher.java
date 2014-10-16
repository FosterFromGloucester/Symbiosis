//MygdxGame this game is a simple representation of all my game with the majority of the game features implemented
//Author : James Foster
//Date: 28th September 2014

package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Symbiosis";
		config.height = 700;
		config.width = 550;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
