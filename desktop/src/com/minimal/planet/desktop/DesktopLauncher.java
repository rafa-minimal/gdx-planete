package com.minimal.planet.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.minimal.arkanoid.wrap.ArkanoidDebugGame;

import static com.minimal.planet.desktop.TexturePackKt.texturePacker;

public class DesktopLauncher {
	public static void main (String[] arg) {
		texturePacker(arg);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		new LwjglApplication(new Planet(), config);
//		new LwjglApplication(new ArkanoidGame(), config);
		new LwjglApplication(new ArkanoidDebugGame(), config);
	}
}
