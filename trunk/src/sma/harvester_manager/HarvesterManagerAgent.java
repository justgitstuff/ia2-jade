package sma.harvester_manager;

import sma.UtilsAgents;
import sma.ontology.Cell;
import sma.ontology.InfoAgent;
import sma.ontology.InfoGame;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

public class HarvesterManagerAgent extends Agent{
	/**
	 * 
	 */
	private static final long serialVersionUID = -981035261731176754L;

	private InfoGame game;
	private ProtocolContractNetInitiator contractNetInitiator;
	private ReceiveFinishLoad receiveFinishLoad;
	
	  /**
	   * A message is shown in the log area of the GUI
	   * @param str String to show
	   */
	private void showMessage(String str) {
	  System.out.println(getLocalName() + ": " + str);
	}
	
	@Override
	protected void setup() {
	    // Register the agent to the DF
	    ServiceDescription sd1 = new ServiceDescription();
	    sd1.setType(UtilsAgents.HARVESTER_MANAGER_AGENT);
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
	    
	    // Add a Behavior to receive Turns
	    MessageTemplate mt=MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_TURN);
	    this.addBehaviour(new QueriesReceiver(this,mt));
	    	    
	    receiveFinishLoad = new ReceiveFinishLoad();	        
	    contractNetInitiator = new ProtocolContractNetInitiator();
	    	    
	    /**
	     * Add a behavior to receive that the harvester load successful garbage. 
	     */
	    receiveFinishLoad.addBehaviour(this);
	    
	    /**
	     * Add a behavior to receive that the harvester download all garbage into recycling center. 
	     */
		new ReceiveFinishDownload().addBehaviour(this);
	    
	    /**
	     * Add a behavior to relay movement orders from harvesters to the coordinator agent.
	     */
	    new MovementRely().addBehavior(this, sma.UtilsAgents.COORDINATOR_AGENT);
	    super.setup();
	}
	
	class QueriesReceiver extends AchieveREResponder
	{

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#prepareResponse(jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			return null;
		}

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#prepareResultNotification(jade.lang.acl.ACLMessage, jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage prepareResultNotification(ACLMessage arg0,
				ACLMessage arg1) throws FailureException {
			return null;
		}

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#handleRequest(jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage handleRequest(ACLMessage arg0)	throws NotUnderstoodException, RefuseException {
			try {
				Object objectReceived= arg0.getContentObject();
				if (objectReceived instanceof InfoGame)
				{
					//Is the coordinator informing of a new turn
					game=(InfoGame)objectReceived;
					showMessage("New turn received from coordinator: "+game.getInfo().getTurn());
					
					//Find all my agents and send them the new turn
					ACLMessage message= new ACLMessage(ACLMessage.REQUEST);
					for(int r=0;r<game.getMap().length-1;r++)
						for(int c=0;c<game.getMap()[c].length-1;c++)
						{
							Cell cell=game.getCell(r,c);
							if (cell!=null)
								if(cell.isThereAnAgent())
								{
									InfoAgent a = cell.getAgent();
									if (a.getAgent().equals("H"))
									{
										message.addReceiver(a.getAID());
									}
								}
						}
					message.setProtocol(sma.UtilsAgents.PROTOCOL_TURN);
					message.setSender(this.myAgent.getAID());
					message.setContentObject(game);
					
					//Actualizing game.
					receiveFinishLoad.setGame(game);
					
					//Send new torn for all harvesters.
					this.myAgent.send(message);
										
					//For each garbage do ContractNetInitiator
					for (int r=0;r<game.getMap().length-1;r++)
					{	
						for (int c=0; c<game.getMap()[r].length-1;c++)
						{
							Cell cell=game.getCell(r,c);							
							if (cell != null)
							{	//If that cell is building.
								if (cell.getCellType()==Cell.BUILDING)
								{	//And not is recycling_center.
									if(cell.getCellType()!=Cell.RECYCLING_CENTER)
										//if getGarbageunits is 0 -> no garbage.
										if(cell.getGarbageUnits()>0) {
											//Execute ContratNet to send harvesters that cell with garbage. 
											contractNetInitiator.addBehaviour(this.myAgent, cell);
										}
								}
							}
						}
					}
				}
				
			} catch (UnreadableException e) {
				e.printStackTrace();
				showMessage("Received an Object that cannot be understood");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return super.handleRequest(arg0);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -6176920249600913698L;

		public QueriesReceiver(Agent arg0, MessageTemplate arg1) {
			super(arg0, arg1);
		}		
	}
}
