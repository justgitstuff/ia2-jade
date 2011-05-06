package sma.scout;

import sma.UtilsAgents;
import sma.ontology.InfoGame;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class ScoutAgent extends Agent {

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
	    sd1.setType(UtilsAgents.SCOUT_AGENT);
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
			
			this.game.getInfo();
			if(this.game!=null)
			{
				
				showMessage("Tinc un joc amb "+game.getInfo().getNumScouts()+" scouts");
				//GameMap gm = new GameMap();
				//this.game.getCell(1,1);
			}
		}
	    MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET), MessageTemplate.MatchPerformative(ACLMessage.CFP));
	    
	    addBehaviour(new ContractNetResponder(this, template){
			
			private static final long serialVersionUID = 1L;

			protected ACLMessage prepareResponse(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent "+getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+cfp.getContent());
				int proposal = evaluateAction();
				//if(proposal>2){
					//we give proposal.
					System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
					ACLMessage propose = cfp.createReply();
					propose.setPerformative(ACLMessage.PROPOSE);
					propose.setContent(String.valueOf(proposal));
					return propose;
				//}else{
					//we refuse
					//System.out.println("Agent "+getLocalName()+": Refuse");
					//throw new RefuseException("proposal-failed");
				//}
			}
			
			protected ACLMessage prepareResultNotification(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException{
				System.out.println("Agent "+getLocalName()+": Proposal accepted.");
				if(performAction()){
					System.out.println("Agent "+getLocalName()+": Action successfully performed.");
					ACLMessage inform = accept.createReply();
					inform.setPerformative(ACLMessage.INFORM);
					return inform;
				}else {
					System.out.println("Agent "+getLocalName()+": Action execution failed.");
					throw new FailureException("unexpected_error");
				}
			}
			
			protected void handleRejectProposal(ACLMessage reject){
				System.out.println("Agent "+getLocalName()+": Proposal rejected");
			}
		});
	    
	    
		super.setup();
	}
	
	private int evaluateAction(){
		//retornem el cami mes curt
		return 0;
	}
	
	private boolean performAction(){
		//return (Math.random()>0.2);
		//retornem up, down,....
		return true;
	}
	
	
	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		super.takeDown();
	}
	
	
	
	private static final long serialVersionUID = -1956175742934189946L;
//   �� 
	public ScoutAgent()
	{
		super();
	}
}
