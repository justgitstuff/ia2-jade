package sma.harvester_manager;
import java.util.ArrayList;
import java.util.List;

import jade.util.leap.Serializable;

public class DistanceList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List <Integer> ldistance;
	public DistanceList(){	
		ldistance = new ArrayList <Integer> ();	
	}	
	
	public void addDistance(int dist){
		ldistance.add(dist);
	}
	
	public List<Integer> getDistances(){
		return ldistance;
	}

}
