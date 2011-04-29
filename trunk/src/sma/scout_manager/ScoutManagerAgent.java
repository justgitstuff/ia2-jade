package sma.scout_manager;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.Vector;

import sma.UtilsAgents;

public class ScoutManagerAgent extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3186674807204123899L;
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
		super.setup();
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
