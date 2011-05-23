package sma.pathFinding;



import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import sma.ontology.InfoGame;



/**
 * A simple test to show some path finding at unit
 * movement for a tutorial at http://www.cokeandcode.com
 * 
 */
public class PathTest{
	/** The map on which the units will move */
	private GameMap map;
	/** The path finder we'll use to search our map */
	private AStarPathFinder finder;
	/** The last path found for the current unit */
	private Path path;
	
	/** The list of tile images to render the map */
	private Image[] tiles = new Image[6];
	/** The offscreen buffer used for rendering in the wonder world of Java 2D */
	private Image buffer;
	
	/** The x coordinate of selected unit or -1 if none is selected */
	private int selectedx = -1;
	/** The y coordinate of selected unit or -1 if none is selected */
	private int selectedy = -1;
	
	/** The x coordinate of the target of the last path we searched for - used to cache and prevent constantly re-searching */
	private int lastFindX = -1;
	/** The y coordinate of the target of the last path we searched for - used to cache and prevent constantly re-searching */
	private int lastFindY = -1;
	
	/**
	 * Create a new test game for the path finding tutorial
	 */
	public PathTest(InfoGame infoGame) {
		
		
	 	map= new GameMap(infoGame);
		
		
		finder = new AStarPathFinder(map, 500);
		
	}
	
	
  
	 
	 /**
		 * Handle the mouse being pressed. If the mouse is over a unit select it. Otherwise we move
		 * the selected unit to the new target (assuming there was a path found)
		 * 
		 * @param x The x coordinate of the mouse cursor on the screen
		 * @param y The y coordinate of the mouse cursor on the screen
		 */
		public void PosicioInicial(int x, int y, int option) {
		
			//System.out.println("Posicio Inicial "+x+" "+y);
			if ((x < 0) || (y < 0) || (x >= map.getWidthInTiles()) || (y >= map.getHeightInTiles())) {
				return;
			}
			
			if (map.getUnit(x, y) != 0) {
				selectedx = x;
				selectedy = y;
				lastFindX = - 1;
			} else {
				//if (true){
					map.clearVisited();
					path = finder.findPath(new UnitMover(map.getUnit(selectedx, selectedy)), 
							   			   selectedx, selectedy, x, y, option);
					
					if (path != null) {
						path = null;
						int unit = map.getUnit(selectedx, selectedy);
						map.setUnit(selectedx, selectedy, 0);
						map.setUnit(x,y,unit);
						selectedx = x;
						selectedy = y;
						lastFindX = - 1;
					}
			//	}
			}
			//System.out.printf("Selected: "+selectedx+" "+selectedy);
		}

	/**
	 * Handle the mouse being moved. In this case we want to find a path from the
	 * selected unit to the position the mouse is at
	 * 
	 * @param x The x coordinate of the mouse cursor on the screen
	 * @param y The y coordinate of the mouse cursor on the screen
	 */

// TENIR EN COMPTE K SERA UN STREET				
	public Path PosicioFinal(int x, int y, int option) {		
		//if (true) {
			// D'acord l'algoritme la posicio final interve en el calcul! per a k sigui possible la convertime temporalment en una 
			// posicio accesible(STREET)
		
				map.setTerrain(x, y);
		
			if ((lastFindX != x) || (lastFindY != y)) {
				lastFindX = x;
				lastFindY = y;
				path = finder.findPath(new UnitMover(map.getUnit(selectedx, selectedy)), selectedx, selectedy, x, y, option);		
			}
		//}
		
		return path;
	}
		
	/* Distancia sense tenir en compte pesos caselles
	public int distancia(Path path){
		System.out.println(path.getLength());
			return path.getLength();
	}*/
	

	public int distanciaPesos(Path path){
		int x,y,dist=0;
		
		if(path==null)
		{
			//System.out.println("Path es null");
			return 10000;
		}
		else{
			for(int i=1;i<path.getLength();i++){
				
				//System.out.println(" Pas"+ (i+1) + ":  " + path.getStep(i).getX()+" "+ path.getStep(i).getY());
				 x=path.getStep(i).getX();
				 y=path.getStep(i).getY();
				
				 //WATER
				 if( map.getTerrain(x, y) == GameMap.UNDISCOVERED)dist=dist+2; else dist++;
				 
				// System.out.println(map.getTerrain(x, y));
			}			
			return dist;
		}
	}
	
	public void stepsFinals(Path path){
		
		//System.out.println("----Passsos a seguir-----");
		// Imprimeix els passos k realitza!
		for(int i=0;i<path.getLength();i++){
			System.out.println(" Pas"+ (i+1) + ":  " + path.getStep(i).getX()+" "+ path.getStep(i).getY());
		}								
		//System.out.println("-----------------------");
}

	
	
	
	/**
	 * Entry point to our simple test game
	 * 
	 * @param argv The arguments passed into the game
	 */
	//public static void main(String[] argv) {
		
	//	PathTest test = new PathTest();		
		
		
	//}
}
