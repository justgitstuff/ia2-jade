package sma.harvester_manager;
import jade.proto.*;
import jade.lang.acl.ACLMessage;
import jade.core.Agent;
import java.util.Vector;

public class ProtocolContractNetInitiator extends ContractNetInitiator{
	////El constructor rep una referència a l’agent que l’ha creat
	//i el missatge (CFP) a enviar
	public ProtocolContractNetInitiator (Agent myAgent, ACLMessage msg)
	{
		super(myAgent, msg); 
	}
	
	//s’executa cada cop que es rep un missatge de Propose
	protected void handlePropose (ACLMessage propose, Vector acceptances)
	{
		ACLMessage reply = propose.createReply();
		//Evaluate the proposal
		System.out.println("L'agent: "+propose.getSender()+", ha enviat "+propose.getContent()+".");
		//Accept o reject proposal.
		reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
		acceptances.add(reply);
	}
	
	protected void handleNotUnderstood (ACLMessage msg) {
		System.out.println("HandleNoutUnderstood");
	}
	protected void handleRefuse (ACLMessage msg) {
		System.out.println("handlerefuse");
	}
	protected void handleInform (ACLMessage msg) {
		System.out.println("Handleinform");
	}
	protected void handleFailure (ACLMessage msg){
		System.out.println("Handlefailure");
	}
}
