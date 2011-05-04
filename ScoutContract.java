package sma.scout;

import jade.core.Agent;
import jade.domain.introspection.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

public class ScoutContract extends ContractNetResponder{

	public ScoutContract(Agent myAgent, MessageTemplate msg) {
		super(myAgent, msg);
		
	}
	
	
	
	/*
	 * ACLMessage prepareResponse (ACLMessage msg): s’executa
	 * quan es rep un missatge CFP i caldrà que retorni la resposta corresponent
	 * (not-understood, refuse o propose).
	*/
	protected ACLMessage prepareResponse (ACLMessage msg) {
		return msg;}
	
	/*
	 * ACLMessage prepareResultNotification (ACLMessage
	 * cfp, ACLMessage propose, ACLMessage accept): s’executa
	 * quan es rep un missatge Accept-proposal. També rep per paràmetres el
	 * CFP inicial i la resposta (Propose). Cal que executi l’acció corresponent i
	 * retorni el resultat (Inform o Failure).
	 */
	protected ACLMessage prepareResultNotification (ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
		return accept;
		
	}
	
	/*
	 * void handleRejectPorposal (ACLMessage cfp, ACLMessage
	 * propose, ACLMessage reject): manega la recepció de missatges
	 * Reject-proposal.
	*/
	protected void handleRejectProposal (ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
		
	}

	private static final long serialVersionUID = 1L;

}
