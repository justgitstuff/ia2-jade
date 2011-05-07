package sma;

import jade.core.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import sma.harvester_manager.MovementRely;
import sma.ontology.*;

import java.util.*;
/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2010-11. Recycle swarm.</p>
 * <p><b>Copyright:</b> Copyright (c) 2009</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author not attributable
 * @version 2.0
 */
public class CoordinatorAgent extends Agent {

  /**
	 * 
	 */
	private static final long serialVersionUID = 2444910571881207948L;

private AuxInfo info;

  private AID centralAgent, harvesterManagerAgent, scoutManagerAgent;

  public CoordinatorAgent() {
  }

  /**
   * A message is shown in the log area of the GUI
   * @param str String to show
   */
  private void showMessage(String str) {
    System.out.println(getLocalName() + ": " + str);
  }


  /**
   * Agent setup method - called when it first come on-line. Configuration
   * of language to use, ontology and initialization of behaviours.
   */
  protected void setup() {

    /**** Very Important Line (VIL) *********/
    this.setEnabledO2ACommunication(true, 1);
    /****************************************/

    // Register the agent to the DF
    ServiceDescription sd1 = new ServiceDescription();
    sd1.setType(UtilsAgents.COORDINATOR_AGENT);
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
      System.err.println(getLocalName() + " registration with DF " + "unsucceeded. Reason: " + e.getMessage());
      doDelete();
    }

    // we search the CentralAgent
    ServiceDescription searchCriterion = new ServiceDescription();
    searchCriterion.setType(UtilsAgents.CENTRAL_AGENT);
    this.centralAgent = UtilsAgents.searchAgent(this, searchCriterion);

    // we search for manager Agents
    searchCriterion.setType(UtilsAgents.HARVESTER_MANAGER_AGENT);
    this.harvesterManagerAgent= UtilsAgents.searchAgent(this, searchCriterion);
    
    searchCriterion.setType(UtilsAgents.SCOUT_MANAGER_AGENT);
    this.scoutManagerAgent = UtilsAgents.searchAgent(this, searchCriterion);
    // searchAgent is a blocking method, so we will obtain always a correct AID
    
    //add a behavior to receive end turns and movement orders
    this.addBehaviour(new MessageReceiver(this, null));
    //add a behavior to rely movement orders to Central Agent
    new MovementRely().addBehavior(this, sma.UtilsAgents.CENTRAL_AGENT);
    
  } //endof setup


  class MessageReceiver extends AchieveREResponder
  {

	@Override
	protected ACLMessage prepareResultNotification(ACLMessage arg0,	ACLMessage arg1) throws FailureException {
		return null;
	}

	@Override
	protected ACLMessage prepareResponse(ACLMessage arg0)
			throws NotUnderstoodException, RefuseException {
	      /* method called when the message has been received. If the message to send
	       * is an AGREE the behaviour will continue with the method prepareResultNotification. */

	      ACLMessage reply = arg0.createReply();
	      try {
	        Object contentRebut = (Object)arg0.getContentObject();
	        if (contentRebut instanceof InfoGame)
	        {
	        	InfoGame game=(InfoGame)contentRebut;
    	
	        	reply.setPerformative(ACLMessage.AGREE);
	        	showMessage("Turn received: "+game.getInfo().getTurn());
	        	
	        	//Send map to all managers
			    ACLMessage requestInicial = new ACLMessage(ACLMessage.REQUEST);
			    requestInicial.clearAllReceiver();
			    requestInicial.addReceiver(harvesterManagerAgent);
			    requestInicial.addReceiver(scoutManagerAgent);
			    requestInicial.setProtocol(sma.UtilsAgents.PROTOCOL_TURN);
			    try {
			      requestInicial.setContentObject(game);
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
			    //we add a behavior that sends the message and waits for an answer
			    this.myAgent.addBehaviour(new MessageSender(this.myAgent, requestInicial));
	        }
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
     
	      // Send the map to managers
	      
	      
	      return reply;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5733983393467734330L;

	public MessageReceiver(Agent arg0, MessageTemplate arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	  
  }
  
  
  class MessageSender extends AchieveREInitiator
  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8383729124578338150L;

	public MessageSender(Agent arg0, ACLMessage arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	  
  }
 
  /*************************************************************************/

  /**
    * <p><B>Title:</b> IA2-SMA</p>
    * <p><b>Description:</b> Practical exercise 2010-11. Recycle swarm.</p>
    * This class lets send the REQUESTs for any agent. Concretely it is used in the 
    * initialization of the game. The Coordinator Agent sends a REQUEST for the information
    * of the game (instance of <code>AuxInfo</code> containing parameters, coordenates of 
    * the agents and the recycling centers and visual range of each agent). The Central Agent
    * sends an AGREE and then it informs of this information which is stored by the Coordinator
    * Agent. The game is processed by another behaviour that we add after the INFORM has been 
    * processed.
    * <p><b>Copyright:</b> Copyright (c) 2011</p>
    * <p><b>Company:</b> Universitat Rovira i Virgili (<a
    * href="http://www.urv.cat">URV</a>)</p>
    * @author David Isern and Joan Albert L�pez
    * @see sma.ontology.Cell
    * @see sma.ontology.InfoGame
    * @see sma.ontology.CellList
   */
  class RequesterBehaviour extends AchieveREInitiator {
	  
	/**
	 * 
	 */
	private static final long serialVersionUID = 3102064470122523141L;
	@SuppressWarnings("unused")
	private ACLMessage msgSent = null;
    @SuppressWarnings("unused")
	private boolean finish = false;
    
    public RequesterBehaviour(Agent myAgent, ACLMessage requestMsg) {
      super(myAgent, requestMsg);
      showMessage("AchieveREInitiator starts...");
      msgSent = requestMsg;
    }

    /**
     * Handle AGREE messages
     * @param msg Message to handle
     */
    protected void handleAgree(ACLMessage msg) {
      showMessage("AGREE received from "+ ( (AID)msg.getSender()).getLocalName());
    }

    /**
     * Handle INFORM messages
     * @param msg Message
     */
    @SuppressWarnings("unchecked")
	protected void handleInform(ACLMessage msg) {
      showMessage("INFORM received from "+ ( (AID)msg.getSender()).getLocalName()+" ... [OK]");
      try {
        info = (AuxInfo)msg.getContentObject();
        if (info instanceof AuxInfo) {
          //showMessage("Visual range for each agent: ");
          HashMap<?, ?> hm = info.getAgentsInitialPosition();
          Iterator<?> it = hm.keySet().iterator();
          while (it.hasNext()){
        	  InfoAgent ia = (InfoAgent)it.next();
              //System.out.println(ia);
              java.util.List<Cell> pos = (java.util.List<Cell>)hm.get(ia);
              Iterator<Cell> it2 = pos.iterator();
              while (it2.hasNext()){
            	  it2.next();
              }
              //System.out.println();              
          }
          System.out.println();
          //showMessage("Cells with recycling centers: ");
          it = info.getRecyclingCenters().iterator();
          while (it.hasNext()){
        	 // System.out.println("cell: " + it.next()); 
          }
        
         
          //@todo Add a new behaviour which initiates the turns of the game 

        }
      } catch (Exception e) {
        showMessage("Incorrect content: "+e.toString());
      }
    }

    /**
     * Handle NOT-UNDERSTOOD messages
     * @param msg Message
     */
    protected void handleNotUnderstood(ACLMessage msg) {
      showMessage("This message NOT UNDERSTOOD. \n");
    }

    /**
     * Handle FAILURE messages
     * @param msg Message
     */
    protected void handleFailure(ACLMessage msg) {
      showMessage("The action has failed.");

    } //End of handleFailure

    /**
     * Handle REFUSE messages
     * @param msg Message
     */
    protected void handleRefuse(ACLMessage msg) {
      showMessage("Action refused.");
    }
  } //Endof class RequesterBehaviour


  /*************************************************************************/


} //endof class CoordinatorAgent