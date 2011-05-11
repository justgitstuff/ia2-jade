package sma.scout_manager;

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
import jade.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.Vector;

import sma.UtilsAgents;
import sma.ontology.Cell;
import sma.ontology.InfoAgent;
import sma.ontology.InfoGame;

public class ScoutManagerAgent extends Agent{

	private static final long serialVersionUID = -3186674807204123899L;

	private InfoGame game;
	
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
		sd1.setType(UtilsAgents.SCOUT_MANAGER_AGENT);
		sd1.setName(getLocalName());
		sd1.setOwnership(UtilsAgents.OWNER);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd1);
		dfd.setName(getAID());
		
		try {
			DFService.register(this, dfd);
			showMessage("Registered to the DF");
		} catch (FIPAException e) {
			System.err.println(getLocalName() + " registration with DF " + "unsucceeded. Reason: " + e.getMessage());
			doDelete();
		}
		
		MessageTemplate mt = MessageTemplate.MatchProtocol(UtilsAgents.PROTOCOL_TURN);
		this.addBehaviour(new QueriesReceiver(this, mt));
	    
		super.setup();
	}
	
	private void manageScouts() {
		ServiceDescription sd1 = new ServiceDescription();
	    sd1.setType(UtilsAgents.SCOUT_AGENT);
	    
	    DFAgentDescription descripcion = new DFAgentDescription();
        descripcion.addServices(sd1);
        
		try {
			// Get the scouts
			DFAgentDescription[] scouts = DFService.search(this, descripcion);
			
			// Create the CFP
			ACLMessage messageCFP = new ACLMessage(ACLMessage.CFP);
	        for (DFAgentDescription scout:scouts) {
	        	messageCFP.addReceiver(scout.getName());
	        }
		} catch (FIPAException e) {
			System.err.println("No scouts found by the ScoutManager");
		}
	}

	/**
	 * Manages all incoming FIPA REQUESTS
	 * You can discrimine the type of message with the "instanceof"
	 * @author Roger
	 */
	class QueriesReceiver extends AchieveREResponder {

		private static final long serialVersionUID = -6176920249600913698L;
		
		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#prepareResponse(jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			ACLMessage response = arg0.createReply();

			try {
				if (arg0.getContentObject() instanceof InfoGame) {
					response.setPerformative(ACLMessage.AGREE);
					game = (InfoGame) arg0.getContentObject();
					showMessage("New turn " + game.getInfo().getTurn());
				} else if (arg0.getPerformative() == ACLMessage.AGREE) {
					// TODO now what?!
					response = null;
				} else {
					throw new NotUnderstoodException("Not the expected object type");
				}
			} catch (UnreadableException e) {
				response.setPerformative(ACLMessage.FAILURE);
			}
			
			return response;
		}

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#prepareResultNotification(jade.lang.acl.ACLMessage, jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage prepareResultNotification(ACLMessage arg0, ACLMessage arg1) throws FailureException {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#handleRequest(jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage handleRequest(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			try {
				Object objectReceived = arg0.getContentObject();
				if (objectReceived instanceof InfoGame) {
					// Is the coordinator informing of a new turn
					game = (InfoGame) objectReceived;
					showMessage("New turn received from coordinator: " + game.getInfo().getTurn());

					// Find all my agents and send them the new turn
					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					for (int x = 0; x < game.getMap().length - 1; x++) {
						for (int y = 0; y < game.getMap()[x].length - 1; y++) {
							Cell c = game.getCell(x, y);
							if (c!=null)
								if (c.isThereAnAgent()) {
									InfoAgent a = c.getAgent();
									if (a.getAgent().equals("S")) {
										message.addReceiver(a.getAID());
									}
								}
						}
					}
					message.setProtocol(UtilsAgents.PROTOCOL_TURN);
					message.setSender(this.myAgent.getAID());
					message.setContentObject(game);
					this.myAgent.send(message);
				}
				
			} catch (UnreadableException e) {
				showMessage("Received an Object that cannot be understood");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return super.handleRequest(arg0);
		}

		public QueriesReceiver(Agent arg0, MessageTemplate arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}
	}
	
	class ScoutManagerHandler extends ContractNetInitiator {
		private static final long serialVersionUID = -5530192765322368330L;

		public ScoutManagerHandler(Agent agent, ACLMessage aclMessage) {
			super(agent, aclMessage);
		}

        protected void handlePropose(ACLMessage propose, Vector acceptances) {
            System.out.printf("\n");
        }
 
        protected void handleRefuse(ACLMessage refuse) {
            System.out.printf("\n");
        }
 
        protected void handleFailure(ACLMessage failure) {
            if (failure.getSender().equals(myAgent.getAMS())) {
                System.out.println("Error\n");
            } else {
                System.out.printf("Error\n");
            }
        }
 
        protected void handleAllResponses(Vector responses, Vector acceptances) {
 
            ACLMessage accepted = null;
            for (Object resp:responses) {
                ACLMessage message = (ACLMessage) resp;
                if (message.getPerformative() == ACLMessage.PROPOSE) {
//                    ACLMessage response = message.createReply();
//                    response.setPerformative(ACLMessage.REJECT_PROPOSAL);
//                    acceptances.add(response);
                    
                    // TODO determine the best
                }
            }
 
            // TODO Actions to realiza for the best
        }
 
        protected void handleInform(ACLMessage inform) {
            System.out.printf("\n");
        }
	}

}
