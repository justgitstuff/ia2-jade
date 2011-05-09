package sma.harvester_manager;

import java.io.IOException;

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
	    
	    // Add a Behaviour to receive finished dropped garbage from one harvester.
	    new ReceiveFinishLoad().addBehaviour(this,game);
		/*MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
	    this.addBehaviour(new ReciveFinishDownload(this, MessageTemplate.and(mt1,mt2)));    
	    */
	    //Add a Behaviour to receive finished download garbage into recycling center.
	    new ReceiveFinishDownload().addBehaviour(this);
	    contractNetInitiator = new ProtocolContractNetInitiator();
	    
	    DistanceList l = null;
	    try {
			new SendFinishLoad().addBehaviour(this, l);
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.setup();
	}
	
	class QueriesReceiver extends AchieveREResponder
	{

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#prepareResponse(jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#prepareResultNotification(jade.lang.acl.ACLMessage, jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage prepareResultNotification(ACLMessage arg0,
				ACLMessage arg1) throws FailureException {
			// TODO Auto-generated method stub
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
					for(int x=0;x<game.getMap().length-1;x++)
						for(int y=0;y<game.getMap()[x].length-1;y++)
						{
							Cell c=game.getCell(x, y);
							if(c.isThereAnAgent())
							{
								InfoAgent a = c.getAgent();
								if (a.getAgent().equals("H"))
								{
									message.addReceiver(a.getAID());
								}
							}
						}
					message.setProtocol(sma.UtilsAgents.PROTOCOL_TURN);
					message.setSender(this.myAgent.getAID());
					message.setContentObject(game);
					this.myAgent.send(message);
					//fins aquí enviat un nou torn a tots els harvesters.
					//TODO for each garbage
						Cell cell = game.getCell(1, 2);
						showMessage("contract net.");
						contractNetInitiator.addBehaviour(this.myAgent, cell);									
				}
				
			} catch (UnreadableException e) {
				showMessage("Received an Object that cannot be understood");
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
