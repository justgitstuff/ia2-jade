package sma.harvester;

import sma.UtilsAgents;
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


public class HarvesterAgent extends Agent{

	private static final long serialVersionUID = 2649857519665884242L;
	private ProtocolContractNetResponder protocolContractNetResponder;
	private sma.ontology.InfoGame game;
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
	    sd1.setType(UtilsAgents.HARVESTER_AGENT);
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
	    //Get the game info
	    Object[] args = getArguments();
	    
	    if(args.length>0)
	    {
			this.game=(InfoGame)getArguments()[0];
			if(this.game!=null)
			{
				
				showMessage("Tinc un joc amb "+game.getInfo().getNumScouts()+" scouts");
			}
		}
	    
	    //Adds a behavior to update game info each turn
	    addTurnControl();
	    
	  //CONTRACTNET

	 
	 protocolContractNetResponder = new ProtocolContractNetResponder();
	 protocolContractNetResponder.addBehaviour(this);
	  
	    
		super.setup();
	}
	
	
	
	
	
	
	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		super.takeDown();
	}

	// TURN CONTROL
	public void addTurnControl()
	{
	    MessageTemplate mt= MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_TURN);
		this.addBehaviour(new MessageReceiver(this,mt));
	}
	
	class MessageReceiver extends AchieveREResponder
	{
		@Override
		protected ACLMessage prepareResultNotification(ACLMessage arg0,
				ACLMessage arg1) throws FailureException {
			return null;
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0)
				throws NotUnderstoodException, RefuseException {
			ACLMessage response=arg0.createReply();
			
			try {
				if(arg0.getContentObject() instanceof InfoGame)
				{
					
					response.setPerformative(ACLMessage.AGREE);
					game=(InfoGame) arg0.getContentObject();
					//showMessage("New turn "+ game.getInfo().getTurn());
					protocolContractNetResponder.setInfoGame(game);				
					//TODO you have the new game info on game
				}else{
					throw new NotUnderstoodException("Not the expected object type");
				}
			} catch (UnreadableException e) {
				response.setPerformative(ACLMessage.FAILURE);
			}
			
			return response;
		}

		private static final long serialVersionUID = -2066908850596603472L;

		public MessageReceiver(Agent arg0, MessageTemplate arg1) {
			super(arg0, arg1);
		}
	}


}
