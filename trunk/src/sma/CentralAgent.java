package sma;

import java.lang.*;
import java.io.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import jade.util.leap.ArrayList;
import jade.util.leap.List;
import jade.wrapper.AgentController;
import jade.core.*;
import jade.core.Runtime;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.onto.*;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

import sma.moves.Movement;
import sma.moves.Movement.Order;
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

  @SuppressWarnings("unused")
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
 

   //add behaviours

   // busco AgentCoordinador
   ServiceDescription searchCriterion = new ServiceDescription();
   searchCriterion.setType(UtilsAgents.COORDINATOR_AGENT);
   this.coordinatorAgent = UtilsAgents.searchAgent(this, searchCriterion);
   // searchAgent is a blocking method, so we will obtain always a correct AID

   // add behaviours

   // we wait for the initialization of the game
    @SuppressWarnings("unused")
	MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol(InteractionProtocol.FIPA_REQUEST), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
    
   this.addBehaviour(new RequestResponseBehaviour(this, null));

   // Setup finished. When the last inform is received, the agent itself will add
   // a behavious to send/receive actions

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
	for(int x=0;x<game.getMap().length-1;x++)
		for(int y=0;y<game.getMap()[x].length-1;y++)
		{
			if (game.getCell(x, y).isDiscovered())
			{
				publicGame.setCell(x, y, game.getCell(x, y));
			}else
				publicGame.setCell(x, y, null);
		}
}


  /**
   * Create Real Agents and fill AID
   */
	@SuppressWarnings("unchecked")
	protected void createAgents() 
	{
		updatePublicGame();
		AuxInfo info=game.getInfo();
	    initContainer();
        HashMap hm = info.getAgentsInitialPosition();
	    Iterator it = hm.keySet().iterator();
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
   * 
   * @author roger
   *	A simple emulation of a real game with random moves
   */
  private class Simulator extends OneShotBehaviour{

	  public Simulator(){
		super ();
	  }
	/**
	 * Action
	 */
	private static final long serialVersionUID = 8803529649791935988L;

	@SuppressWarnings("unchecked")
	@Override
	public void action() {
        int destX,destY;
		boolean fatalError=false;
		Cell agentPosition;
		//showMessage("Simulation Step. Turn "+game.getInfo().getTurn());
		
        HashMap hm = game.getInfo().getAgentsInitialPosition();
        Iterator it = hm.keySet().iterator();
        while (it.hasNext()){
      	  InfoAgent ia = (InfoAgent)it.next();
      	  try {
			//showMessage("Moving "+ia.getAgent());
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        agentPosition=null;
		for(int x=0;x<game.getMap().length-1;x++)
			for(int y=0;y<game.getMap()[x].length-1;y++)
			{
				Cell c=game.getCell(x,y);
	         	  if(c.isThereAnAgent())
	         	  {
	         		  if(c.getAgent().getAID().equals(ia.getAID())){
	         			  agentPosition=c; 
	         		  }
	         	  }
			}

           if(agentPosition!=null)
           {
	            int dx=0,dy=0;
	            int action=(int) (Math.random()*4);
	            switch (action)
	            {
	            case 0: dx=0;dy=-1;break;
	            case 1: dx=1;dy=0;break;
	            case 2: dx=0;dy=1;break;
	            case 3: dx=-1;dy=0;break;
	            }
	            
	
	            destX=agentPosition.getColumn()+dx;
	            destY=agentPosition.getRow()+dy;
	            
	
	            
	            try {
	                Cell agentDestination = game.getMap()[destY][destX];
	                //System.out.println("I have the destination cell");
					agentPosition.removeAgent(ia);
	                //System.out.println("Agent removed from previous position");
					agentDestination.addAgent(ia);
					//System.out.println("Agent added");
					//System.out.println("******* EUREKA **********");
				} catch (Exception e) {
					try {
						//e.printStackTrace();
						if(!agentPosition.isThereAnAgent()) agentPosition.addAgent(ia);
					} catch (Exception e1) {
						showMessage("FATAL ERROR: Cannot Undo this action");
						fatalError=true;
						//e1.printStackTrace();
					}
					System.out.println("The random move did not suceed");
				}
           }
            
        }

		//do it again
		if(!fatalError)
		{
			gui.repaint();
			if (game.getInfo().getTurn()<game.getInfo().getGameDuration())
			{
				game.getInfo().incrTurn();
				try {Thread.sleep(game.getInfo().getTimeout());} catch ( InterruptedException e ) {}
				SequentialBehaviour sb = new SequentialBehaviour();
				sb.addSubBehaviour(new Simulator());
				this.myAgent.addBehaviour(sb);
			}else{
				showMessage("Game Finished");
				try {
					game.writeGameResult("result.txt", game.getMap());
				} catch (IOException e) {
					showMessage("Cannot write the game results");
					e.printStackTrace();
				} catch (Exception e) {
					showMessage("General error writing game results");
					e.printStackTrace();
				}
			}
		}
	}
	  
  }
  
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
		}
	} 
  }
  
  /*************************************************************************/

  /**
   * <p><B>Title:</b> IA2-SMA</p>
   * <p><b>Description:</b> Practical exercise 2010-11. Recycle swarm.</p>
   * Class that receives the REQUESTs from any agent. Concretely it is used 
   * at the beginning of the game. The Coordinator Agent sends a REQUEST for all
   * the information of the game and the CentralAgent sends an AGREE and then
   * it sends all the required information.
   * <p><b>Copyright:</b> Copyright (c) 2009</p>
   * <p><b>Company:</b> Universitat Rovira i Virgili (<a
   * href="http://www.urv.cat">URV</a>)</p>
   * @author David Isern and Joan Albert L�pez
   * @see sma.ontology.Cell
   * @see sma.ontology.InfoGame
   * @see sma.ontology.CellList
   */
  private class RequestResponseBehaviour extends AchieveREResponder {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8595737671236286405L;


	/**
     * Constructor for the <code>RequestResponseBehaviour</code> class.
     * @param myAgent The agent owning this behaviour
     * @param mt Template to receive future responses in this conversation
     */
    public RequestResponseBehaviour(CentralAgent myAgent, MessageTemplate mt) {
      super(myAgent, mt);
      showMessage("Waiting REQUESTs from authorized agents");
    }

    protected ACLMessage prepareResponse(ACLMessage msg) {

      /* method called when the message has been received. If the message to send
       * is an AGREE the behaviour will continue with the method prepareResultNotification. */

      ACLMessage reply = msg.createReply();
      try {
        Object contentRebut = (Object)msg.getContent();
        if(contentRebut.equals("Initial request")) {
          showMessage("Initial request received");
          reply.setPerformative(ACLMessage.AGREE);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      showMessage("Answer sent"); //: \n"+reply.toString());
      
      return reply;
    } //endof prepareResponse


   

    /**
     * This method is called after the response has been sent and only when
     * one of the following two cases arise: the response was an agree message
     * OR no response message was sent. This default implementation return null
     * which has the effect of sending no result notification. Programmers
     * should override the method in case they need to react to this event.
     * @param msg ACLMessage the received message
     * @param response ACLMessage the previously sent response message
     * @return ACLMessage to be sent as a result notification (i.e. one of
     * inform, failure).
     */
    protected ACLMessage prepareResultNotification(ACLMessage msg, ACLMessage response) {

      // it is important to make the createReply in order to keep the same context of
      // the conversation
      ACLMessage reply = msg.createReply();
      reply.setPerformative(ACLMessage.INFORM);

      try {
        reply.setContentObject(game.getInfo());
      } catch (Exception e) {
        reply.setPerformative(ACLMessage.FAILURE);
        System.err.println(e.toString());
        e.printStackTrace();
      }
      showMessage("Answer sent"); //+reply.toString());  
      
      // Call the simulator
		/*SequentialBehaviour sb = new SequentialBehaviour();
		sb.addSubBehaviour(new Simulator());
		this.myAgent.addBehaviour(sb);*/
      this.myAgent.addBehaviour(new TurnControlBehavior(this.myAgent, game.getInfo().getTimeout()));
      
      return reply;

    } //endof prepareResultNotification


    /**
     *  No need for any specific action to reset this behaviour
     */
    public void reset() {
    }

  } //end of RequestResponseBehaviour


  /*************************************************************************/

  class MovesReceiver extends AchieveREInitiator
  {
	private Cell findAgent(Agent a)
	{
        Cell agentPosition=null;
		for(int x=0;x<game.getMap().length-1;x++)
			for(int y=0;y<game.getMap()[x].length-1;y++)
			{
				Cell c=game.getCell(x,y);
	         	  if(c.isThereAnAgent())
	         	  {
	         		  if(c.getAgent().getAID().equals(a.getAID())){
	         			  agentPosition=c; 
	         		  }
	         	  }
			}
		return agentPosition;
	}
	@Override
	protected void handleInform(ACLMessage arg0) {
		Cell origin = null,destination;
		InfoAgent ia = null;
		int dx=0,dy=0,x,y;
		try {
			sma.moves.Movement moveOrder=(Movement) arg0.getContentObject();
			origin=findAgent(moveOrder.getAgent());
			x=origin.getColumn();
			y=origin.getRow();
			switch (moveOrder.getOrder())
			{
				case UP:dy=-1;break;
				case DOWN:dy=1;break;
				case LEFT:dx=-1;break;
				case RIGHT:dx=1;break;
			}
			destination=game.getCell(x+dx, y+dy);
			ia=origin.getAgent();
			origin.removeAgent(ia);
			destination.addAgent(ia);		
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			try {
				if(!origin.isThereAnAgent()) origin.addAgent(ia);
			} catch (Exception e1) {
				showMessage("FATAL ERROR: Cannot Undo this action");
			}
		}

		super.handleInform(arg0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1637780111859751489L;

	public MovesReceiver(Agent arg0, ACLMessage arg1) {
		
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	  
  }

} //endof class AgentCentral
