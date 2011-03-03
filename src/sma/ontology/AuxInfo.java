package sma.ontology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import sma.gui.UtilsGUI;
import java.util.*;

/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2010-11. Recycle swarm.</p>
 * Additional information which is sent to the coordinator agent during
 * the initialization. This object is initialized from a file.
 * <p><b>Copyright:</b> Copyright (c) 2011</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author David Isern & Joan Albert López
 * @see sma.CoordinatorAgent
 * @see sma.CentralAgent
 */
public class AuxInfo implements java.io.Serializable {
	  private int gameDuration;
	  private long timeout;
	  private int turn;
	  private int numScouts;
	  private int numHarvesters;
	  private String[] typeHarvesters;
	  private int[] capacityHarvesters;

	  private HashMap agentsInitialPosition = new HashMap(); //For each InfoAgent it contains a list with all the cells within its visual range
	  private List recyclingCenters = new ArrayList(); //It contains the list of cells with recycling centers
	  
	  public int getGameDuration() { return this.gameDuration; }
	  protected void setGameDuration(int d) { this.gameDuration = d; }

	  public long getTimeout() { return this.timeout; }
	  protected void setTimeout(long n) { this.timeout = n; }

	  public int getTurn() { return this.turn; }
	  public void incrTurn() { this.turn++; }

	  public boolean isEndGame() { return (this.turn>=this.gameDuration); }

	  public int getNumScouts() { return this.numScouts; }

	  public int getNumHarvesters() { return this.numHarvesters; }
	
	  protected void setNumScouts(int numScouts) {
		this.numScouts = numScouts;
	  }
	  protected void setNumHarvesters(int numHarvesters) {
		this.numHarvesters = numHarvesters;
	  }
	  protected void setTypeHarvesters(String[] typeHarvesters) {
		this.typeHarvesters = typeHarvesters;
	  }
	  public String[] getTypeHarvesters() {
			return typeHarvesters;
	  }
	  protected void setCapacityHarvesters(int[] capacityHarvesters) {
		this.capacityHarvesters = capacityHarvesters;
	  }
	  public int[] getCapacityHarvesters() {
	  	return capacityHarvesters;
	  }	
	  public void fillAgentsInitialPositions (List<Cell> agents, InfoGame info){
		  Iterator it = agents.iterator();
		  while (it.hasNext()){
			  Cell c = (Cell)it.next();
			  int x=c.getRow(); int y=c.getColumn();
			  List<Cell> list = new ArrayList<Cell>();
			  list.add(c);
			  if (x>0){
				  list.add(info.getCell(x-1, y));
		    	  if (y>0) list.add(info.getCell(x-1, y-1));
		    	  if (y<info.getMap()[x].length-1) list.add(info.getCell(x-1, y+1));
		      }
	    	  if (x<info.getMap().length-1){
	    		  list.add(info.getCell(x+1, y));
	    		  if (y>0) list.add(info.getCell(x+1, y-1));
	    		  if (y<info.getMap()[x].length-1) list.add(info.getCell(x+1, y+1));
	    	  }
	    	  if (y>0) list.add(info.getCell(x, y-1));
			  if (y<info.getMap()[x].length-1) list.add(info.getCell(x, y+1));
			  agentsInitialPosition.put(c.getAgent(), list);
		  }
	}
	public HashMap getAgentsInitialPosition() {
		return agentsInitialPosition;
	}
	public void setAgentsInitialPosition(HashMap agentsInitialPosition) {
		this.agentsInitialPosition = agentsInitialPosition;
	}
	public List getRecyclingCenters() {
		return recyclingCenters;
	}
	public void setRecyclingCenters(List recyclingCenters) {
		this.recyclingCenters = recyclingCenters;
	}
	public void addRecyclingCenter (Cell c){
		recyclingCenters.add(c);
	}
}
