package sma.harvester;

import sma.UtilsAgents;
import sma.ontology.InfoGame;
import sma.pathFinding.PathTest;
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
				
				// CONTROL MAPA, el carragem i funcions d'us
				//PathTest test = new PathTest(this.game);	
				/*
				int xPositionAgent;
				int yPositionAgent;
				boolean trobat=false;
				
				
				
				System.out.println("ssssssssssss" + dfd.getName());
				
				for(int x=0;x<game.getMap().length-1 && !trobat;x++){
					for(int y=0;y<game.getMap()[x].length-1 && !trobat;y++){
						
						if (game.getCell(x, y)!=null){
							System.out.println(game.getCell(x, y).isThereAnAgent());
													
							// MIRAR AGENT
							if(game.getCell(x,y).isThereAnAgent()==true){
								System.out.println("AGENT TROBATTTT");
								if(game.getCell(x,y).getAgent().getAID()==dfd.getName()){
									 xPositionAgent= x;
									 yPositionAgent= y;
									 trobat= true;
								}
								
								
								
							} 
						}
					}	
				}
				*/
				
				//test.PosicioInicial(xPositionAgent,xPositionAgent,1);
				//Path stepsPathFinal= test.PosicioFinal(25,8,1);
				
				//distancia(stepsPathFinal);
				
				//No fa falta, directament mostrem lestep seguent(mirar d controlar tema index i problemes)
				//stepsFinals(stepsPathFinal);
				
				//int distPesosOp1= test.distanciaPesos(stepsPathFinal);
				
						
				// OPCIOOOOO 2
				//test.PosicioInicial(xPositionAgent,yPositionAgent,2);
				//Path stepsPathFinal2= test.PosicioFinal(25,8,2);
				
				//int distPesosOp2 = test.distanciaPesos(stepsPathFinal2);
				
				// Distancia pitjor dels casos 
				//int distFinal= distPesosOp1;
				
				//if(distFinal>distPesosOp2)distFinal=distPesosOp2;
				
				//System.out.println("Distancia FINAL" + distFinal);
				
				
				
				showMessage("Tinc un joc amb "+game.getInfo().getNumScouts()+" scouts");
			}
		}
	    
	    //Adds a behavior to update game info each turn
	    addTurnControl();
	    
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0)
				throws NotUnderstoodException, RefuseException {
			ACLMessage response=arg0.createReply();
			
			try {
				if(arg0.getContentObject() instanceof InfoGame)
				{
					//PathTest test = new PathTest(game);
					response.setPerformative(ACLMessage.AGREE);
					game=(InfoGame) arg0.getContentObject();
					showMessage("New turn "+ game.getInfo().getTurn());
					
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
