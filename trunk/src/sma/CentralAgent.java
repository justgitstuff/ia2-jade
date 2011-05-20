package sma;

import java.io.*;
import jade.wrapper.AgentController;
import jade.core.*;
import jade.core.Runtime;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import sma.moves.Movement;

import sma.ontology.*;
import sma.gui.*;
import java.util.*;

/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2010-11. Recycle swarm.</p>
 * <p><b>Copyright:</b> Copyright (c) 2011</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author not attributable
 * @version 2.0
 */
public class CentralAgent extends Agent {

  /**
	 * 
	 */
	private static final long serialVersionUID = 2738522227375621454L;
  private sma.gui.GraphicInterface gui;
  private sma.ontology.InfoGame game;
  private sma.ontology.InfoGame publicGame;
  private Statistics stats;

  private AID coordinatorAgent;
  
  private jade.wrapper.AgentContainer ac;

  public CentralAgent() {
    super();
  }

  /**
   * A message is shown in the log area of the GUI
   * @param str String to show
   */
  private void showMessage(String str) {
    if (gui!=null) gui.showLog(str + "\n");
    System.out.println(getLocalName() + ": " + str);
  }

  private java.util.List<Cell> placeAgents(InfoGame currentGame) throws Exception {
      java.util.List<Cell> agents = new java.util.ArrayList<Cell>();
      for(int k=0; k<currentGame.getInfo().getNumScouts(); k++) {
        InfoAgent b = new InfoAgent(InfoAgent.SCOUT);
        ((currentGame.getMap())[9+k][9]).addAgent(b);
        agents.add(currentGame.getCell(9+k,9));
      }
      for(int k=0; k<currentGame.getInfo().getNumHarvesters(); k++) {
    	InfoAgent p = new InfoAgent(InfoAgent.HARVESTER);
        ((currentGame.getMap())[k+2][0]).addAgent(p);
        agents.add( currentGame.getCell(k+2,0));
      }
      return agents;
    }
  
  /**
   * Agent setup method - called when it first come on-line. Configuration
   * of language to use, ontology and initialization of behaviours.
   */
  protected void setup() {

    /**** Very Important Line (VIL) *********/
    this.setEnabledO2ACommunication(true, 1);
    /****************************************/


//    showMessage("Agent (" + getLocalName() + ") .... [OK]");

    // Register the agent to the DF
    ServiceDescription sd1 = new ServiceDescription();
    sd1.setType(UtilsAgents.CENTRAL_AGENT);
    sd1.setName(getLocalName());
    sd1.setOwnership(UtilsAgents.OWNER);
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.addServices(sd1);
    dfd.setName(getAID());
    try {
      DFService.register(this, dfd);
      showMessage("Registered to the DF");
    }
    catch (FIPAException e) {
      System.err.println(getLocalName() + " registration with DF " +
                         "unsucceeded. Reason: " + e.getMessage());
      doDelete();
    }


   /**************************************************/

    try {
      this.game = new InfoGame(); //object with the game data
      this.publicGame = new InfoGame(); // game data to publish
      this.publicGame.readGameFile("game.txt");
      this.game.readGameFile("game.txt");
      //game.writeGameResult("result.txt", game.getMap());
    } catch(Exception e) {
      e.printStackTrace();
      System.err.println("Game NOT loaded ... [KO]");
    }
    try {
      this.gui = new GraphicInterface(game);
      gui.setVisible(true);
      showMessage("Game loaded ... [OK]");
    } catch (Exception e) {
      e.printStackTrace();
    }

   /****Agents are randomly placed****************/
   java.util.List<Cell> agents = null;
   try{
	   agents = placeAgents(this.game);
   }catch(Exception e){}
   
   /**************************************************/
   this.game.getInfo().fillAgentsInitialPositions(agents, game);
   
   //Create real Agents and fill AID
   createAgents();
 

   //init Statistics
   stats=new Statistics(game,gui);
   
   //add behaviours

   // Search for coordinator Agent
   ServiceDescription searchCriterion = new ServiceDescription();
   searchCriterion.setType(UtilsAgents.COORDINATOR_AGENT);
   this.coordinatorAgent = UtilsAgents.searchAgent(this, searchCriterion);
   // searchAgent is a blocking method, so we will obtain always a correct AID

  
   
   // add behaviours

   // we wait for the initialization of the game
   /* @SuppressWarnings("unused")
	MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol(InteractionProtocol.FIPA_REQUEST), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
    
   this.addBehaviour(new RequestResponseBehaviour(this, null));

   // Setup finished. When the last inform is received, the agent itself will add
   // a behavious to send/receive actions
*/
   
   // Start the simulation
   this.addBehaviour(new TurnControlBehavior(this, game.getInfo().getTimeout()));
   
   // Add a behavior to receive movement orders
   MessageTemplate mt=MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_MOVEMENT);
   this.addBehaviour(new MovesReceiver(this, mt));
   
   
  } //endof setup

  
  private void initContainer()
  {
	  // Get a hold on JADE runtime
	    Runtime rt = Runtime.instance();
	    // Exit the JVM when there are no more containers around
	    rt.setCloseVM(true);
	    Profile p = new ProfileImpl(true);
	    System.out.println(getLocalName()+": Launching the agent container ...\n-Profile: " + p);
	    ac = rt.createAgentContainer(p);
  }
  
private AID createAgent(String name, String type, Object args[])
{
	AID itsAID=null;
	try{
		//Create Agent in new container
		System.out.println(getLocalName()+" Creating NEW agent ("+name+", "+type+")...");
	    AgentController another= ac.createNewAgent(name, type, args);
	    another.start();
	    //Return its AID
	    ServiceDescription searchCriterion = new ServiceDescription();
	    searchCriterion.setName(name);
	    itsAID = UtilsAgents.searchAgent(this, searchCriterion);
	} catch (jade.wrapper.StaleProxyException e) {
		System.err.println("ERROR for creating the agent. Reason: "+e.toString());
		e.printStackTrace();
	} catch (Exception e4) {
		System.err.println("Error in reading config file"+e4.toString());
	}
	return itsAID;
}
  
private void updatePublicGame()
{
	publicGame.setInfo(game.getInfo());
	for(int r=0;r<game.getMap().length-1;r++)
		for(int c=0;c<game.getMap()[r].length-1;c++)
		{
			if (game.getCell(r, c).isDiscovered())
			{
				publicGame.setCell(r, c, game.getCell(r, c));
			}else
				publicGame.setCell(r, c, null);
		}
}


  /**
   * Create Real Agents and fill AID
   */
	protected void createAgents() 
	{
		updatePublicGame();
		AuxInfo info=game.getInfo();
	    initContainer();
        HashMap<?, ?> hm = info.getAgentsInitialPosition();
	    Iterator<?> it = hm.keySet().iterator();
	    int agentNumber=1;
	    while (it.hasNext())
	    {
	        	  InfoAgent ia = (InfoAgent)it.next();
	              //Creating the agent
	              AID aid=null;
            	  Object[] o;
            	  o=new Object[1];
            	  o[0]=publicGame;
	              try{
		              switch (ia.getAgentType())
		              {
			              case 0: aid=createAgent(ia.getAgent()+agentNumber, "sma.scout.ScoutAgent", o); break;
			              case 1: aid=createAgent(ia.getAgent()+agentNumber, "sma.harvester.HarvesterAgent", o); break;
		              }
		              ia.setAID(aid);
	              }catch(Exception e){
	            	  showMessage("Could not create the agent specified in the game");
	              }
	              agentNumber++;
	    }
}
  
  
  /**
   * Cyclic behavior each cycle is a game turn, also controls simulation ending
   * @author Roger
   *
   */
  private class TurnControlBehavior extends TickerBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4793117331763237218L;

	public TurnControlBehavior(Agent arg0, long arg1) {
		super(arg0, arg1);
		showMessage("Turn Control Initiated timeout is "+game.getInfo().getTimeout());
	}

	@Override
	protected void onTick() {
		game.getInfo().incrTurn();
		showMessage("Turn "+game.getInfo().getTurn());
		if (game.getInfo().getTurn()==game.getInfo().getGameDuration())
		{
			showMessage("Game Finished");
			this.stop();
			try {
				game.writeGameResult("result.txt", game.getMap());
			} catch (IOException e) {
				showMessage("Cannot write the game results");
				e.printStackTrace();
			} catch (Exception e) {
				showMessage("General error writing game results");
				e.printStackTrace();
			}
			stats.show();
		}else{
			//Send updated map to all Coordinator Agent
		    ACLMessage requestInicial = new ACLMessage(ACLMessage.REQUEST);
		    requestInicial.clearAllReceiver();
		    requestInicial.addReceiver(coordinatorAgent);
		    requestInicial.setProtocol(InteractionProtocol.FIPA_QUERY);
		    try {
		      requestInicial.setContentObject(publicGame);
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    //we add a behavior that sends the message and waits for an answer
		    updatePublicGame();
		    this.myAgent.addBehaviour(new Informer(this.myAgent, requestInicial));
		}
	} 
  }
  
  /**
   * 
   * @author roger
   * User to inform the coordinator agent
   */
  class Informer extends AchieveREInitiator
  {

	@Override
	protected void handleAgree(ACLMessage arg0) {
		super.handleAgree(arg0);
	}

	@Override
	protected void handleInform(ACLMessage arg0) {
		// TODO Auto-generated method stub
		super.handleInform(arg0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8199039269513958089L;

	public Informer(Agent arg0, ACLMessage arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	  
	
  }
  
  
  /*************************************************************************/

  /*************************************************************************/

  class MovesReceiver extends AchieveREResponder
  {
	public MovesReceiver(Agent arg0, MessageTemplate arg1) {
		super(arg0, arg1);
	}
	
	@Override
	protected ACLMessage prepareResponse(ACLMessage arg0)
			throws NotUnderstoodException, RefuseException {
		Cell origin = null,destination;
		InfoAgent ia = null;
		ACLMessage response=arg0.createReply();
		int dx=0,dy=0,x,y;
		sma.moves.Movement moveOrder;
		try {
			moveOrder = (Movement) arg0.getContentObject();
		} catch (UnreadableException e1) {
			moveOrder=null;
			response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		}
		if(moveOrder!=null)
			if(moveOrder.getAgent()!=null)
				if(findAgent(moveOrder.getAgent())!=null)
		{
			//showMessage("Received movement order from "+moveOrder.getAgent().getLocalName());
			origin=findAgent(moveOrder.getAgent());	
			x=origin.getColumn();
			y=origin.getRow();
			//showMessage("I have its position "+origin);
			boolean diagonal=false;
			switch (moveOrder.getDirection())
			{
				case UP:dy=-1;/*showMessage("UP");*/break;
				case DOWN:dy=1;/*showMessage("DOWN");*/break;
				case LEFT:dx=-1/*;showMessage("LEFT")*/;break;
				case RIGHT:dx=1;/*showMessage("RIGHT");*/break;
				case UPLEFT:dx=-1;dy=-1;diagonal=true;break;
				case UPRIGHT:dx=1;dy=-1;diagonal=true;break;
				case DOWNLEFT:dx=-1;dy=1;diagonal=true;break;
				case DOWNRIGHT:dx=1;dy=1;diagonal=true;break;
			}

			//showMessage("I have its destination "+destination);
			response.setPerformative(ACLMessage.FAILURE);
			if(y+dy>=0)
				if(y+dy<game.getMap()[0].length)
					if(x+dx>=0)
						if(x+dx<game.getMap().length)
						{
							//showMessage("Will move "+dx+" "+dy);
							destination=game.getMap()[y+dy][x+dx];
							switch (moveOrder.getAction())
							{
							case GO:
								try{
									showMessage(origin.getAgent().getAgent()+" Moving");
									if (diagonal) throw new Exception();
									//showMessage("Its a go order");
									ia=origin.getAgent();
									//showMessage("Have the agent to remove");
									origin.removeAgent(ia);
									//showMessage("Agent Removed");
									destination.addAgent(ia);
									//showMessage("Agent added to new position");
									response.setPerformative(ACLMessage.AGREE);
									stats.incMovement(ia);
								}catch (Exception e) {
									response.setPerformative(ACLMessage.FAILURE);
									//showMessage("Could not move to that position");
									if(!origin.isThereAnAgent())
										try {
											origin.addAgent(ia);
										} catch (Exception e1) {
											showMessage("FATAL ERROR: Cannot Undo this action");
										}
								}break;
							case GET:
								try
								{
									showMessage(origin.getAgent().getAgent()+" Getting Garbage");
									getGarbage(moveOrder, origin, destination);
									response.setPerformative(ACLMessage.AGREE);
								}catch(Exception e){
									showMessage("Failure GETTING");
									response.setPerformative(ACLMessage.FAILURE);
								}
								break;
							case PUT:
								try {
									showMessage(origin.getAgent().getAgent()+" Putting Garbage");
									putGarbage(moveOrder, origin, destination);
									response.setPerformative(ACLMessage.AGREE);
								} catch (Exception e) {
									response.setPerformative(ACLMessage.FAILURE);
								}
									
								break;
							}
						}
		}

		return response;
	}
	
	private boolean agentIsAbleToCarry(InfoAgent agent, char garbageType)
	{
		switch (garbageType)
		{
		case 'G': return agent.getGarbageType()[0];
		case 'P': return agent.getGarbageType()[1];
		case 'M': return agent.getGarbageType()[2];
		case 'A': return agent.getGarbageType()[3];
		}
		return false;
	}
	
	private void getGarbage(Movement moveOrder, Cell origin, Cell destination) throws Exception {
		// Check if destination is a Building
		showMessage("Check if destination is a Building");
		if (destination.getCellType()!=Cell.BUILDING) throw new Exception();
		// Check if destination has the type of garbage we want to get
		showMessage("Check if destination has the type of garbage we want to get");
		//if (!moveOrder.getType().equals(destination.getGarbageType())) throw new Exception();
		// Check if destination has enough garbage units 
		showMessage("Check if destination has enough garbage units ");
		int gu=destination.getGarbageUnits();
		if (gu<1) throw new Exception();
		// Check if agent has enough room to carry more garbage
		showMessage("Check if agent has enough room to carry more garbage");
		InfoAgent agent=origin.getAgent();
		int au=agent.getUnits();
		if (au==agent.getMaxUnits()) throw new Exception();
		// Check if agent has the ability to carry that type of garbage
		showMessage("Check if agent has the ability to carry that type of garbage");
		//if(!agentIsAbleToCarry(agent, destination.getGarbageType())) throw new Exception();
		showMessage("GET ALL OK");
		// All OK, go ahead with getting garbage
		destination.setGarbageUnits(gu-1);
		agent.setUnits(au+1);
		agent.setCurrentType(destination.getGarbageType());
		
		// TODO discrimine when agent already has garbage and tries to get a different type of garbage
	}
	
	private void putGarbage(Movement moveOrder, Cell origin, Cell destination) throws Exception
	{
		// Check if destination is a recycling center
		if (destination.getCellType()!=Cell.RECYCLING_CENTER) throw new Exception();
		// Check if destination accepts this type of garbage
		InfoAgent agent=origin.getAgent();
		int garbageType =agent.getCurrentType();
		int points;
		points=destination.getGarbagePoints()[garbageType];
		if(points==0) throw new Exception();
		// Check if agent has garbage
		int au=agent.getUnits();
		if(au==0) throw new Exception();
		
		// All OK, go ahead putting garbage
		agent.setUnits(au-1);
		stats.scoreGarbage(points);
		// TODO complete this
		
	}

	@Override
	protected ACLMessage prepareResultNotification(ACLMessage arg0,
			ACLMessage arg1) throws FailureException {
		return null;
	}
	private Cell findAgent(AID a)
	{
        Cell agentPosition=null;
		for(int r=0;r<game.getMap().length-1;r++)
			for(int c=0;c<game.getMap()[c].length-1;c++)
			{
				Cell cell=game.getCell(r,c);
	         	  if(cell.isThereAnAgent())
	         	  {
	         		  if(cell.getAgent().getAID().equals(a)){
	         			  agentPosition=cell; 
	         		  }
	         	  }
			}
		return agentPosition;
	}

	private static final long serialVersionUID = -1637780111859751489L;


	  
  }

} //endof class AgentCentral
