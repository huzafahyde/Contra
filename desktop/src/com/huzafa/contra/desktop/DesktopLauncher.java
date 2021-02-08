package com.huzafa.contra.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.huzafa.contra.GameClass;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GameClass(), config);
		config.width = 400 * 3;
		config.height = 250 * 3;
		
		config.title = "Contra";
		config.addIcon("Misc/contra.png", FileType.Internal);
	}
}
