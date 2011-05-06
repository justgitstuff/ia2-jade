package sma.harvester_manager;
import jade.core.*;
import jade.lang.acl.*;

public class ReciveFinishDownload extends Agent{
	
	private String content;
	
	public ReciveFinishDownload (Agent myAgent)
	{
		//Now wait from the harvester when he finish the download garbage in the recycling center.		
		MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage msg=myAgent.blockingReceive(MessageTemplate.and(mt1, mt2));
		setContent(msg.getContent());						
		System.out.println("El harvester: "+msg.getSender()+", ha enviat el content: "+getContent()+".");		
		//If download it's ok.
		if (getContent().equals(sma.UtilsAgents.OK))
		{
			System.out.println("Harvester: "+msg.getSender()+", downloaded has successfully garbage.");
			//Send be_scout to the harvester.
			ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
			msg2.setProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
			msg2.setContent(sma.UtilsAgents.BE_SCOUT);
			msg2.setSender(myAgent.getAID());
			msg2.addReceiver(msg.getSender());
			myAgent.send(msg2);			
			
		}else if(getContent().equals(sma.UtilsAgents.FAILURE))
		{//else download failure.
			System.out.println("Harvester: "+msg.getSender()+", send failure download garbage.");
		}else System.out.println("Message from Harvester "+msg.getSender()+", failure in comunication protocol.");		
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}	
	
}
