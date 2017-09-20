package com.thirdtake.au.rts_returners.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images {
	
	//Declare Images Here
	public static BufferedImage baseSprite;
	
	public static void load(){
		try {
			
			//Initialise Images Here
			baseSprite = ImageIO.read(new File("./resources/buildings/baseSprite.png"));
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}