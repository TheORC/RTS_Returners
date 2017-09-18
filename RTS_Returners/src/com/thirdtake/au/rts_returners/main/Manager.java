package com.thirdtake.au.rts_returners.main;


import java.awt.Color;

import com.Tylabobaid.Centaur.Graphics.GraphicsEngine;
import com.Tylabobaid.Centaur.Main.Game;
import com.Tylabobaid.Centaur.Main.Main;

public class Manager extends Main{ //This Class must extend Manager
	
	static Manager man = new Manager(); //This line is necissary for setup
	
	public static void main(String[] args) { //This is what starts the java program
		Game.startGame(man); //This starts the Game
	}

	@Override
	public void initialise() { //This runs once when first started
		
		setMultithreaded(true); //decides whether or not to use different threads for the rendering and ticking
		//note, the thread pool can still be used while this is set the false
		
		//The size of the inbuilt Thread Pool can be set,
		// USING: setThreadPoolSize(sizw); //default is 2
		
		setAudioPlaying(true); //determines if audio should play, default is false
		
		setWindowWidth(1920); //Default is 1280
		setWindowHeight(1080); //Default is 720
		
		setWindowTitle("Title"); //Default is "Centaur Engine"
		
		setTargetFPS(120); //Default is 60
	}
	
	@Override
	public void render(){ //Runs once per frame, should only be used to render
		//Graphics Engine is Included
		//Graphics Engine automatically scales the screen resolution from 1280 x 720. This resolution should be used for testing.
		
		//For Example:
		GraphicsEngine.renderBackground(Color.BLUE);
	}
	
	@Override
	public void tick(){ //Runs once per tick, should be used for calculations, not rendering
		//Mouse Listener is Included, note cooridinates are scaled depending on screen resolution
		// USING: MouseInput.getMouseX();
		// USING: MouseInput.getMouseY();
		// USING: MouseInput.leftClick();
		// USING: MouseInput.rightClick();
		
		//Key Listener is Included
		// USING: Keyinput.getkey(index)
		//This index of the desired key can be found,
		// USING: Keyinput.showingKeyPresses(true);
		//keys can be disabled untill pressed again,
		// USING: Keyinput.ignoreKey(index);
		
		//Perlin noise is Included
		// USING: setNoiseSeed(12345);
		// USING: generateNoise(width, height, scale, octaves, frequency, amplitude);
		
		//A Thread Pool is Included
		// USING: executeInThreadPool(task);
		
		//Audio Manager is Included
		// USING: AudioManager.playSound(clip);
		// USING: AudioManager.setBackgroundMusic(clip);
		// USING: AudioManager.setForegroundMusic(clip);
		// USING: AudioManager.stopBackgroundMusic();
		// USING: AudioManager.stopForegroundMusic();
		//A clip can be created by:
			//public static Clip click;
			//try {
			//	click = AudioSystem.getClip();
			//	click.open(AudioSystem.getAudioInputStream(new File("./resources/click.wav")));
			//} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			//	e.printStackTrace();
			//}
	}
}
