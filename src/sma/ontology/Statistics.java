package sma.ontology;


import java.util.ArrayList;
import java.util.List;

public class Statistics {

	private int points, maxPoints, unitsGarbaged, totalGarbage, turnsToFinish, discoveredBuildings, totalGarbageBuildings;
	private boolean gameFinished;
	private InfoGame game;
	private List<Cell> garbageBuildings;
	
	sma.gui.GraphicInterface gui;
	private int turnsToFindGarbageBuildings;
	/**
	 * Percent of points over the maximum
	 * Percent of total garbage collected
	 * turns needed to collect all garbage
	 * Percent of buildings with garbage discovered
	 * turns needed to discover all garbage
	 * number of movements for each vehicle
	 * @param game
	 */
	public Statistics(InfoGame game, sma.gui.GraphicInterface gui)
	{
		this.gui=gui;
		this.game=game;
		gameFinished=false;
		points=0;
		totalGarbage=0;
		try {
			setMaxPoints(calcMaxPoints(game));
			totalGarbage=calcMaxGarbage();
		} catch (Exception e) {e.printStackTrace();System.err.println("Statistics Error: could not init statistics");}
		garbageBuildings=findGarbageBuildings();
		totalGarbageBuildings=garbageBuildings.size();
		unitsGarbaged=0;
		turnsToFinish=-1;
		turnsToFindGarbageBuildings=-1;
		//TODO pensar com fer això discoveredBuildings=findDiscoveredBuildings(game);
		
	}
	
	private List<Cell> findGarbageBuildings() {
		List<Cell> buildings=new ArrayList<Cell>();
		for(int x=0;x<game.getMap().length;x++)
			for(int y=0;y<game.getMap()[x].length;y++)
			{
				Cell c=game.getCell(x, y);
				if(c.getCellType()==Cell.BUILDING)
					try {
						if(c.getGarbageUnits()>0)
						{
							buildings.add(c);
							showMessage("Building ADDED " +c );
						}
					} catch (Exception e) {
						showMessage("FAILED ADDING STATISTICS");
						e.printStackTrace();
					}
			}
		return buildings;
	}

	private int calcMaxGarbage() throws Exception {
		int total=0;
		for(int x=0;x<game.getMap().length;x++)
			for(int y=0;y<game.getMap()[x].length;y++)
			{
				Cell c=game.getCell(x, y);
				if(c.getCellType()==Cell.BUILDING)
					total+=c.getGarbageUnits();
			}
		return total;
	}

	public void incMovement(InfoAgent a)
	{
		int aux;
		aux=a.getMovements();
		a.setMovements(aux+1);
		updateBuildingsDiscovered();
		a.setLastTurn(game.getInfo().getTurn());
	}
	
	private void updateBuildingsDiscovered() {
		
	
		for(int x=0;x<game.getMap().length;x++)
			for(int y=0;y<game.getMap()[x].length;y++)
			{
				Cell c=game.getCell(x, y);
				if(c.getCellType()==Cell.BUILDING)
					if(c.isDiscovered())
					{	
						if(garbageBuildings.contains(c))
						{
							garbageBuildings.remove(c);
							if(garbageBuildings.isEmpty())
							{
								turnsToFindGarbageBuildings=game.getInfo().getTurn();
							}
						}
					}
			}	
		
	}

	public void scoreGarbage(int points)
	{
		this.points+=points;
		this.unitsGarbaged++;
		if (!gameFinished)
			if(isFinished())
			{
				gameFinished=true;
				turnsToFinish=game.getInfo().getTurn();
			}
	}
	
 	public boolean isFinished()
	{
		for(int x=0;x<game.getMap().length;x++)
			for(int y=0;y<game.getMap()[x].length;y++)
			{
				Cell c=game.getCell(x, y);
				try {
					if(c.getCellType()==Cell.BUILDING)
						if (c.getGarbageUnits()!=0) return false;
					if (c.isThereAnAgent())
						if(c.getAgent().getUnits()!=0)
							return false;
				} catch (Exception e) {
					// Rarely will go here
				}
			}
		return true;
	}
	
	private int calcMaxPoints(InfoGame game) throws Exception
	{
		int result;
		
		int totalGarbage[]=new int[4];
		for (int i=0;i<4;i++) totalGarbage[i]=0;
		
		
		for(int x=0;x<game.getMap().length;x++)
			for(int y=0;y<game.getMap()[x].length;y++)
			{
				Cell c=game.getCell(x, y);
				if(c.getCellType()==Cell.BUILDING)
					if (c.getGarbageUnits()>0)
					{
						totalGarbage[Cell.getGarbagePointsIndex(c.getGarbageType())]+=c.getGarbageUnits();
					}
			}
		int maxPoints[]=new int[4];
		for (int i=0;i<4;i++) maxPoints[i]=0;
		
		for(int x=0;x<game.getMap().length;x++)
			for(int y=0;y<game.getMap()[x].length;y++)
			{
				Cell c=game.getCell(x, y);
				if(c.getCellType()==Cell.RECYCLING_CENTER)
				{
					for (int i=0;i<4;i++)
					{
						maxPoints[i]=Math.max(maxPoints[i],c.getGarbagePoints()[i]);
					}
				}
			}
		
		result=0;
		for (int i=0;i<4;i++)
		{
			result+=maxPoints[i]*totalGarbage[i];
		}
		
		return result;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}

	public int getPoints() {
		return points;
	}

	public void setUnitsGarbaged(int unitsGarbaged) {
		this.unitsGarbaged = unitsGarbaged;
	}

	public int getUnitsGarbaged() {
		return unitsGarbaged;
	}

	public void setTurnsToFinish(int turnsToFinish) {
		this.turnsToFinish = turnsToFinish;
	}

	public int getTurnsToFinish() {
		return turnsToFinish;
	}

	public void setDiscoveredBuildings(int discoveredBuildings) {
		this.discoveredBuildings = discoveredBuildings;
	}

	public int getDiscoveredBuildings() {
		return discoveredBuildings;
	}

	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}

	public int getMaxPoints() {
		return maxPoints;
	}
	
	  /**
	   * A message is shown in the log area of the GUI
	   * @param str String to show
	   */
	  private void showMessage(String str) {
	    if (gui!=null) gui.showLog(str + "\n");
	    System.out.println(str);
	  }
	
	public void show(){
		gui.showStatistics("");
		gui.showStatistics("Simulation Finished");
		gui.showStatistics("*******************");
		gui.showStatistics("");
		gui.showStatistics("Points earned: "+points);
		gui.showStatistics("Max points: "+ maxPoints);
		gui.showStatistics("Percentage earned: "+ (100.0*points)/maxPoints+"%");
		gui.showStatistics("Garbage collected: "+unitsGarbaged);
		gui.showStatistics("Total garbage: "+totalGarbage);
		gui.showStatistics("Turns to finish: "+turnsToFinish);
		gui.showStatistics("Buildings discovered: "+ (totalGarbageBuildings-garbageBuildings.size()));
		gui.showStatistics("Total Garbage Buildings: "+ totalGarbageBuildings);
		gui.showStatistics("Percentage of Garbage Buildings discovered :"+ (100- 100.0*garbageBuildings.size()/totalGarbageBuildings)+"%");
		gui.showStatistics("Turns to find all garbage buildings: "+turnsToFindGarbageBuildings);
		for(int x=0;x<game.getMap().length;x++)
			for(int y=0;y<game.getMap()[x].length;y++)
			{
				Cell c=game.getCell(x, y);
				if(c.isThereAnAgent())
				{	
					InfoAgent ia=c.getAgent();
					try {
						gui.showStatistics("Agent "+ia.getAID().getLocalName()+": "+ia.getMovements()+ " movements");
					} catch (Exception e) {
						// Nothing terrible
					}
				}
			}
		gui.showStatistics("");
		gui.showStatistics("*******************");

	}
	
}
