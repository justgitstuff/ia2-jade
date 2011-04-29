package ferran;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import sma.gui.Quadrant;
import sma.ontology.Cell;
import sma.scout_manager.Point;
import sma.scout_manager.ScoutManagerUtils;

public class ProvaForats {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Cell[][] map = new Cell[10][10];
			FileInputStream fstream = new FileInputStream("test/ferran/map.txt");
			// Get the object of DataInputStream
		    BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
		    for (int i = 0; i < 10; i++) {
		    	String linia = br.readLine();
		    	for (int j = 0; j < 10; j++) {
		    		String character;
		    		character = Character.toString(linia.charAt(j));
		    		if (character.equals("O")) {
		    			map[i][j] = new Cell(Cell.STREET);
		    		} else if (character.equals("P")) {
		    			map[i][j] = new Cell(Cell.BUILDING);
		    		} else if (character.equals("X")) {
		    			map[i][j] = new Cell(Cell.UNCHARTED);
		    		}
		    	}
		    }
		    
		    Point point = ScoutManagerUtils.chooseUnchartedPointToSendScout(new Quadrant(0, 9, 0, 9), map, new Point(2, 9));
		    System.out.println("El millor punt és el " + point);
		    
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
