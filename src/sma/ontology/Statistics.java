package sma.ontology;

public class Statistics {

	private int points, maxPoints, unitsGarbaged, turnsToFinish, discoveredBuildings, discoveredGarbageBuildings;

	public Statistics(InfoGame game)
	{
		points=0;
		try {
			setMaxPoints(calcMaxPoints(game));
		} catch (Exception e) {System.err.println("Statistics Error: could not init statistics");}
		
		
	}
	
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
	
	
}
