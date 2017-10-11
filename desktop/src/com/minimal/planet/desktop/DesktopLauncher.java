package com.minimal.planet.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.minimal.arkanoid.wrap.ArkanoidGame;
import com.minimal.planet.Planet;

import java.io.File;

import static com.badlogic.gdx.graphics.GL20.GL_LINEAR_MIPMAP_LINEAR;
import static com.badlogic.gdx.graphics.Texture.TextureFilter.MipMapLinearLinear;

public class DesktopLauncher {
	public static void main (String[] arg) {
		texturePacker(true);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//		new LwjglApplication(new Planet(), config);
		new LwjglApplication(new ArkanoidGame(), config);
	}

	private static void texturePacker(boolean force) {
		File source = new File("../../images");
		File target = new File("./");
		if (force || source.lastModified() > target.lastModified()) {
			if (force)
				System.out.println("force = true, pakujemy");
			else
				System.out.println("Tekstury są nowsze od atlasu, pakujemy");
			TexturePacker.Settings settings = new TexturePacker.Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.filterMag = MipMapLinearLinear;
			settings.filterMin = MipMapLinearLinear;
			TexturePacker.process(settings, "../../images", "./", "atlas");
		}
		else {
			System.out.println("Tekstury są starsze od atlasu, pomijam");
		}
	}
}
