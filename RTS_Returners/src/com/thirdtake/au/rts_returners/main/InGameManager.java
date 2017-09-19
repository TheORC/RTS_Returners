package com.thirdtake.au.rts_returners.main;

import java.awt.Color;
import java.awt.image.BufferedImage;

import com.Tylabobaid.Centaur.Events.Keyinput;
import com.Tylabobaid.Centaur.Events.MouseInput;
import com.Tylabobaid.Centaur.Graphics.GraphicsEngine;
import com.Tylabobaid.Centaur.Main.PerlinNoise;
import com.Tylabobaid.Centaur.Main.Vector;
import com.thirdtake.au.rts_returners.Buildings.Building;
import com.thirdtake.au.rts_returners.enums.TileTypes;
import com.thirdtake.au.rts_returners.map.Tile;

public class InGameManager {
	private Tile[][] tiles; //2D array that contains the tiles on the current map
	private float[][] heightMap; //2D array that contains the tiles on the current map
	private float[][] HDHeightMap; //2D array that contains the tiles on the current map
	PerlinNoise noise; //The Perlin Noise used to generate the map
	private BufferedImage mapImage; //image generated from the Perlin Noise
	private BufferedImage HDMapImage; //HD version of the map
	
	Vector cameraOffset = new Vector(); //Vector that determines the position of the camera
	Vector displayCorner = new Vector();
	
	int mapSize = 256; //width/ height of the map (in tiles)
	int mapSeed = 420420; //seed used to generate the map
	int tileSize = 25; //size of each tile when rendering
	int renderRadius = 1000; //radius around player where objects will render
	int cameraTileX = 0; //Tile that the camera is positioned in
	int cameraTileY = 0;
	int cameraMaxMovementSpeed = 20; //Maximum Speed that the camera will move
	int borderWidth = 150; //border of screen where the mouse can move the camera
	
	int selectedTileX = 0; //The tile that the mouse is hovering over
	int selectedTileY = 0;
	
	int hudX = 10; //X- coordinate of the HUD (set when ticking the camera)
	int hudY = 10; //Y- coordinate of the HUD (set when ticking the camera)
	
	int scaledScreenWidth; //Width of box shown on minimap (set when ticking the camera)
	int scaledScreenHeight; //height of box shown on minimap (set when ticking the camera)
	int minimapScale = 1; //Factor used to scale up/down minimap size
	
	int HDScale = 5; //Facor used to increase quality of map
	
	int leftBorder;
	int rightBorder;
	int topBorder;
	int bottomBorder;
	
	private Building mouseBuilding = null;
	
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
		
		HDMapImage = new BufferedImage(mapSize*HDScale,mapSize*HDScale,BufferedImage.TYPE_INT_ARGB); //creates the map image
		for(int i = 0; i < tiles.length; i ++){ //Loops through every pixel of the image
			for(int j = 0; j < tiles[0].length; j ++){
				for(int xx = 0; xx < HDScale; xx ++){
					for(int yy = 0; yy < HDScale; yy ++){
						
						int x = (i*HDScale)+xx;
						int y = (j*HDScale)+yy;
						
						Color mapColour = new Color(255,255,255); //Colour is initially set to White (should never be seen)

						int shade = (int) (255*(HDHeightMap[x][y]*1.4)); //Determines the shade of grass
						if(shade > 200){
							shade = 200;
						}else if(shade < 50){
							shade = 50;
						}
						shade = 255-shade;
						
						int waterShade = (int) (255*Math.abs(HDHeightMap[x][y])); //Determines the shade of water
						if(waterShade > 255){
							waterShade = 255;
						}else if(waterShade < 50){
							waterShade = 50;
						}
						waterShade = 255-waterShade;
						
						int rockShade = (int) (255*(HDHeightMap[x][y])); //Determines the shade of Rocks
						if(rockShade > 250){
							rockShade = 250;
						}else if(rockShade < 50){
							rockShade = 50;
						}
						
//						System.out.println(rockShade);
						
						if(tiles[i][j].getType() == TileTypes.GROUND){ //Determines the Colour of the corrosponding pixel on the image
							mapColour = new Color(0,shade,0);
						}else if(tiles[i][j].getType() == TileTypes.WATER){
							mapColour = new Color(0,0,waterShade);
						}else if(tiles[i][j].getType() == TileTypes.SAND){
							mapColour = new Color(255,255,100);
						}else if(tiles[i][j].getType() == TileTypes.ROCKS){
							mapColour = new Color(rockShade,rockShade,rockShade);
						}
						
						HDMapImage.setRGB(x, y, mapColour.getRGB()); //Sets the Colour of each pixel
					}
				}

			}
		}
	}
	
	private Tile[][] newMap(int w, int h) {
		Tile[][] tiles = new Tile[w][h];
		float[][] floats = noise.generateNoiseMap(w, h, 0.05f, 3, 0.8f, 1); //These Values may require some tweaking
		HDHeightMap = noise.generateNoiseMap(w*HDScale, h*HDScale, 0.05f/HDScale, 3, 0.8f, 1); //These Values may require some tweaking

		
		for(int x = 0; x < tiles.length; x ++){ //Loops through each tile
			for(int y = 0; y < tiles[0].length; y ++){
				if(floats[x][y] <= -0.4){ //Perlin noise generates a height map. Height values are checked to determine tile type
					tiles[x][y] = new Tile(TileTypes.WATER);
				}else if(floats[x][y] <= -0.3){
					tiles[x][y] = new Tile(TileTypes.SAND);
				}else if(floats[x][y] <= 0.4){
					tiles[x][y] = new Tile(TileTypes.GROUND);
				}else {
					tiles[x][y] = new Tile(TileTypes.ROCKS);
				}
			}
		}
		
		heightMap = floats;
		return tiles;
	}

	public void tick(){
		tickCamera(); //Performs all Camera Calculations
		tickTiles();
	}

	private void tickTiles() {
		if(MouseInput.leftClick() && mouseInsideWindow()){
			if(mouseBuilding != null){
				if(selectedTileX >= 0 && selectedTileX < tiles.length){
					if(selectedTileY >= 0 && selectedTileY < tiles[0].length){
						tiles[selectedTileX][selectedTileY].build(mouseBuilding);
						
						//remove required materials here
						
						mouseBuilding = null;
					}
				}
			}
		}
		
		for(int x = 0; x < tiles.length; x++){
			for(int y = 0; y < tiles[0].length; y ++){
				tiles[x][y].tickBuilding();
			}
		}
	}

	private boolean mouseInsideWindow() {
		return (MouseInput.getMouseX() >= leftBorder && MouseInput.getMouseX() <= rightBorder && MouseInput.getMouseY() >= topBorder && MouseInput.getMouseY() <= bottomBorder);
	}

	public void tickCamera(){
		
		leftBorder = mapSize*minimapScale+20;
		rightBorder = GraphicsEngine.getWidth()-10;
		topBorder = 10;
		bottomBorder = GraphicsEngine.getHeight()-10;
		
		displayCorner = new Vector(leftBorder, topBorder); //Sets to the Center of the screen (for rendering Purposes)
		cameraTileX = (int) Math.floor(cameraOffset.getX()/tileSize); //Sets the current tile of the Camera
		cameraTileY = (int) Math.floor(cameraOffset.getY()/tileSize);
		
		scaledScreenWidth = (int) (minimapScale*(rightBorder-leftBorder)/tileSize); //sets the size of the screen shown on the minimap
		scaledScreenHeight = (int) (minimapScale*(bottomBorder-topBorder)/tileSize);
		
		//Checking Arrow keys to move Camera
		if(Keyinput.getkey(38)){ //UP
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() - cameraMaxMovementSpeed); //Moves Camera UP
		}
		if(Keyinput.getkey(40)){ //DOWN
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() + cameraMaxMovementSpeed); //Moves Camera Down
		}
		
		if(Keyinput.getkey(37)){ //LEFT
			cameraOffset = new Vector(cameraOffset.getX() - cameraMaxMovementSpeed, cameraOffset.getY()); //Moves Camera Left
		}
		if(Keyinput.getkey(39)){ //RIGHT
			cameraOffset = new Vector(cameraOffset.getX() + cameraMaxMovementSpeed, cameraOffset.getY()); //Moves Camera Right
		}
		
		//Ensuring that the Camera stays on the map
		while(cameraOffset.getX()-(leftBorder/tileSize)+10 < 0){
			cameraOffset = new Vector(cameraOffset.getX() + 1, cameraOffset.getY());
		}
		while(cameraOffset.getX()+(rightBorder-leftBorder) > (tiles.length*tileSize)){
			cameraOffset = new Vector(cameraOffset.getX() - 1, cameraOffset.getY());
		}
		while(cameraOffset.getY()-(topBorder/tileSize) < 0){
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() + 1);
		}
		while(cameraOffset.getY()+(bottomBorder-topBorder) > (tiles[0].length*tileSize)){
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() - 1);
		}
				
		if(MouseInput.getMouseX() >= hudX && MouseInput.getMouseX() <= hudX + mapSize*minimapScale){ //When the mouseX is within the minimap
			if(MouseInput.getMouseY() >= hudY && MouseInput.getMouseY() <= hudY + mapSize*minimapScale){ //When the mouseY is within the minimap
				if(MouseInput.leftClick()){
					cameraOffset = new Vector((tileSize*(MouseInput.getMouseX()/minimapScale)-hudX)-((rightBorder-leftBorder)/2), (((tileSize*MouseInput.getMouseY())/minimapScale)-hudY)-((bottomBorder-topBorder)/2));
				}
			}
		}
		
		if(mouseInsideWindow()){
			selectedTileX = (int) Math.floor((MouseInput.getMouseX()-displayCorner.getX()+cameraOffset.getX())/tileSize);
			selectedTileY = (int) Math.floor((MouseInput.getMouseY()-displayCorner.getY()+cameraOffset.getY())/tileSize);	
		}else{
			selectedTileX = -1;
			selectedTileY = -1;
		}
		
		//Checking Mouse Position to move Camera
		if(mouseInsideWindow()){
			int deltaX = rightBorder - MouseInput.getMouseX(); //Distance from mouse to right side of the screen
			int deltaY = bottomBorder-MouseInput.getMouseY(); //Distance from the mouse to bottom of the screen
			
			if(MouseInput.getMouseY() <= topBorder+borderWidth){ //UP
				int speed = cameraMaxMovementSpeed-(((MouseInput.getMouseY()-topBorder)*cameraMaxMovementSpeed)/borderWidth);
				cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() - speed); //Moves Camera UP
			}
			if(deltaY <= borderWidth){ //DOWN
				int speed = cameraMaxMovementSpeed-((deltaY*cameraMaxMovementSpeed)/borderWidth);
				cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() + speed); //Moves Camera Down
			}
					
			if(MouseInput.getMouseX() <= leftBorder+borderWidth){ //LEFT
				int speed = cameraMaxMovementSpeed-(((MouseInput.getMouseX()-leftBorder)*cameraMaxMovementSpeed)/borderWidth);
				cameraOffset = new Vector(cameraOffset.getX() - speed, cameraOffset.getY()); //Moves Camera LEFT
			}
			if(deltaX <= borderWidth){ //RIGHT
				int speed = cameraMaxMovementSpeed-((deltaX*cameraMaxMovementSpeed)/borderWidth);
				cameraOffset = new Vector(cameraOffset.getX() + speed, cameraOffset.getY()); //Moves Camera RIGHT
			}
		
		}
	}
	
	public void render(){
		renderBackgroundImgage();
		renderBuildings();
		renderHUD();
	}
	
	private void renderBuildings() {
		int tileRadius = renderRadius/tileSize; //Radius of Tiles that will be rendered
		for(int xx = -tileRadius; xx < tileRadius; xx ++){ //Loops through all tiles on the screen that are within the Radius
			for(int yy = -tileRadius; yy < tileRadius; yy ++){
				int x = xx+cameraTileX;
				int y = yy+cameraTileY;
				if(x >= 0 && x < tiles.length){ //Checks for null-pointer errors (trying the draw tiles that don't exist)
					if(y >= 0 && y < tiles[0].length){
						if(tiles[x][y].getContainsBuilding()){
							
							GraphicsEngine.setColor(new Color(255,0,0));
							GraphicsEngine.rect((int) ((x*tileSize)-cameraOffset.getX()+displayCorner.getX()),(int) ((y*tileSize)-cameraOffset.getY()+displayCorner.getY()), tileSize, tileSize); //Draws the Tiles (tileSize+1 is used to remove borders around tiles)
		
							GraphicsEngine.setColor(new Color(255,255,255));
							GraphicsEngine.outLineRect((int) ((x*tileSize)-cameraOffset.getX()+displayCorner.getX()),(int) ((y*tileSize)-cameraOffset.getY()+displayCorner.getY()), tileSize, tileSize); //Draws the Tiles (tileSize+1 is used to remove borders around tiles)
						
							GraphicsEngine.setColor(new Color(0,0,0));
							GraphicsEngine.outLineRect((int) ((x*tileSize)-cameraOffset.getX()+displayCorner.getX()) -1,(int) ((y*tileSize)-cameraOffset.getY()+displayCorner.getY()) -1, tileSize+2, tileSize+2); //Draws the Tiles (tileSize+1 is used to remove borders around tiles)
		
						}
					}
				}
			}
		}
	}

	private void renderBackgroundImgage() { //renders the background as an image
		GraphicsEngine.renderImage(HDMapImage,(int) (-cameraOffset.getX()+displayCorner.getX()),(int) (-cameraOffset.getY()+displayCorner.getY()), mapSize*tileSize, mapSize*tileSize);
	
		if(mouseBuilding != null){
			if(selectedTileX >= 0 && selectedTileX < tiles.length){
				if(selectedTileY >= 0 && selectedTileY < tiles[0].length){
					if(tiles[selectedTileX][selectedTileY].getCanBuild()){
						GraphicsEngine.setColor(new Color(0,255,0,100));
					}else{
						GraphicsEngine.setColor(new Color(255,0,0,100));
					}
					GraphicsEngine.rect((int) ((selectedTileX*tileSize)-cameraOffset.getX()+displayCorner.getX()),(int) ((selectedTileY*tileSize)-cameraOffset.getY()+displayCorner.getY()), tileSize, tileSize); //Draws the Tiles (tileSize+1 is used to remove borders around tiles)
	
					GraphicsEngine.setColor(new Color(255,255,255));
					GraphicsEngine.outLineRect((int) ((selectedTileX*tileSize)-cameraOffset.getX()+displayCorner.getX()),(int) ((selectedTileY*tileSize)-cameraOffset.getY()+displayCorner.getY()), tileSize, tileSize); //Draws the Tiles (tileSize+1 is used to remove borders around tiles)
		
					GraphicsEngine.setColor(new Color(0,0,0));
					GraphicsEngine.outLineRect((int) ((selectedTileX*tileSize)-cameraOffset.getX()+displayCorner.getX()) -1,(int) ((selectedTileY*tileSize)-cameraOffset.getY()+displayCorner.getY()) -1, tileSize+2, tileSize+2); //Draws the Tiles (tileSize+1 is used to remove borders around tiles)
	
				}
			}
		}
	}
	
	public void renderHUD(){
		//Drawing HUD
		
		GraphicsEngine.setColor(new Color(100,100,100));
//		GraphicsEngine.rect(10, 10, ( mapSize*minimapScale), GraphicsEngine.getHeight()-(30+( mapSize*minimapScale)));

		GraphicsEngine.rect(0, 0, GraphicsEngine.getWidth(), topBorder);
		
		GraphicsEngine.rect(0, 0, leftBorder, GraphicsEngine.getHeight());
//		
		GraphicsEngine.rect(rightBorder, 0, GraphicsEngine.getWidth()-rightBorder, GraphicsEngine.getHeight());
//		
		GraphicsEngine.rect(0, bottomBorder, GraphicsEngine.getWidth(), GraphicsEngine.getHeight()-bottomBorder);
		
		GraphicsEngine.setColor(new Color(0,0,0));
		GraphicsEngine.outLineRect(leftBorder, topBorder, rightBorder-leftBorder, bottomBorder-topBorder);
		GraphicsEngine.setColor(new Color(255,255,255));
		GraphicsEngine.outLineRect(leftBorder+1, topBorder+1, rightBorder-leftBorder-2, bottomBorder-topBorder-2);	
		
		GraphicsEngine.renderImage(mapImage, hudX, hudY, mapSize*minimapScale, mapSize*minimapScale); //Draws the map image
		
		GraphicsEngine.setColor(new Color(255, 255, 255));
		GraphicsEngine.outLineRect(hudX + (cameraTileX*minimapScale), hudY + (cameraTileY*minimapScale), scaledScreenWidth, scaledScreenHeight); //Draws the Screen Representation on the Minimap
		
		GraphicsEngine.setColor(new Color(0,0,0));
		GraphicsEngine.outLineRect(hudX, hudY, mapSize*minimapScale, mapSize*minimapScale); //Draws a black outline around the minimap
	}
}
