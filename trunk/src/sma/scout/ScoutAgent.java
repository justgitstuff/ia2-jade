package sma.scout;

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
import sma.UtilsAgents;
import sma.ontology.InfoGame;
import sma.pathFinding.Path;

public class ScoutAgent extends Agent {

	private sma.ontology.InfoGame game;
	private int my_zone;
	private Path short_path;
	private int my_x, my_y;
	private DFAgentDescription dfd;
	private ProtocolContractNetResponder protocolContractNetResponder;
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
	    sd1.setType(UtilsAgents.SCOUT_AGENT);
	    sd1.setName(getLocalName());
	    sd1.setOwnership(UtilsAgents.OWNER);
	    
	    dfd = new DFAgentDescription();
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
			
			this.game.getInfo();
			if(this.game!=null)
			{
				
				
				showMessage("Tinc un joc amb "+game.getInfo().getNumScouts()+" scouts");
				//GameMap gm = new GameMap();
				//this.game.getCell(1,1);
			}
		}
	    
	    //Adds a behavior to update game info each turn
	    addTurnControl();
	    
	    protocolContractNetResponder = new ProtocolContractNetResponder();
		 
	    protocolContractNetResponder.addBehaviour(this);
	    
	    protocolContractNetResponder.setInfoGame(game);
	   
	  
	    
		super.setup();
	}
	
	
	
	
		
	
	
	// TURN CONTROL
	public void addTurnControl()
	{
		System.out.println("TURN CONTROL ACTIVAT");
	    MessageTemplate mt= MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_TURN);
		this.addBehaviour(new MessageReceiver(this,mt));
	}
	
	
	

	class MessageReceiver extends AchieveREResponder
	{
		@Override
		protected ACLMessage prepareResultNotification(ACLMessage arg0,
				ACLMessage arg1) throws FailureException {
			// TODO Auto-generated method stub
			
			return null;
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0)throws NotUnderstoodException, RefuseException {
			
			ACLMessage response=arg0.createReply();
			
			try {
				if(arg0.getContentObject() instanceof InfoGame)
				{
					response.setPerformative(ACLMessage.AGREE);
					game=(InfoGame) arg0.getContentObject();
					showMessage("New turn "+ game.getInfo().getTurn());
					 
					protocolContractNetResponder.setInfoGame(game);
					//my_zone = 1;
					//hauria d'agafar la zona que em pertoca com??? qui me l'assigna
					
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
