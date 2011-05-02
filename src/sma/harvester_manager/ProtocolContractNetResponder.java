package sma.harvester_manager;
import jade.proto.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.Agent;

public class ProtocolContractNetResponder extends ContractNetResponder{
	//El constructor rep la instància de l’agent que l’ha creat
	//i la plantilla amb els filtres per als missatges entrants
	public ProtocolContractNetResponder (Agent myAgent, MessageTemplate mt)
	{
		super(myAgent, mt);
	}
	
	//s’executa	quan es rep un missatge CFP i caldrà que retorni la resposta corresponent
	//(not-understood, refuse o propose)
	protected ACLMessage prepareResponse (ACLMessage msg)
	{
		String content = msg.getContent();
		System.out.println("Harvester: Rebut de "+msg.getSender()+", amb el missatge "+content+".");
		ACLMessage reply = msg.createReply();
		reply.setPerformative(ACLMessage.PROPOSE);
		//Or refuse or not-understood.
		reply.setContent("Responc del harvester "+this.myAgent.getName()+".");
		return reply;
	}
	
	//s’executa quan es rep un missatge Accept-proposal. També rep per paràmetres el
	//CFP inicial i la resposta (Propose). Cal que executi l’acció corresponent i
	//retorni el resultat (Inform o Failure).
	protected ACLMessage prepareResultNotification (ACLMessage cfp, ACLMessage propose, ACLMessage accept)
	{
		ACLMessage inform = accept.createReply();
		System.out.println("Soc el harvester "+this.myAgent.getName()+", rebut de "+accept.getSender()+"acceptada la meva proposta: "+propose.getContent()+".");
		inform.setPerformative(ACLMessage.INFORM);
		//Or failure.
		return inform;
	}
	
	//manega la recepció de missatges Reject-proposal.
	protected void handleRejectProposal (ACLMessage cfp, ACLMessage propose, ACLMessage reject)
	{
		System.out.println("Sóc el Harvester "+this.myAgent.getName()+". Han refusat la meva proposta "+propose.getContent()+".");
	}
}
