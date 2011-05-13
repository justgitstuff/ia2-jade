package sma.pathFinding;

import sma.ontology.InfoGame;



/**
 * The data map from our example game. This holds the state and context of each tile
 * on the map. It also implements the interface required by the path finder. It's implementation
 * of the path finder related methods add specific handling for the types of units
 * and terrain in the example game.
 * 
 */
public class GameMap {
	private sma.ontology.InfoGame game;
	
	/** The map width in tiles */
	//public static final int WIDTH = 30;
	/** The map height in tiles */
	//public static final int HEIGHT = 30;
	
	// MEDIDAS DE NUESTRO SISTEMA VERDADERO
	public int WIDTH;
	public int HEIGHT;
	
	
	
	/*
	 * 1 Building
	 * 2 Street
	 * 3 RECYLCING_CENTER
	 * */
	
	public static final int BUILDING = 1;
	public static final int STREET = 2;
	public static final int RECYCLING_CENTER = 3;
	public static final int GARBAGE = 6;
	
	public static final int UNDISCOVERED = 10;
	/*
	 * Scout = 0
	 * Harverst = 1
	 */
	
	public static final int SCOUT = 4;
	public static final int HARVESTER = 5;
	
	/** The terrain settings for each tile in the map */
	private int[][] terrain ;
	/** The unit in each tile of the map */
	private int[][] units ;
	/** Indicator if a given tile has been visited during the search */
	private boolean[][] visited;
	
	/**
	 * Create a new test map with some default configuration
	 */
	public GameMap( InfoGame infoGame) {
		
		this.game=infoGame;
		
			
		WIDTH=this.game.getMap()[0].length;
		HEIGHT=this.game.getMap().length;
		
		terrain = new int[WIDTH][HEIGHT];
	    units = new int[WIDTH][HEIGHT];
		visited = new boolean[WIDTH][HEIGHT];
		
		
		
	//	System.out.println("long mappppp" + this.game.getMap().length);
		
		for(int c=0;c<WIDTH;c++){
			for(int r=0;r<HEIGHT;r++){
				
				if (game.getCell(r, c)!=null){
					//System.out.println(this.game.getCell(x, y).getCellType());
					//System.out.println(game.getCell(r, c).isThereAnAgent());
					//private char garbageType = '-'; //G=Glass, P=Plastic, M=Metal, P=Paper
					
					terrain[c][r]=game.getCell(r, c).getCellType();
				
					
					// NO SURTEN ELS AGENTS RECONEGUTS
					if(this.game.getCell(r,c).isThereAnAgent()==true){
						//System.out.println("AGENT TROBATTTT");
						if(this.game.getCell(r,c).getAgent().getAgentType()==0){
							units[c][r] = SCOUT;
							//System.out.println("SCOUTTTT");
						}else{// SERA 1 corresponent HARVESTER
							units[c][r] = HARVESTER;
							//System.out.println("HARVESTER");
						}	
					}
					
				}else{
					
					terrain[c][r]=UNDISCOVERED;
					
				}
				
								
			}
		}
		
		//printMap();
	
	}

	
	/**
	 * Clear the array marking which tiles have been visted by the path 
	 * finder.
	 */
	
	public void clearVisited() {
		for (int x=0;x<getWidthInTiles();x++) {
			for (int y=0;y<getHeightInTiles();y++) {
				visited[x][y] = false;
			}
		}
	}
	
	/**
	 * @see TileBasedMap#visited(int, int)
	 */
	public boolean visited(int x, int y) {
		return visited[x][y];
	}
	
	/**
	 * Get the terrain at a given location
	 * 
	 * @param x The x coordinate of the terrain tile to retrieve
	 * @param y The y coordinate of the terrain tile to retrieve
	 * @return The terrain tile at the given location
	 */
	public int getTerrain(int x, int y) {
		return terrain[x][y];
	}
	
	public void setTerrain(int x, int y) {
		//System.out.print("Posicioooooo Origen Destiiiiiiiiiii "+terrain[x][y]+ " " + x+ " " + y);
		 terrain[x][y]=STREET;
		// System.out.print("Posicioooooo Destiiiiiiiiiii "+terrain[x][y] +" " + x+ " " + y);
	}
	
	
	/**
	 * Get the unit at a given location
	 * 
	 * @param x The x coordinate of the tile to check for a unit
	 * @param y The y coordinate of the tile to check for a unit
	 * @return The ID of the unit at the given location or 0 if there is no unit 
	 */
	public int getUnit(int x, int y) throws java.lang.ArrayIndexOutOfBoundsException {
		try{
			return units[x][y];
		}catch(java.lang.ArrayIndexOutOfBoundsException e)
		{
			System.err.printf("Index out of bounds "+x+" "+y);
			return 0;
		}
	}
	
	/**
	 * Set the unit at the given location
	 * 
	 * @param x The x coordinate of the location where the unit should be set
	 * @param y The y coordinate of the location where the unit should be set
	 * @param unit The ID of the unit to be placed on the map, or 0 to clear the unit at the
	 * given location
	 */
	public void setUnit(int x, int y, int unit) {
		units[x][y] = unit;
	}
	
	/**
	 * @see TileBasedMap#blocked(Mover, int, int)
	 */
	
	// Tenir en compte k s0haura de generar dos alternaitvas de bloked...
	//Incorporar una variable booleana! per calcular les dos possibilitats
	public boolean blocked(UnitMover mover, int x, int y, int option) {
		// if theres a unit at the location, then it's blocked
		if (getUnit(x,y) != 0) {
			return true;
		}
		
		int unit = ((UnitMover) mover).getType();
		
		
		switch(option){
		// OPTION 
		// Nomes passa per les caselles STREET
			case 1:
				if (unit == SCOUT || unit == HARVESTER ) {
					return ((terrain[x][y] != STREET));
				}
				break;
			
		// OPTION 2
		// Nomes passa per les caselles STREET y UNDISCOVERED 
			case 2:
				if (unit == SCOUT || unit == HARVESTER ) {
					return ((terrain[x][y] != STREET)&&(terrain[x][y] != UNDISCOVERED));
				}
				break;
		}
		
		return true;
	}

	/**
	 * @see TileBasedMap#getCost(Mover, int, int, int, int)
	 */
	public float getCost(UnitMover mover, int sx, int sy, int tx, int ty) {
		return 1;
	}

	/**
	 * @see TileBasedMap#getHeightInTiles()
	 */
	public int getHeightInTiles() {
		return WIDTH;
	}

	/**
	 * @see TileBasedMap#getWidthInTiles()
	 */
	public int getWidthInTiles() {
		return HEIGHT;
	}

	/**
	 * @see TileBasedMap#pathFinderVisited(int, int)
	 */
	public void pathFinderVisited(int x, int y) {
		visited[x][y] = true;
	}
	
	public void printMap()
	{
		for(int r=0;r<HEIGHT;r++){
			for(int c=0;c<WIDTH;c++){
				switch(terrain[c][r])
				{
				case 2:System.out.print("-");break;
				case 1:System.out.print("*");break;
				case 3:System.out.print("o");break;
				case 10:System.out.print("?");break;
				}
			}
			System.out.println(".");
		}
	}
}
