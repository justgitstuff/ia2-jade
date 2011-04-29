package ferran;

import java.util.ArrayList;
import java.util.List;

import sma.gui.Quadrant;
import sma.scout_manager.ScoutManagerUtils;

public class ProvaQuadrants {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Quadrant> listQuadrants = new ArrayList<Quadrant>();
		
		listQuadrants = ScoutManagerUtils.divideCity(20, 30, 5);
		
		listQuadrants.size();
	}

}
