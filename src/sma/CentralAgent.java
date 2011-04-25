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

  private sma.gui.GraphicInterface gui;
  private sma.ontology.InfoGame game;

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
  
private AID createAgent(String name, String type, String args)
{
	AID itsAID=null;
	try{
	    AgentController another = null;
	    
	    if(args=="") {
	       System.out.println(getLocalName()+" Creating NEW agent ("+name+", "+type+") without arguments ...");
	       another = ac.createNewAgent(name, type, new Object[0]);
	    } else {
	        System.out.println(getLocalName()+": Trying to start "+name+" with arguments ...");
	        Object[] arguments = new Object[1];
	        arguments[0] = args;
	        another = ac.createNewAgent(name, type, arguments);
	      }
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
  
  /**
   * Create Real Agents and fill AID
   */
	@SuppressWarnings("unchecked")
	protected void createAgents() 
	{
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
	              try{
		              switch (ia.getAgentType())
		              {
			              case 0: aid=createAgent(ia.getAgent()+agentNumber, "sma.scout.ScoutAgent", ""); break;
			              case 1: aid=createAgent(ia.getAgent()+agentNumber, "sma.harvester.HarvesterAgent", ""); break;
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
		showMessage("Simulation Step");
		
        HashMap hm = game.getInfo().getAgentsInitialPosition();
        Iterator it = hm.keySet().iterator();
        while (it.hasNext()){
      	  InfoAgent ia = (InfoAgent)it.next();
      	  try {
			showMessage("Moving "+ia.getAgent());
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        agentPosition=null;
		for(int x=1;x<game.getMap().length-1;x++)
			for(int y=1;y<game.getMap()[x].length-1;y++)
			{
				Cell c=game.getCell(x,y);
	         	  if(c.isThereAnAgent())
	         		  if(c.getAgent().equals(ia)){
	         			  agentPosition=c; 
	         			  showMessage("Found!");
	         		  }
			}
		
          /*java.util.List<Cell> pos = (java.util.List<Cell>)hm.get(ia);
          //this is the cell containing the agent
           Iterator it2 = pos.iterator();

           while (it2.hasNext()){
         	  Cell c = (Cell)it2.next();
         	  if(c.isThereAnAgent())
         		  if(c.getAgent().equals(ia)){
         			  agentPosition=c; 			  
         		  }
           }*/
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
	                System.out.println("I have the destination cell");
					agentPosition.removeAgent(ia);
	                System.out.println("Agent removed from previous position");
					agentDestination.addAgent(ia);
					System.out.println("Agent added");
					System.out.println("******* EUREKA **********");
				} catch (Exception e) {
					try {
						e.printStackTrace();
						if(!agentPosition.isThereAnAgent()) agentPosition.addAgent(ia);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
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
			SequentialBehaviour sb = new SequentialBehaviour();
			sb.addSubBehaviour(new Simulator());
			try {Thread.sleep(1000);} catch ( InterruptedException e ) {}
			this.myAgent.addBehaviour(sb);
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
		SequentialBehaviour sb = new SequentialBehaviour();
		sb.addSubBehaviour(new Simulator());
		this.myAgent.addBehaviour(sb);
      
      return reply;

    } //endof prepareResultNotification


    /**
     *  No need for any specific action to reset this behaviour
     */
    public void reset() {
    }

  } //end of RequestResponseBehaviour


  /*************************************************************************/


} //endof class AgentCentral
