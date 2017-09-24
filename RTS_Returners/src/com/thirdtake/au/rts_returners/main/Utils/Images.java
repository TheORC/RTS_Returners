package com.thirdtake.au.rts_returners.main.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Oliver Clarke, Aiden Jacobs
 *
 */
public class Images {

	// Declare Images Here
	public static BufferedImage baseSprite;

	public static void load() {
		try {

			// Initialise Images Here
			baseSprite = ImageIO.read(new File("./resources/buildings/baseSprite.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param filePath - Path to the file being loaded
	 * @return The loaded image or null.
	 */
	public static BufferedImage LoadImage(String filePath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}