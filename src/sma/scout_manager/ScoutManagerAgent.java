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

import java.util.Vector;

import sma.UtilsAgents;
import sma.ontology.InfoGame;

public class ScoutManagerAgent extends Agent{

	/**
	 * 
	 */
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
	    }
	    catch (FIPAException e) {
	      System.err.println(getLocalName() + " registration with DF " + "unsucceeded. Reason: " + e.getMessage());
	      doDelete();
	    }
	    
	    this.addBehaviour(new QueriesReceiver(this, null));
	    
		super.setup();
	}
	
	/**
	 * Manages all incoming FIPA REQUESTS
	 * You can discrimine the type of message with the "instanceof"
	 * @author Roger
	 *
	 */
	class QueriesReceiver extends AchieveREResponder
	{

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#prepareResponse(jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0)
				throws NotUnderstoodException, RefuseException {
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
		protected ACLMessage handleRequest(ACLMessage arg0)
				throws NotUnderstoodException, RefuseException {
			try {
				Object objectReceived= arg0.getContentObject();
				if (objectReceived instanceof InfoGame)
				{
					//Is the coordinator informing of a new turn
					game=(InfoGame)objectReceived;
					showMessage("New turn received from coordinator: "+game.getInfo().getTurn());
					//TODO pass the game to all my agents
				}
				
			} catch (UnreadableException e) {
				showMessage("Received an Object that cannot be understood");
			}
			return super.handleRequest(arg0);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -6176920249600913698L;

		public QueriesReceiver(Agent arg0, MessageTemplate arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	class ScoutManagerHandler extends ContractNetInitiator {
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
