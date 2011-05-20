package sma.ontology;

public class Statistics {

	private int points, maxPoints, unitsGarbaged, totalGarbage, turnsToFinish, discoveredBuildings, discoveredGarbageBuildings;
	private boolean gameFinished;
	private InfoGame game;
	sma.gui.GraphicInterface gui;
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
		} catch (Exception e) {System.err.println("Statistics Error: could not init statistics");}

		unitsGarbaged=0;
		turnsToFinish=-1;
		//TODO pensar com fer això discoveredBuildings=findDiscoveredBuildings(game);
		
	}
	
	private int calcMaxGarbage() throws Exception {
		int total=0;
		for(int x=0;x<game.getMap().length-1;x++)
			for(int y=0;y<game.getMap()[x].length-1;y++)
			{
				Cell c=game.getCell(x, y);
				total+=c.getGarbageUnits();
			}
		return total;
	}

	public void incMovement(InfoAgent a)
	{
		int aux;
		aux=a.getMovements();
		a.setMovements(aux+1);
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
	
 	private boolean isFinished()
	{
		for(int x=0;x<game.getMap().length-1;x++)
			for(int y=0;y<game.getMap()[x].length-1;y++)
			{
				Cell c=game.getCell(x, y);
				try {
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
	
	/*public int findDiscoveredBuildings(InfoGame game)
	{
		for(int x=0;x<game.getMap().length-1;x++)
			for(int y=0;y<game.getMap()[x].length-1;y++)
			{
				Cell c=game.getCell(x, y);
				if (c.isDiscovered())
					if(c.getGarbageUnits())
			}
	}*/
	
	private int calcMaxPoints(InfoGame game) throws Exception
	{
		int result;
		
		int totalGarbage[]=new int[4];
		for (int i=0;i<4;i++) totalGarbage[i]=0;
		
		
		for(int x=0;x<game.getMap().length-1;x++)
			for(int y=0;y<game.getMap()[x].length-1;y++)
			{
				Cell c=game.getCell(x, y);
				if (c.getGarbageUnits()>0)
				{
					totalGarbage[c.getGarbageType()]+=c.getGarbageUnits();
				}
			}
		int maxPoints[]=new int[4];
		for (int i=0;i<4;i++) maxPoints[i]=0;
		
		for(int x=0;x<game.getMap().length-1;x++)
			for(int y=0;y<game.getMap()[x].length-1;y++)
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

	public void setDiscoveredGarbageBuildings(int discoveredGarbageBuildings) {
		this.discoveredGarbageBuildings = discoveredGarbageBuildings;
	}

	public int getDiscoveredGarbageBuildings() {
		return discoveredGarbageBuildings;
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
		
		showMessage("Simulation Finished");
		showMessage("*******************");
		showMessage("");
		showMessage("Points earned: "+points);
		showMessage("Max points: "+ maxPoints);
		showMessage("Percentage earned: "+ (100.0*points)/maxPoints+"%");
		showMessage("Garbage collected: "+unitsGarbaged);
		showMessage("Total garbage: "+totalGarbage);
		showMessage("Turns to finish: "+turnsToFinish);
		showMessage("");
		showMessage("");
		showMessage("");
		showMessage("");
		showMessage("");
		showMessage("");
		showMessage("");
		showMessage("");
		showMessage("");
		
		
		for(int x=0;x<game.getMap().length-1;x++)
			for(int y=0;y<game.getMap()[x].length-1;y++)
			{
				Cell c=game.getCell(x, y);
				if(c.isThereAnAgent())
				{	
					InfoAgent ia=c.getAgent();
					try {
						showMessage("Agent "+ia.getAID().getLocalName()+": "+ia.getMovements()+ " movements");
					} catch (Exception e) {
						// Nothing terrible
					}
				}
			}


	}
	
}
