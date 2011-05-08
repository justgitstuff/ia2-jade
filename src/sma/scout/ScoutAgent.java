package sma.scout;

import sma.UtilsAgents;
import sma.moves.Movement;
import sma.moves.Movement.Action;
import sma.ontology.InfoGame;
import sma.pathFinding.*;
import sma.scout.TurnScoutReceiver.MessageReceiver;
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
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetResponder;

public class ScoutAgent extends Agent {

	private sma.ontology.InfoGame game;
	private int my_zone;
	private Path short_path;
	private int my_x, my_y;
	private DFAgentDescription dfd;
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
	    
	    
	    
	    //CONTRACTNET
	    MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET), MessageTemplate.MatchPerformative(ACLMessage.CFP));
	    
	    addBehaviour(new ContractNetResponder(this, template){
			
			private static final long serialVersionUID = 1L;

			protected ACLMessage prepareResponse(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent "+getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+cfp.getContent());
				
				
				String[] fields = cfp.getContent().split(" "); //"zone x y"
				
				int proposal = evaluateAction(fields);
				
				if(proposal!=-1){ 
					//we give proposal.
					System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
					ACLMessage propose = cfp.createReply();
					propose.setPerformative(ACLMessage.PROPOSE);
					propose.setContent(String.valueOf(proposal));
					return propose;
				}else{// si em manen anar a una zona diferent a la meva
					//we refuse
					System.out.println("Agent "+getLocalName()+": Refuse");
					throw new RefuseException("proposal-failed");
				}
			}
			
			protected ACLMessage prepareResultNotification(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException{
				System.out.println("Agent "+getLocalName()+": Proposal accepted.");
				if(performAction()){
					System.out.println("Agent "+getLocalName()+": Action successfully performed.");
					ACLMessage inform = accept.createReply();
					inform.setPerformative(ACLMessage.INFORM);
					
					inform.setContent(getNextStep()); // passem U, D, L o R
					
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
	
	private String getNextStep(){// com estan distribuits els index de la matriu del mapa??
		//Movement m = new Movement();
		int destination_x = short_path.getX(1);
		int destination_y = short_path.getY(1);
		
		if(my_x<destination_x){ 
			return "D";	//m.setDirection(sma.moves.Movement.Direction.DOWN);}
		
		}if(my_x>destination_x){ 
			return "U";//m.setDirection(sma.moves.Movement.Direction.UP);
		}
		
		if(my_y<destination_y){ 
			return "L";//m.setDirection(sma.moves.Movement.Direction.LEFT);
		}
		
		if(my_y>destination_y){ 
			return "R";//m.setDirection(sma.moves.Movement.Direction.RIGHT);
		}
		
		return "a";
		
	}
	
	private int evaluateAction(String[] fields){
		if(my_zone != Integer.parseInt(fields[0]))
			return -1;
		
		int xfinal = Integer.parseInt(fields[1]);
		int yfinal = Integer.parseInt(fields[2]);
		//retornem el cami mes curt
		PathTest test = new PathTest(game);
		
		//Em busco a mi mateix
		int xPositionAgent=0;
		int yPositionAgent=0;
		boolean trobat=false;
				
		System.out.println("ssssssssssss" + dfd.getName());
		
		for(int x=0;x<game.getMap().length && !trobat;x++){
			for(int y=0;y<game.getMap()[x].length && !trobat;y++){
				
				if (game.getCell(x, y)!=null){
					System.out.println(game.getCell(x, y).isThereAnAgent());
											
					// MIRAR AGENT
					if(game.getCell(x,y).isThereAnAgent()==true){
						System.out.println("AGENT TROBATTTT");
						if(game.getCell(x,y).getAgent().getAID()==dfd.getName()){
							 xPositionAgent = x;
							 yPositionAgent = y;
							 my_x = x; //globals
							 my_y = y; //globals
							 trobat= true;
						}
					} 
				}
			}	
		}
		
		test.PosicioInicial(xPositionAgent,yPositionAgent,1); // op1
		Path stepsPathFinal= test.PosicioFinal(xfinal,yfinal,1);
		
		//distancia(stepsPathFinal);
		
		//No fa falta, directament mostrem lestep seguent(mirar d controlar tema index i problemes)
		//stepsFinals(stepsPathFinal);
		
		int distPesosOp1= test.distanciaPesos(stepsPathFinal);
		
				
		// OPCIOOOOO 2
		test.PosicioInicial(xPositionAgent,yPositionAgent,2);
		Path stepsPathFinal2= test.PosicioFinal(xfinal,yfinal,2);
		
		int distPesosOp2 = test.distanciaPesos(stepsPathFinal2);
		
		
		// Distancia pitjor dels casos 
		int distFinal= distPesosOp1;
		short_path = stepsPathFinal;
		
		
		if(distFinal>distPesosOp2){
			distFinal=distPesosOp2;
			short_path = stepsPathFinal2;
		}
		
		//System.out.println("Distancia FINAL" + distFinal);
		
		return distFinal;
	}
	
	private boolean performAction(){ // q fa aquesta funcio?
		//return (Math.random()>0.2);
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
					my_zone = 1;
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
