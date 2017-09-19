package com.thirdtake.au.rts_returners.main;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.Tylabobaid.Centaur.Events.Keyinput;
import com.Tylabobaid.Centaur.Events.MouseInput;
import com.Tylabobaid.Centaur.Graphics.GraphicsEngine;
import com.Tylabobaid.Centaur.Main.PerlinNoise;
import com.Tylabobaid.Centaur.Main.Vector;
import com.thirdtake.au.rts_returners.enums.TileTypes;
import com.thirdtake.au.rts_returners.map.Tile;

public class InGameManager {
	private Tile[][] tiles; //2D array that contains the tiles on the current map
	PerlinNoise noise; //The Perlin Noise used to generate the map
	private BufferedImage mapImage; //image generated from the Perlin Noise
	
	Vector cameraOffset = new Vector(); //Vector that determines the position of the camera
	Vector screenCenter = new Vector();
	
	int mapSize = 256; //width/ height of the map (in tiles)
	int mapSeed = 12345; //seed used to generate the map
	int tileSize = 50; //size of each tile when rendering
	int renderRadius = 1000; //radius around player where objects will render
	int cameraTileX = 0; //Tile that the camera is positioned in
	int cameraTileY = 0;
	int cameraMovementSpeed = 20; //speed that the camera will move when arrow keys are used
	
	int selectedTileX = 0;
	int selectedTileY = 0;
	
	int hudX; //X- coordinate of the HUD (set when ticking the camera)
	int hudY; //Y- coordinate of the HUD (set when ticking the camera)
	int scaledScreenWidth; //Width of box shown on minimap (set when ticking the camera)
	int scaledScreenHeight; //height of box shown on minimap (set when ticking the camera)
	int minimapScale = 1; //Factor used to scale up/down minimap size
	
	public InGameManager(){
		noise = new PerlinNoise(mapSeed); //initialises the Perlin Noise Used
		tiles = newMap(mapSize,mapSize); //generates the map
		
		mapImage = new BufferedImage(mapSize,mapSize,BufferedImage.TYPE_INT_ARGB); //creates the map image
		for(int x = 0; x < tiles.length; x ++){ //Loops through every pixel of the image
			for(int y = 0; y < tiles[0].length; y ++){
				Color mapColour = new Color(255,255,255); //Colour is initially set to White (should never be seen)
				
				if(tiles[x][y].getType() == TileTypes.GROUND){ //Determines the Colour of the corrosponding pixel on the image
					mapColour = new Color(0,255,0);
				}else if(tiles[x][y].getType() == TileTypes.WATER){
					mapColour = new Color(0,0,255);
				}else if(tiles[x][y].getType() == TileTypes.SAND){
					mapColour = new Color(255,255,100);
				}else if(tiles[x][y].getType() == TileTypes.ROCKS){
					mapColour = new Color(100,100,100);
				}
				mapImage.setRGB(x, y, mapColour.getRGB()); //Sets the Colour of each pixel
			}
		}

	}
	
	private Tile[][] newMap(int w, int h) {
		Tile[][] tiles = new Tile[w][h];
		float[][] floats = noise.generateNoiseMap(w, h, 0.05f, 3, 0.8f, 1); //These Values may require some tweaking
		
		for(int x = 0; x < tiles.length; x ++){ //Loops through each tile
			for(int y = 0; y < tiles[0].length; y ++){
				if(floats[x][y] <= -0.2){ //Perlin noise generates a height map. Height values are checked to determine tile type
					tiles[x][y] = new Tile(TileTypes.WATER);
				}else if(floats[x][y] <= -0.1){
					tiles[x][y] = new Tile(TileTypes.SAND);
				}else if(floats[x][y] <= 0.4){
					tiles[x][y] = new Tile(TileTypes.GROUND);
				}else {
					tiles[x][y] = new Tile(TileTypes.ROCKS);
				}
			}
		}
		
		return tiles;
	}

	public void tick(){
		tickCamera(); //Performs all Camera Calculations
	}
	
	public void tickCamera(){
		screenCenter = new Vector(GraphicsEngine.getWidth()/2, GraphicsEngine.getHeight()/2); //Sets to the Center of the screen (for rendering Purposes)
		cameraTileX = (int) Math.floor(cameraOffset.getX()/tileSize); //Sets the current tile of the Camera
		cameraTileY = (int) Math.floor(cameraOffset.getY()/tileSize);
		
		hudX = 10;
		hudY = GraphicsEngine.HEIGHT-(mapSize*minimapScale)-10; //May Require Tweaking
		
		scaledScreenWidth = (int) (minimapScale*screenCenter.getX()*2/tileSize); //sets the size of the screen shown on the minimap
		scaledScreenHeight = (int) (minimapScale*screenCenter.getY()*2/tileSize);
		
		//Checking Arrow keys to move Camera
		if(Keyinput.getkey(38)){ //UP
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() - cameraMovementSpeed); //Moves Camera UP
		}
		if(Keyinput.getkey(40)){ //DOWN
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() + cameraMovementSpeed); //Moves Camera Down
		}
		
		if(Keyinput.getkey(37)){ //LEFT
			cameraOffset = new Vector(cameraOffset.getX() - cameraMovementSpeed, cameraOffset.getY()); //Moves Camera Left
		}
		if(Keyinput.getkey(39)){ //RIGHT
			cameraOffset = new Vector(cameraOffset.getX() + cameraMovementSpeed, cameraOffset.getY()); //Moves Camera Right
		}
		
		//Ensuring that the Camera stays on the map
		while(cameraOffset.getX() < screenCenter.getX()){
			cameraOffset = new Vector(cameraOffset.getX() + 1, cameraOffset.getY());
		}
		while(cameraOffset.getX() > (tiles.length*tileSize)-screenCenter.getX()){
			cameraOffset = new Vector(cameraOffset.getX() - 1, cameraOffset.getY());
		}
		while(cameraOffset.getY() < screenCenter.getY()){
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() + 1);
		}
		while(cameraOffset.getY() > (tiles[0].length*tileSize)-screenCenter.getY()){
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() - 1);
		}
				
		if(MouseInput.getMouseX() >= hudX && MouseInput.getMouseX() <= hudX + mapSize*minimapScale){ //When the mouseX is within the minimap
			if(MouseInput.getMouseY() >= hudY && MouseInput.getMouseY() <= hudY + mapSize*minimapScale){ //When the mouseY is within the minimap
				if(MouseInput.leftClick()){
					cameraOffset = new Vector(tileSize*(MouseInput.getMouseX()/minimapScale)-hudX, tileSize*(MouseInput.getMouseY()-hudY)/minimapScale); //Not Sure why this isn't perfect. Kind of Works
				}
			}
		}
		selectedTileX = (int) Math.floor((MouseInput.getMouseX()-screenCenter.getX()+cameraOffset.getX())/tileSize);
		selectedTileY = (int) Math.floor((MouseInput.getMouseY()-screenCenter.getY()+cameraOffset.getY())/tileSize);
	
		//Checking Mouse Position to move Camera
		if(MouseInput.getMouseY() <= 100){ //UP
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() - cameraMovementSpeed); //Moves Camera UP
		}
		if(MouseInput.getMouseY() >= GraphicsEngine.getHeight()-100){ //DOWN
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() + cameraMovementSpeed); //Moves Camera Down
		}
				
		if(MouseInput.getMouseX() <= 100){ //LEFT
			cameraOffset = new Vector(cameraOffset.getX() - cameraMovementSpeed, cameraOffset.getY()); //Moves Camera Left
		}
		if(MouseInput.getMouseX() >= GraphicsEngine.getWidth()-100){ //RIGHT
			cameraOffset = new Vector(cameraOffset.getX() + cameraMovementSpeed, cameraOffset.getY()); //Moves Camera Right
		}
	}
	
	public void render(){
		renderBackground();
		renderHUD();
	}
	
	public void renderBackground(){
		//Drawing Background
		int tileRadius = renderRadius/tileSize; //Radius of Tiles that will be rendered
		for(int xx = -tileRadius; xx < tileRadius; xx ++){ //Loops through all tiles on the screen that are within the Radius
			for(int yy = -tileRadius; yy < tileRadius; yy ++){
				int x = xx+cameraTileX;
				int y = yy+cameraTileY;
						
				if(x >= 0 && x < tiles.length){ //Checks for null-pointer errors (trying the draw tiles that don't exist)
					if(y >= 0 && y < tiles[0].length){
						if(tiles[x][y].getType() == TileTypes.GROUND){ //These Colours are placeholders, will be replaced by sprites
							GraphicsEngine.setColor(new Color(0,100,0));
						}else if(tiles[x][y].getType() == TileTypes.WATER){
							GraphicsEngine.setColor(new Color(0,0,255));
						}else if(tiles[x][y].getType() == TileTypes.SAND){
							GraphicsEngine.setColor(new Color(255,255,100));
						}else if(tiles[x][y].getType() == TileTypes.ROCKS){
							GraphicsEngine.setColor(new Color(100,100,100));
						}
						
						GraphicsEngine.rect((int) ((x*tileSize)-cameraOffset.getX()+screenCenter.getX()),(int) ((y*tileSize)-cameraOffset.getY()+screenCenter.getY()), tileSize+1, tileSize+1); //Draws the Tiles (tileSize+1 is used to remove borders around tiles)
						
						if(x == selectedTileX && y == selectedTileY){
							if(tiles[x][y].getCanBuild()){
								GraphicsEngine.setColor(new Color(0,255,0,150));
							}else{
								GraphicsEngine.setColor(new Color(255,0,0,150));
							}
							GraphicsEngine.rect((int) ((x*tileSize)-cameraOffset.getX()+screenCenter.getX()),(int) ((y*tileSize)-cameraOffset.getY()+screenCenter.getY()), tileSize+1, tileSize+1); //Draws the Tiles (tileSize+1 is used to remove borders around tiles)
						}
					}
				}
			}
		}
	}
	
	public void renderHUD(){
		//Drawing HUD
		GraphicsEngine.renderImage(mapImage, hudX, hudY, mapSize*minimapScale, mapSize*minimapScale); //Draws the map image
		
		GraphicsEngine.setColor(new Color(255, 255, 255));
		GraphicsEngine.outLineRect(hudX + (cameraTileX*minimapScale) - (scaledScreenWidth/2), hudY + (cameraTileY*minimapScale) - (scaledScreenHeight/2),scaledScreenWidth, scaledScreenHeight); //Draws the Screen Representation on the Minimap
		
		GraphicsEngine.setColor(new Color(0,0,0));
		GraphicsEngine.outLineRect(hudX, hudY, mapSize*minimapScale, mapSize*minimapScale); //Draws a black outline around the minimap
	}
}
