package com.thirdtake.au.rts_returners.main.game;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.Tylabobaid.Centaur.Collisions.BoundaryBox;
import com.Tylabobaid.Centaur.Events.Button;
import com.Tylabobaid.Centaur.Events.Keyinput;
import com.Tylabobaid.Centaur.Events.MouseInput;
import com.Tylabobaid.Centaur.Graphics.GraphicsEngine;
import com.Tylabobaid.Centaur.Main.PerlinNoise;
import com.Tylabobaid.Centaur.Main.Vector;
import com.thirdtake.au.rts_returners.main.game.Buildings.Base;
import com.thirdtake.au.rts_returners.main.game.Buildings.Building;
import com.thirdtake.au.rts_returners.main.game.Enums.TileTypes;
import com.thirdtake.au.rts_returners.main.game.Enums.UnitTypes;
import com.thirdtake.au.rts_returners.main.game.Map.Tile;
import com.thirdtake.au.rts_returners.main.game.Units.Unit;

public class InGameManager {
	private Tile[][] tiles; // 2D array that contains the tiles on the current
							// map
//	private float[][] heightMap; // 2D array that contains the height of tiles on the
									// current map
	private float[][] HDHeightMap; // 2D array that contains the tiles on the
									// current map
	PerlinNoise noise; // The Perlin Noise used to generate the map
	private BufferedImage mapImage; // image generated from the Perlin Noise
	private BufferedImage HDMapImage; // HD version of the map

	Vector cameraOffset = new Vector(); // Vector that determines the position
										// of the camera
	Vector displayCorner = new Vector();
	Vector realMousePos;
	
	int mapSize = 256; // width/ height of the map (in tiles)
	int mapSeed = 420420; // seed used to generate the map
	int tileSize = 25; // size of each tile when rendering
	int renderRadius = 1500; // radius around player where objects will render
	int cameraTileX = 0; // Tile that the camera is positioned in
	int cameraTileY = 0;
	int cameraMaxMovementSpeed = 20; // Maximum Speed that the camera will move
	int borderWidth = 150; // border of screen where the mouse can move the
							// camera

	int highlightedTileX = -1; // The tile that the mouse is hovering over
	int highlightedTileY = -1;

	int selectedTileX = -1; // The tile that the mouse is hovering over
	int selectedTileY = -1;

	int hudX = 10; // X- coordinate of the HUD (set when ticking the camera)
	int hudY = 10; // Y- coordinate of the HUD (set when ticking the camera)

	int scaledScreenWidth; // Width of box shown on minimap (set when ticking
							// the camera)
	int scaledScreenHeight; // height of box shown on minimap (set when ticking
							// the camera)
	int minimapScale = 1; // Factor used to scale up/down minimap size

	int HDScale = 5; // Facor used to increase quality of map

	int leftBorder;
	int rightBorder;
	int topBorder;
	int bottomBorder;

	Color unitColour = new Color(255,255,255);
	Color buildingColour = new Color(255,255,255);
	
	public List<Unit> localPlayerUnits = new ArrayList<Unit>();
	public List<Integer> selectedUnits = new ArrayList<Integer>();
	
	Vector previouseMousePosition = new Vector();
	boolean previouseClicked;
	BoundaryBox selectedRectangle = new BoundaryBox();
	
	Vector selectedPoint = new Vector();
	Vector selectedSize = new Vector();
	
	private Button button1 = new Button(10, 220 + (minimapScale * mapSize), 50, 50, "", new Color(255, 255, 255),
			new Color(100, 100, 100));
	private Button button2 = new Button(70, 220 + (minimapScale * mapSize), 50, 50, "", new Color(255, 255, 255),
			new Color(100, 100, 100));
	private Button button3 = new Button(130, 220 + (minimapScale * mapSize), 50, 50, "", new Color(255, 255, 255),
			new Color(100, 100, 100));

	private Button button4 = new Button(10, 280 + (minimapScale * mapSize), 50, 50, "", new Color(255, 255, 255),
			new Color(100, 100, 100));
	private Button button5 = new Button(70, 280 + (minimapScale * mapSize), 50, 50, "", new Color(255, 255, 255),
			new Color(100, 100, 100));
	private Button button6 = new Button(130, 280 + (minimapScale * mapSize), 50, 50, "", new Color(255, 255, 255),
			new Color(100, 100, 100));

	private Button button7 = new Button(10, 340 + (minimapScale * mapSize), 50, 50, "", new Color(255, 255, 255),
			new Color(100, 100, 100));
	private Button button8 = new Button(70, 340 + (minimapScale * mapSize), 50, 50, "", new Color(255, 255, 255),
			new Color(100, 100, 100));
	private Button button9 = new Button(130, 340 + (minimapScale * mapSize), 50, 50, "", new Color(255, 255, 255),
			new Color(100, 100, 100));

	private Building mouseBuilding = null;
	
	public InGameManager() {
		
		noise = new PerlinNoise(mapSeed); // initialises the Perlin Noise Used
		tiles = newMap(mapSize, mapSize); // generates the map

		mouseBuilding = new Base();

		mapImage = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_ARGB); // creates
																						// the
																						// map
																						// image
		for (int x = 0; x < tiles.length; x++) { // Loops through every pixel of
													// the image
			for (int y = 0; y < tiles[0].length; y++) {
				Color mapColour = new Color(255, 255, 255); // Colour is
															// initially set to
															// White (should
															// never be seen)

				if (tiles[x][y].getType() == TileTypes.GROUND) { // Determines
																	// the
																	// Colour of
																	// the
																	// corrosponding
																	// pixel on
																	// the image
					mapColour = new Color(0, 255, 0);
				} else if (tiles[x][y].getType() == TileTypes.WATER) {
					mapColour = new Color(0, 0, 255);
				} else if (tiles[x][y].getType() == TileTypes.SAND) {
					mapColour = new Color(255, 255, 100);
				} else if (tiles[x][y].getType() == TileTypes.ROCKS) {
					mapColour = new Color(100, 100, 100);
				}
				mapImage.setRGB(x, y, mapColour.getRGB()); // Sets the Colour of
															// each pixel
			}
		}

		HDMapImage = new BufferedImage(mapSize * HDScale, mapSize * HDScale, BufferedImage.TYPE_INT_ARGB); // creates
																											// the
																											// map
																											// image
		for (int i = 0; i < tiles.length; i++) { // Loops through every pixel of
													// the image
			for (int j = 0; j < tiles[0].length; j++) {
				for (int xx = 0; xx < HDScale; xx++) {
					for (int yy = 0; yy < HDScale; yy++) {

						int x = (i * HDScale) + xx;
						int y = (j * HDScale) + yy;

						Color mapColour = new Color(255, 255, 255); // Colour is
																	// initially
																	// set to
																	// White
																	// (should
																	// never be
																	// seen)

						int shade = (int) (255 * (HDHeightMap[x][y] * 1.4)); // Determines
																				// the
																				// shade
																				// of
																				// grass
						if (shade > 200) {
							shade = 200;
						} else if (shade < 50) {
							shade = 50;
						}
						shade = 255 - shade;

						int waterShade = (int) (255 * Math.abs(HDHeightMap[x][y])); // Determines
																					// the
																					// shade
																					// of
																					// water
						if (waterShade > 255) {
							waterShade = 255;
						} else if (waterShade < 50) {
							waterShade = 50;
						}
						waterShade = 255 - waterShade;

						int rockShade = (int) (255 * (HDHeightMap[x][y])); // Determines
																			// the
																			// shade
																			// of
																			// Rocks
						if (rockShade > 250) {
							rockShade = 250;
						} else if (rockShade < 50) {
							rockShade = 50;
						}

						if (tiles[i][j].getType() == TileTypes.GROUND) { // Determines
																			// the
																			// Colour
																			// of
																			// the
																			// corrosponding
																			// pixel
																			// on
																			// the
																			// image
							mapColour = new Color(0, shade, 0);
						} else if (tiles[i][j].getType() == TileTypes.WATER) {
							mapColour = new Color(0, 0, waterShade);
						} else if (tiles[i][j].getType() == TileTypes.SAND) {
							mapColour = new Color(255, 255, 100);
						} else if (tiles[i][j].getType() == TileTypes.ROCKS) {
							mapColour = new Color(rockShade, rockShade, rockShade);
						}

						HDMapImage.setRGB(x, y, mapColour.getRGB()); // Sets the
																		// Colour
																		// of
																		// each
																		// pixel
					}
				}

			}
		}
		
		selectedRectangle.setup(0, 0, 0, 0);
	}

	private Tile[][] newMap(int w, int h) {
		Tile[][] tiles = new Tile[w][h];
		float[][] floats = noise.generateNoiseMap(w, h, 0.05f, 3, 0.8f, 1); // These
																			// Values
																			// may
																			// require
																			// some
																			// tweaking
		HDHeightMap = noise.generateNoiseMap(w * HDScale, h * HDScale, 0.05f / HDScale, 3, 0.8f, 1); // These
																										// Values
																										// may
																										// require
																										// some
																										// tweaking

		for (int x = 0; x < tiles.length; x++) { // Loops through each tile
			for (int y = 0; y < tiles[0].length; y++) {
				if (floats[x][y] <= -0.4) { // Perlin noise generates a height
											// map. Height values are checked to
											// determine tile type
					tiles[x][y] = new Tile(TileTypes.WATER);
				} else if (floats[x][y] <= -0.3) {
					tiles[x][y] = new Tile(TileTypes.SAND);
				} else if (floats[x][y] <= 0.4) {
					tiles[x][y] = new Tile(TileTypes.GROUND);
				} else {
					tiles[x][y] = new Tile(TileTypes.ROCKS);
				}
			}
		}

//		heightMap = floats;
		return tiles;
	}

	public void tick() {
		tickCamera(); // Performs all Camera Calculations
		tickTiles();
		tickUnits();
	}

	private void tickUnits() {
		
		if(selectedUnits.size() >= 1){
			mouseBuilding = null;
		}
		
		for(int i = 0; i < localPlayerUnits.size(); i ++){
			localPlayerUnits.get(i).move();
			
			if(MouseInput.leftClick()){
				
				if(localPlayerUnits.get(i).hitCheck(realMousePos)){
					
					if(!Keyinput.getkey(16) && !Keyinput.getkey(17)){
						selectedUnits.clear();
					}
					
					selectedUnits.add(i);
					
					selectedTileX = -1;
					selectedTileY = -1;
					
					sanitiseSelectedUnits();
				}
			}
		}
		
		if(mouseInsideWindow()){
//			selectedRectangle.setup(0, 0, 0, 0);
			
//			creatingRectangle = false;
			
			int xpos = 0;
			int ypos = 0;
			int width = 0;
			int height = 0;
			
			if(previouseClicked && MouseInput.leftClick()){
	//			System.out.println("held");
				int x = (int) (realMousePos.getX()-selectedPoint.getX());
				int y = (int) (realMousePos.getY()-selectedPoint.getY());
				selectedSize = new Vector(x, y);
				
				if(selectedSize.getX() < 0){
					xpos = (int) (selectedPoint.getX()+selectedSize.getX());
				}else{
					xpos = (int) (selectedPoint.getX());
				}
				if(selectedSize.getY() < 0){
					ypos = (int) (selectedPoint.getY()+selectedSize.getY());
				}else{
					ypos = (int) (selectedPoint.getY());
				}
				
				width = (int) Math.abs(selectedSize.getX());
				height = (int) Math.abs(selectedSize.getY());
				
				selectedRectangle.setup(xpos, ypos, width, height);
			}
			if(!previouseClicked && MouseInput.leftClick()){
	//			System.out.println("Clicked");
				previouseMousePosition = realMousePos;
				selectedPoint = new Vector((int) realMousePos.getX(),(int) realMousePos.getY());
			}
			
			if(previouseMousePosition.getX() != realMousePos.getX() || previouseMousePosition.getY() != realMousePos.getY()){
				if(previouseClicked && !MouseInput.leftClick()){
					System.out.println("bu");
					// System.out.println("Released");
					if(!Keyinput.getkey(16) && !Keyinput.getkey(17)){
						selectedUnits.clear();
					}
					
					for(int i = 0; i < localPlayerUnits.size(); i++){
						if(selectedRectangle.hitCheck(localPlayerUnits.get(i).getPosition())){
							
							selectedTileX = -1;
							selectedTileY = -1;
							
							selectedUnits.add(i);
							
							sanitiseSelectedUnits();
						}
					}
					
					selectedRectangle.setup(0, 0, 0, 0);
				}
			}
			
			previouseClicked = MouseInput.leftClick();
		}
	}

	private void tickTiles() {
		if (MouseInput.leftClick() && mouseInsideWindow()) {
			if (mouseBuilding != null) {
				if (highlightedTileX >= 0 && highlightedTileX < tiles.length) {
					if (highlightedTileY >= 0 && highlightedTileY < tiles[0].length) {
						tiles[highlightedTileX][highlightedTileY].build(mouseBuilding, new Vector((highlightedTileX*tileSize)+(tileSize/2), (highlightedTileY*tileSize)+(tileSize/2)));

						// remove required materials here

						mouseBuilding = null;
					}
				}
			} else {
				if(tiles[highlightedTileX][highlightedTileY].getContainsBuilding()){
					if(previouseMousePosition.getX() == realMousePos.getX() && previouseMousePosition.getY() == realMousePos.getY()){
						selectedTileX = highlightedTileX;
						selectedTileY = highlightedTileY;
						
						selectedUnits.clear();
					}
				}
			}
		}
		
		if(MouseInput.rightClick() && mouseInsideWindow()){
			for(int i = 0; i < selectedUnits.size(); i ++){
				localPlayerUnits.get(selectedUnits.get(i)).setDestination(realMousePos);
			}
		}

		if (Keyinput.getkey(27)) { // ESC
			mouseBuilding = null;
			selectedTileX = -1;
			selectedTileY = -1;
			
			selectedUnits.clear();
		}

		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[0].length; y++) {
				tiles[x][y].tickBuilding();
			}
		}
		
//		if(selectedUnits.size() >= 1){
//			if(selectedUnitsSame()){
//				
//			}
//		}
		//int a;
		
		if(selectedTileX >= 0 && selectedTileX < tiles.length && selectedTileY >= 0 && selectedTileY < tiles[0].length){
			if(tiles[selectedTileX][selectedTileY].getContainsBuilding() && tiles[selectedTileX][selectedTileY].getBuilding() != null){
				if(!tiles[selectedTileX][selectedTileY].getCurrentlyBuilding()){
					if(button1.Clicked()){
						tiles[selectedTileX][selectedTileY].getBuilding().action1();
					}
					if(button2.Clicked()){
						tiles[selectedTileX][selectedTileY].getBuilding().action2();
					}
					if(button3.Clicked()){
						tiles[selectedTileX][selectedTileY].getBuilding().action3();
					}
					if(button4.Clicked()){
						tiles[selectedTileX][selectedTileY].getBuilding().action4();
					}
					if(button5.Clicked()){
						tiles[selectedTileX][selectedTileY].getBuilding().action5();
					}
					if(button6.Clicked()){
						tiles[selectedTileX][selectedTileY].getBuilding().action6();
					}
					if(button7.Clicked()){
						tiles[selectedTileX][selectedTileY].getBuilding().action7();
					}
					if(button8.Clicked()){
						tiles[selectedTileX][selectedTileY].getBuilding().action8();
					}
					if(button9.Clicked()){
						tiles[selectedTileX][selectedTileY].getBuilding().action9();
					}
				}
			}
		}
	}

	private boolean mouseInsideWindow() {
		return (MouseInput.getMouseX() >= leftBorder && MouseInput.getMouseX() <= rightBorder
				&& MouseInput.getMouseY() >= topBorder && MouseInput.getMouseY() <= bottomBorder);
	}

	public void tickCamera() {

		leftBorder = mapSize * minimapScale + 20;
		rightBorder = GraphicsEngine.getWidth() - 10;
		topBorder = 10;
		bottomBorder = GraphicsEngine.getHeight() - 10;

		displayCorner = new Vector(leftBorder, topBorder); // Sets to the Center
															// of the screen
															// (for rendering
															// Purposes)
		cameraTileX = (int) Math.floor(cameraOffset.getX() / tileSize); // Sets
																		// the
																		// current
																		// tile
																		// of
																		// the
																		// Camera
		cameraTileY = (int) Math.floor(cameraOffset.getY() / tileSize);

		scaledScreenWidth = (int) (minimapScale * (rightBorder - leftBorder) / tileSize); // sets
																							// the
																							// size
																							// of
																							// the
																							// screen
																							// shown
																							// on
																							// the
																							// minimap
		scaledScreenHeight = (int) (minimapScale * (bottomBorder - topBorder) / tileSize);

		realMousePos = new Vector(cameraOffset.getX()-displayCorner.getX()+MouseInput.getMouseX(), cameraOffset.getY()-displayCorner.getY()+MouseInput.getMouseY());
		
		// Checking Arrow keys to move Camera
		if (Keyinput.getkey(38)) { // UP
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() - cameraMaxMovementSpeed); // Moves
																											// Camera
																											// UP
		}
		if (Keyinput.getkey(40)) { // DOWN
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() + cameraMaxMovementSpeed); // Moves
																											// Camera
																											// Down
		}

		if (Keyinput.getkey(37)) { // LEFT
			cameraOffset = new Vector(cameraOffset.getX() - cameraMaxMovementSpeed, cameraOffset.getY()); // Moves
																											// Camera
																											// Left
		}
		if (Keyinput.getkey(39)) { // RIGHT
			cameraOffset = new Vector(cameraOffset.getX() + cameraMaxMovementSpeed, cameraOffset.getY()); // Moves
																											// Camera
																											// Right
		}

		// Ensuring that the Camera stays on the map
//		lockCamera();

		if (MouseInput.getMouseX() >= hudX && MouseInput.getMouseX() <= hudX + mapSize * minimapScale) { // When
																											// the
																											// mouseX
																											// is
																											// within
																											// the
																											// minimap
			if (MouseInput.getMouseY() >= hudY && MouseInput.getMouseY() <= hudY + mapSize * minimapScale) { // When
																												// the
																												// mouseY
																												// is
																												// within
																												// the
																												// minimap
				if (MouseInput.leftClick()) {
					cameraOffset = new Vector(
							(tileSize * (MouseInput.getMouseX() / minimapScale) - hudX)
									- ((rightBorder - leftBorder) / 2),
							(((tileSize * MouseInput.getMouseY()) / minimapScale) - hudY)
									- ((bottomBorder - topBorder) / 2));
				}
			}
		}

		if (mouseInsideWindow()) {
			highlightedTileX = (int) Math
					.floor((MouseInput.getMouseX() - displayCorner.getX() + cameraOffset.getX()) / tileSize);
			highlightedTileY = (int) Math
					.floor((MouseInput.getMouseY() - displayCorner.getY() + cameraOffset.getY()) / tileSize);
		} else {
			highlightedTileX = -1;
			highlightedTileY = -1;
		}

		// Checking Mouse Position to move Camera
		if (mouseInsideWindow()) {
			int deltaX = rightBorder - MouseInput.getMouseX(); // Distance from
																// mouse to
																// right side of
																// the screen
			int deltaY = bottomBorder - MouseInput.getMouseY(); // Distance from
																// the mouse to
																// bottom of the
																// screen

			if (MouseInput.getMouseY() <= topBorder + borderWidth) { // UP
				int speed = cameraMaxMovementSpeed
						- (((MouseInput.getMouseY() - topBorder) * cameraMaxMovementSpeed) / borderWidth);
				cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() - speed); // Moves
																								// Camera																			// UP
			}
			if (deltaY <= borderWidth) { // DOWN
				int speed = cameraMaxMovementSpeed - ((deltaY * cameraMaxMovementSpeed) / borderWidth);
				cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() + speed); // Moves
																								// Camera
																								// Down
			}

			if (MouseInput.getMouseX() <= leftBorder + borderWidth) { // LEFT
				int speed = cameraMaxMovementSpeed
						- (((MouseInput.getMouseX() - leftBorder) * cameraMaxMovementSpeed) / borderWidth);
				cameraOffset = new Vector(cameraOffset.getX() - speed, cameraOffset.getY()); // Moves
																								// Camera
																								// LEFT
			}
			if (deltaX <= borderWidth) { // RIGHT
				int speed = cameraMaxMovementSpeed - ((deltaX * cameraMaxMovementSpeed) / borderWidth);
				cameraOffset = new Vector(cameraOffset.getX() + speed, cameraOffset.getY()); // Moves
																								// Camera
																								// RIGHT
			}
		}
		lockCamera();
	}

	private void lockCamera() {
		while (cameraOffset.getX() - (leftBorder / tileSize) < 0) {
			cameraOffset = new Vector(cameraOffset.getX() + 1, cameraOffset.getY());
		}
		while (cameraOffset.getX() + (rightBorder - leftBorder) > (tiles.length * tileSize)) {
			cameraOffset = new Vector(cameraOffset.getX() - 1, cameraOffset.getY());
		}
		while (cameraOffset.getY() - (topBorder / tileSize) < 0) {
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() + 1);
		}
		while (cameraOffset.getY() + (bottomBorder - topBorder) > (tiles[0].length * tileSize)) {
			cameraOffset = new Vector(cameraOffset.getX(), cameraOffset.getY() - 1);
		}
	}

	public void render() {
		renderBackgroundImgage();
		renderUnits();
		renderBuildings();
		
		renderHUD();
	}

	private void renderUnits() {
		for(int i = 0; i < localPlayerUnits.size(); i ++){
			if(Vector.squreDistance(localPlayerUnits.get(i).getPosition(), cameraOffset) <= renderRadius*renderRadius){
				int XPos = (int) (localPlayerUnits.get(i).getPosition().getX());
				XPos -= cameraOffset.getX()-displayCorner.getX();
				
				int YPos = (int) (localPlayerUnits.get(i).getPosition().getY());
				YPos -= cameraOffset.getY()-displayCorner.getY();
				
				if(selectedUnit(i)){
					GraphicsEngine.setColor(new Color(100,100,100, 150));
					GraphicsEngine.ellipse(XPos-10, YPos-10, 20);
				}
				
				GraphicsEngine.setColor(new Color(255,255,0));
				GraphicsEngine.ellipse(XPos-5, YPos-5, 10);
			}
		}
	}

	private boolean selectedUnit(int index) {
		for(int i = 0; i < selectedUnits.size(); i ++){
			if(selectedUnits.get(i) == index){
				return true;
			}
		}
		
		return false;
	}

	private void renderBuildings() {
		int tileRadius = renderRadius / tileSize; // Radius of Tiles that will
													// be rendered
		for (int xx = -tileRadius; xx < tileRadius; xx++) { // Loops through all
															// tiles on the
															// screen that are
															// within the Radius
			for (int yy = -tileRadius; yy < tileRadius; yy++) {
				int x = xx + cameraTileX;
				int y = yy + cameraTileY;
				if (x >= 0 && x < tiles.length) { // Checks for null-pointer
													// errors (trying the draw
													// tiles that don't exist)
					if (y >= 0 && y < tiles[0].length) {
						if (tiles[x][y].getContainsBuilding()) {
							int xxx = (int) ((x * tileSize) - cameraOffset.getX() + displayCorner.getX());
							int yyy = (int) ((y * tileSize) - cameraOffset.getY() + displayCorner.getY());
							GraphicsEngine.setColor(new Color(0, 0, 0));
							GraphicsEngine.rect(xxx, yyy - 5, tileSize, 5);

							GraphicsEngine.setColor(new Color(255, 0, 0));
							GraphicsEngine.rect(xxx, yyy - 5, (tileSize * tiles[x][y].getBuilding().getHealth())
									/ tiles[x][y].getBuilding().getMaxHealth(), 5);

							if (tiles[x][y].getCurrentlyBuilding()) {
								GraphicsEngine.setColor(new Color(0, 0, 0));
								GraphicsEngine.rect(xxx, yyy + (tileSize), tileSize, 5);

								GraphicsEngine.setColor(new Color(255, 255, 0));
								GraphicsEngine.rect(xxx, yyy + (tileSize),
										(tileSize * tiles[x][y].getBuildTime()) / tiles[x][y].getMaxBuildTime(), 5);
							}

							GraphicsEngine.renderImage(tiles[x][y].getBuilding().getSprite(),
									(int) ((x * tileSize) - cameraOffset.getX() + displayCorner.getX()),
									(int) ((y * tileSize) - cameraOffset.getY() + displayCorner.getY()), tileSize,
									tileSize);

						}
					}
				}
			}
		}

		if (mouseBuilding != null) {
			GraphicsEngine.renderImage(mouseBuilding.getSprite(), MouseInput.getMouseX() - (tileSize / 2),
					MouseInput.getMouseY() - (tileSize / 2), tileSize, tileSize);
		}
	}

	private void renderBackgroundImgage() { // renders the background as an
											// image
		GraphicsEngine.renderImage(HDMapImage, (int) (-cameraOffset.getX() + displayCorner.getX()),
				(int) (-cameraOffset.getY() + displayCorner.getY()), mapSize * tileSize, mapSize * tileSize);

		if (mouseBuilding != null) {
			if (highlightedTileX >= 0 && highlightedTileX < tiles.length) {
				if (highlightedTileY >= 0 && highlightedTileY < tiles[0].length) {
					if (tiles[highlightedTileX][highlightedTileY].getCanBuild()) {
						GraphicsEngine.setColor(new Color(0, 255, 0, 100));
					} else {
						GraphicsEngine.setColor(new Color(255, 0, 0, 100));
					}
					GraphicsEngine.rect(
							(int) ((highlightedTileX * tileSize) - cameraOffset.getX() + displayCorner.getX()),
							(int) ((highlightedTileY * tileSize) - cameraOffset.getY() + displayCorner.getY()),
							tileSize, tileSize); // Draws the Tiles (tileSize+1
													// is used to remove borders
													// around tiles)

					GraphicsEngine.setColor(new Color(255, 255, 255));
					GraphicsEngine.outLineRect(
							(int) ((highlightedTileX * tileSize) - cameraOffset.getX() + displayCorner.getX()),
							(int) ((highlightedTileY * tileSize) - cameraOffset.getY() + displayCorner.getY()),
							tileSize, tileSize); // Draws the Tiles (tileSize+1
													// is used to remove borders
													// around tiles)

					GraphicsEngine.setColor(new Color(0, 0, 0));
					GraphicsEngine.outLineRect(
							(int) ((highlightedTileX * tileSize) - cameraOffset.getX() + displayCorner.getX()) - 1,
							(int) ((highlightedTileY * tileSize) - cameraOffset.getY() + displayCorner.getY()) - 1,
							tileSize + 2, tileSize + 2); // Draws the Tiles
															// (tileSize+1 is
															// used to remove
															// borders around
															// tiles)

				}
			}
		}

		if (selectedTileX >= 0 && selectedTileX < tiles.length) {
			if (selectedTileY >= 0 && selectedTileY < tiles[0].length) {
				GraphicsEngine.setColor(new Color(255, 255, 255));
				GraphicsEngine.outLineRect(
						(int) ((selectedTileX * tileSize) - cameraOffset.getX() + displayCorner.getX()),
						(int) ((selectedTileY * tileSize) - cameraOffset.getY() + displayCorner.getY()), tileSize,
						tileSize); // Draws the Tiles (tileSize+1 is used to
									// remove borders around tiles)

				GraphicsEngine.setColor(new Color(0, 0, 0));
				GraphicsEngine.outLineRect(
						(int) ((selectedTileX * tileSize) - cameraOffset.getX() + displayCorner.getX()) - 1,
						(int) ((selectedTileY * tileSize) - cameraOffset.getY() + displayCorner.getY()) - 1,
						tileSize + 2, tileSize + 2); // Draws the Tiles
														// (tileSize+1 is used
														// to remove borders
														// around tiles)

			}
		}
	}

	public void renderHUD() {
		// Drawing HUD

		GraphicsEngine.setColor(new Color(100, 100, 100));
		// GraphicsEngine.rect(10, 10, ( mapSize*minimapScale),
		// GraphicsEngine.getHeight()-(30+( mapSize*minimapScale)));

		GraphicsEngine.rect(0, 0, GraphicsEngine.getWidth(), topBorder);

		GraphicsEngine.rect(0, 0, leftBorder, GraphicsEngine.getHeight());
		//
		GraphicsEngine.rect(rightBorder, 0, GraphicsEngine.getWidth() - rightBorder, GraphicsEngine.getHeight());
		//
		GraphicsEngine.rect(0, bottomBorder, GraphicsEngine.getWidth(), GraphicsEngine.getHeight() - bottomBorder);

		GraphicsEngine.setColor(new Color(0, 0, 0));
		GraphicsEngine.outLineRect(leftBorder, topBorder, rightBorder - leftBorder, bottomBorder - topBorder);
		GraphicsEngine.setColor(new Color(255, 255, 255));
		GraphicsEngine.outLineRect(leftBorder + 1, topBorder + 1, rightBorder - leftBorder - 2,
				bottomBorder - topBorder - 2);

		GraphicsEngine.renderImage(mapImage, hudX, hudY, mapSize * minimapScale, mapSize * minimapScale); // Draws
		
		for(int i = 0; i < localPlayerUnits.size(); i ++){
			int x = (int) Math.floor(((localPlayerUnits.get(i).getPosition().getX()/tileSize)*minimapScale));
			int y = (int) Math.floor(((localPlayerUnits.get(i).getPosition().getY()/tileSize)*minimapScale));
							
			GraphicsEngine.setColor(unitColour);
			GraphicsEngine.ellipse(x-(minimapScale*2)+hudX, y-(minimapScale*2)+hudY, minimapScale*4);
		}
		
		for(int x = 0; x < tiles.length; x ++){
			for(int y = 0; y < tiles[0].length; y ++){
				if(tiles[x][y].getContainsBuilding()){
					GraphicsEngine.setColor(buildingColour);
					GraphicsEngine.ellipse((x*minimapScale)+hudX-(minimapScale*2), (y*minimapScale)+hudY-(minimapScale*2), minimapScale*4);

				}
			}
		}

		GraphicsEngine.setColor(new Color(255, 255, 255));
		GraphicsEngine.outLineRect(hudX + (cameraTileX * minimapScale), hudY + (cameraTileY * minimapScale),
				scaledScreenWidth, scaledScreenHeight); // Draws the Screen
														// Representation on the
														// Minimap

		GraphicsEngine.setColor(new Color(0, 0, 0));
		GraphicsEngine.outLineRect(hudX, hudY, mapSize * minimapScale, mapSize * minimapScale); // Draws
																								// a
																								// black
																								// outline
																								// around
																								// the
																								// minimap

		if (selectedTileX >= 0 && selectedTileX < tiles.length) {
			if (selectedTileY >= 0 && selectedTileY < tiles[0].length) {
				if (tiles[selectedTileX][selectedTileY].getContainsBuilding()) {
					GraphicsEngine.setColor(new Color(255, 255, 255));
					GraphicsEngine.textSize(20);
					GraphicsEngine.text(tiles[selectedTileX][selectedTileY].getBuilding().getName(), 10,
							40 + (minimapScale * mapSize));

					GraphicsEngine.setColor(new Color(255, 255, 255));
					GraphicsEngine.textSize(20);
					GraphicsEngine.text(
							"Health " + tiles[selectedTileX][selectedTileY].getBuilding().getHealth() + "/"
									+ tiles[selectedTileX][selectedTileY].getBuilding().getMaxHealth(),
							10, 40 + (minimapScale * mapSize) + (30 * 1));

					GraphicsEngine.setColor(new Color(255, 255, 255));
					GraphicsEngine.textSize(20);
					GraphicsEngine.text(
							"Built " + ((100 * tiles[selectedTileX][selectedTileY].getBuildTime())
									/ tiles[selectedTileX][selectedTileY].getMaxBuildTime()) + "%",
							10, 40 + (minimapScale * mapSize) + (30 * 2));

				} else {
					GraphicsEngine.setColor(new Color(255, 255, 255));
					GraphicsEngine.textSize(20);
					GraphicsEngine.text(tiles[selectedTileX][selectedTileY].getType().getName(), 10,
							40 + (minimapScale * mapSize));

					GraphicsEngine.setColor(new Color(255, 255, 255));
					GraphicsEngine.textSize(20);
					GraphicsEngine.text("Buildable ::" + tiles[selectedTileX][selectedTileY].getCanBuild(), 10,
							40 + (minimapScale * mapSize) + (30 * 1));
				}

				if(tiles[selectedTileX][selectedTileY].getContainsBuilding() && tiles[selectedTileX][selectedTileY].getBuilding() != null){
					if(!tiles[selectedTileX][selectedTileY].getCurrentlyBuilding()){
						button1.render();
						button2.render();
						button3.render();
						button4.render();
						button5.render();
						button6.render();
						button7.render();
						button8.render();
						button9.render();
					}
				}
				
				if(selectedUnits.size() >= 1){
					button1.render();
					button2.render();
					button3.render();
					button4.render();
					button5.render();
					button6.render();
					button7.render();
					button8.render();
					button9.render();
				}
			}
		}
		
		if (selectedUnits.size() >= 1) {
			GraphicsEngine.setColor(new Color(255, 255, 255));
			GraphicsEngine.textSize(20);
			GraphicsEngine.text(selectedUnits.size()+" Units Selected", 10, 40 + (minimapScale * mapSize));
			
			if(selectedUnits.size() == 1){
				GraphicsEngine.setColor(new Color(255, 255, 255));
				GraphicsEngine.textSize(20);
				GraphicsEngine.text("Health: " + localPlayerUnits.get(selectedUnits.get(0)).getHealth() + "/"	+ localPlayerUnits.get(selectedUnits.get(0)).getMaxHealth(), 10, 40 + (minimapScale * mapSize) + (30 * 1));

			}
		}
		GraphicsEngine.setColor(new Color(0,255,0, 100));
		GraphicsEngine.rect((int) (selectedRectangle.rect.x-cameraOffset.getX()+displayCorner.getX()),(int) (selectedRectangle.rect.y-cameraOffset.getY()+displayCorner.getY()), selectedRectangle.rect.width,(int) selectedRectangle.rect.height);

		GraphicsEngine.setColor(new Color(0,0,0));
		GraphicsEngine.outLineRect((int) (selectedRectangle.rect.x-cameraOffset.getX()+displayCorner.getX()),(int) (selectedRectangle.rect.y-cameraOffset.getY()+displayCorner.getY()), selectedRectangle.rect.width,(int) selectedRectangle.rect.height);

		for(int i = 0; i < localPlayerUnits.size(); i ++){
			if(localPlayerUnits.get(i).hitCheck(realMousePos)){
				GraphicsEngine.setColor(new Color(100,100,100,100));
				GraphicsEngine.rect(MouseInput.getMouseX(), MouseInput.getMouseY(), 170, 60);
				
				GraphicsEngine.setColor(new Color(255,255,255));
				GraphicsEngine.textSize(20);
				GraphicsEngine.text(localPlayerUnits.get(i).getName(), MouseInput.getMouseX()+10, MouseInput.getMouseY()+30);
				GraphicsEngine.setColor(new Color(0,0,0));
				GraphicsEngine.rect(MouseInput.getMouseX()+10, MouseInput.getMouseY()+40, 150, 10);
				GraphicsEngine.setColor(new Color(255,0,0));
				GraphicsEngine.rect(MouseInput.getMouseX()+10, MouseInput.getMouseY()+40, (150*localPlayerUnits.get(i).getHealth())/localPlayerUnits.get(i).getMaxHealth(), 10);
			}
		}
	}

	public void setMouseBuilding(Building _building) {
		mouseBuilding = _building;
		
	}
	
	public void sanitiseSelectedUnits(){
		
		for(int i = 0; i < selectedUnits.size(); i++){
			for(int j = 0; j < selectedUnits.size(); j ++){
				if(j != i){
					if(selectedUnits.get(i) == selectedUnits.get(j)){
						selectedUnits.remove(i);
						i --;
					}
				}
			}
		}
	}
	
	public boolean selectedUnitsSame(){
		UnitTypes type = localPlayerUnits.get(selectedUnits.get(0)).getType();
		boolean b = true;
		
		for(int i = 0; i < selectedUnits.size(); i ++){
			if(localPlayerUnits.get(selectedUnits.get(i)).getType() != type){
				b = false;
			}
		}
		
		return b;
	}
}
