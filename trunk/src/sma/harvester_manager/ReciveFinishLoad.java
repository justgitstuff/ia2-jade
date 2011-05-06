package sma.harvester_manager;
import jade.core.*;
import jade.lang.acl.*;

public class ReciveFinishLoad extends Agent{
	
	private String positions;

	public ReciveFinishLoad (Agent myAgent)
	{
		//Now wait to message from harvester, content will be the distance of all recycling center.		
		MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
		ACLMessage msg=myAgent.blockingReceive(MessageTemplate.and(mt1, mt2));
		setPositions(msg.getContent());						
		System.out.println("El harvester: "+msg.getSender()+", ha enviat el content: "+getPositions()+".");
		
		//tractar el content.....
		
		//Send to harvester the position of recycling center where is that going to download.
		ACLMessage msg2 = new ACLMessage(ACLMessage.AGREE);
		msg2.setProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		msg2.setContent("la posició final del centre de reciclatge");
		msg2.setSender(myAgent.getAID());
		msg2.addReceiver(msg.getSender());
		myAgent.send(msg2);
		
		//Wait to message from harvester, will be Confirma or failure.
		MessageTemplate mt = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		ACLMessage msg3=myAgent.blockingReceive(mt);
		//Returns the integer corresponding to the performative, 4 if is confirm.
		if (msg3.getPerformative()==4){
			//finish of comunication.						
			System.out.println("El harverster: "+msg3.getSender()+", ha enviat confirm i es tanca aquesta comunicació.");
			//int 6 is failure
		}else if (msg3.getPerformative()==6) System.out.println("El harvester: "+msg3.getSender()+", ha donat error en la comunicació pel motiu "+msg3.getContent()+".");
	}
		
	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}
}
