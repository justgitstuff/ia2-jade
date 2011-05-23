package sma;

import java.util.ArrayList;
import java.util.List;

import sma.ontology.Cell;
import sma.ontology.InfoGame;
import jade.core.Agent;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2008-09. Robocup Rescue.</p>
 * <p><b>Copyright:</b> Copyright (c) 2009</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author David Isern & Joan Albert L�pez
 * @version 2.0
 */
public class UtilsAgents {


  public static String CENTRAL_AGENT = "central-agent";
  public static String COORDINATOR_AGENT = "coordinator-agent";

  public static String SCOUT_AGENT = "scout";
  public static String HARVESTER_AGENT = "harvester";
  
  public static String HARVESTER_MANAGER_AGENT = "harvester manager";
  public static String SCOUT_MANAGER_AGENT = "scout manager";

  public static String OWNER = "urv";

  public static String LANGUAGE = "serialized-object";

  public static String ONTOLOGY = "serialized-object";
  
  public static String PROTOCOL_QUERY = "Query";
  public static String PROTOCOL_DOWNLOAD = "Finish_work";
  public static String PROTOCOL_MOVEMENT = "Movement";
  public static String PROTOCOL_TURN = "Turn";
  public static String BE_SCOUT = "Be_scout";
  public static String OK = "OK";
  public static String FAILURE = "failure";
  public static String CONTRACT_NET = "Contract_Net";


  /**
   * Do not use it
   */
  public UtilsAgents() {
  }


  private static long DELAY = 2000; 

  /**
   * To search an agent of a certain type
   * @param parent Agent
   * @param sd ServiceDescription search criterion
   * @return AID of the agent if it is foun, it is a *blocking* method
   */
  public static AID searchAgent( Agent parent, ServiceDescription sd ) {
    /** Searching an agent of the specified type **/
    AID agentBuscat = new AID();
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.addServices( sd );
    try {
      while(true) {
        SearchConstraints c = new SearchConstraints();
        c.setMaxResults( new Long( -1 ) );
        DFAgentDescription[] result = DFService.search( parent, dfd, c );
        if( result.length > 0 ) {
          dfd = result[ 0 ];
          agentBuscat = dfd.getName();
          break;
        }
        Thread.sleep(DELAY); /*Each 5 seconds we try to search*/
      }
   } catch( Exception fe ) {
     fe.printStackTrace();
     System.out.println( parent.getLocalName() +
                         " search with DF is not succeeded because of " +
                         fe.getMessage() );
     parent.doDelete();
   }
   return agentBuscat;
 } //end searchAgent


 /**
  * To create an agent in a given container
  * @param container AgentContainer
  * @param agentName String Agent name
  * @param className String Agent class
  * @param arguments Object[] Arguments; null, if they are not needed
  */
 public void createAgent(AgentContainer container,
                         String agentName,
                         String className,
                         Object[] arguments) {
   try {
     AgentController controller =
         container.createNewAgent(agentName,
                                  className,
                                  arguments);
     controller.start();

   } catch (StaleProxyException e) {
     System.err.println("FATAL ERROR: "+e.toString());
   }
 } //endof createAgent


  /**
   * To create the agent and the container together. You can specify a container and reuse it.
   * @param agentName String Agent name
   * @param className String Class
   * @param arguments Object[] Arguments
   */
  public void createAgent(String agentName, String className, Object[] arguments) {
    try {
      AgentContainer container = null;
      Runtime rt = Runtime.instance();
      Profile p = new ProfileImpl();
      container = rt.createAgentContainer(p);

      AgentController controller = container.createNewAgent(agentName,className, arguments);
      controller.start();

    } catch (Exception e) {
      System.out.println(e.toString());
    }
  } //endof createAgent


  /**
   * To create the agent and the container together, returning the container. 
   * @param agentName String Agent name
   * @param className String Class
   * @param arguments Object[] Arguments
   * @return AgentContainer created
   */
  public AgentContainer createAgentGetContainer(String agentName, String className, Object[] arguments) {
    try {
      AgentContainer container = null;
      Runtime rt = Runtime.instance();
      Profile p = new ProfileImpl();
      container = rt.createAgentContainer(p);

      AgentController controller = container.createNewAgent(agentName,className, arguments);
      controller.start();
      return container;
      
    } catch (Exception e) {
      System.out.println(e.toString());
      return null;
    }
  } //endof createAgent
/**
 * Finds an agent on the game map
 * @param agent - the AID of the agent to search
 * @param game - the InfoGame
 * @return
 */
	public static Cell findAgent(AID agent, InfoGame game)
	{
        Cell agentPosition=null;
		for(int x=0;x<game.getMap().length;x++)
			for(int y=0;y<game.getMap()[x].length;y++)
			{
				Cell c=game.getCell(x,y);
				if(c!=null)
	         	  if(c.isThereAnAgent())
	         	  {
	         		  if(c.getAgent().getAID().equals(agent)){
	         			  agentPosition=c; 
	         		  }
	         	  }
			}
		return agentPosition;
	}
	/**
	 * Calcs the absolute distance between two cells, diagonal movement included 
	 * @param begin
	 * @param end
	 * @return distance in diagonal turns
	 */
	public static int cellDistance(Cell begin, Cell end)
	{
		double c1,c2,diagonal;
		if ((begin!=null)&&(end!=null))
		{
			c1=begin.getRow()-end.getRow();
			c2=begin.getColumn()-end.getColumn();
			c1=Math.pow(c1, 2);
			c2=Math.pow(c2, 2);
			diagonal=Math.sqrt(c1+c2);
			int distance=(int)diagonal;
			System.out.println("**************** Cell distance "+c1+" "+c2+" "+diagonal+" truncate to "+distance);
			return distance;
		}else{
			return 100000;
		}
		
	}
	/**
	 * 
	 * @param game
	 * @return a list of all recycling centers
	 */
	public static List<Cell> getRecyclingCenterList(InfoGame game)
	{
		List<Cell> centers = new ArrayList<Cell>();
		for(int x=0;x<game.getMap().length;x++)
			for(int y=0;y<game.getMap()[x].length;y++)
			{
				Cell c=game.getCell(x, y);
				if(c!=null)
				if(c.getCellType()==Cell.BUILDING)
				{
					centers.add(c);
				}
			}
		return centers;
	}
	/**
	 * Is there garbage of that type arround this cell
	 * @param game - your infoGame
	 * @param type - type of garbage to find
	 * @param c - cell arround witch to find
	 * @return true if there is garbage of that type arround
	 */
	public static boolean isGarbageArround(InfoGame game, int type, Cell c)
	{
		boolean isArround=false;
		int x,y;
		x=c.getColumn();
		y=c.getRow();
		System.out.println("Testing "+type+" arround cell "+c);
		for(int i=x-2;i<=x+2;i++)
			for(int j=y-2;j<=y+2;j++)
			{
				System.out.println("Coordinates "+i+" "+j);
				if(i>=0)
					if(i<game.getMap().length)
						if(j>=0)
							if(j<game.getMap()[0].length)
							{
								Cell cell=game.getCell(j, i);
								System.out.println("with cell "+cell);
								if(cell!=null)
								{
									if (cell.getCellType()==Cell.BUILDING)
									{
										try {
											if(cell.getGarbageUnits()>0)
											{
												System.out.println("Found garbage type "+cell.getCellType()+" agent has type "+type);
												if(Cell.getGarbagePointsIndex(cell.getGarbageType())==type)
													return true;
											}
										} catch (Exception e) {
										}
									}
								}
							}
			}
		return false;
	}
  
} //endof UtilsAgents
