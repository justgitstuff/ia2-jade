package sma.harvester_manager;
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.domain.FIPANames;

import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;

public class ContractNetInitiatorAgent_sencer extends Agent {
	
	public void setup(){
		ACLMessage msg = new ACLMessage(ACLMessage.CFP); 
		//en args[i] estan els receivers. Aquesta linia de sota haria de ser un for per a tots els receivers.
		//Application Identifier (AID). isloclname is a boolean.
		///////msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
		// We want to receive a reply in 1 sec
		msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));
		msg.setContent("material x y");
		
		addBehaviour(new ContractNetInitiator(this, msg){
			
			public void hadlePropose(ACLMessage propose, Vector v){
				System.out.println("Agent "+propose.getSender().getName()+" proposed "+propose.getContent());
			}
			
			public void handleRefuse(ACLMessage refuse) {
				System.out.println("Agent "+refuse.getSender().getName()+" refused");
			}
			
			public void handleFailure(ACLMessage failure) {
				if (failure.getSender().equals(myAgent.getAMS())) {
					// FAILURE notification from the JADE runtime: the receiver
					// does not exist
					System.out.println("Responder does not exist");
				}
				else {
					System.out.println("Agent "+failure.getSender().getName()+" failed");
				}
				// Immediate failure --> we will not receive a response from this agent
				//////////nResponders--;
			}
			
			public void handleAllResponses(Vector responses, Vector acceptances) {
			////	if (responses.size() < nResponders) {
					// Some responder didn't reply within the specified timeout
					////System.out.println("Timeout expired: missing "+(nResponders - responses.size())+" responses");
				////}
				// Evaluate proposals.
				int bestProposal = -1;
				AID bestProposer = null;
				ACLMessage accept = null;
				Enumeration e = responses.elements();
				while (e.hasMoreElements()) {
					ACLMessage msg = (ACLMessage) e.nextElement();
					if (msg.getPerformative() == ACLMessage.PROPOSE) {
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
						acceptances.addElement(reply);
						int proposal = Integer.parseInt(msg.getContent());
						if (proposal > bestProposal) {
							bestProposal = proposal;
							bestProposer = msg.getSender();
							accept = reply;
						}
					}
				}
				// Accept the proposal of the best proposer
				if (accept != null) {
					System.out.println("Accepting proposal "+bestProposal+" from responder "+bestProposer.getName());
					accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				}						
			}
			
			public void handleInform(ACLMessage inform) {
				System.out.println("Agent "+inform.getSender().getName()+" successfully performed the requested action");
			}
		});
	}
}
