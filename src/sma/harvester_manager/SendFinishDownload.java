package sma.harvester_manager;
import jade.core.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;
import jade.lang.acl.*;

public class SendFinishDownload extends Agent{
	
	public SendFinishDownload(Agent myAgent, String content)
	{
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
		msg.setContent(content);
		msg.setSender(myAgent.getAID());
		msg.addReceiver(getIdReceiver());
		myAgent.send(msg);
		
		//Now wait for response of manager harvester.
		MessageTemplate mt1 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		MessageTemplate mt2 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
		ACLMessage msg2 = myAgent.blockingReceive(MessageTemplate.and(mt1, mt2));

		//If all is correct the content is marked with "be_scout" and return this value. Else return "error".
		if (msg2.getContent().equals(sma.UtilsAgents.BE_SCOUT)) ;////////return msg2.getContent();
		else ;///////return "error";
		//end of this communication.
	}
	
	private AID getIdReceiver()
	{
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(sma.UtilsAgents.HARVESTER_MANAGER_AGENT);
		dfd.addServices(sd);
		DFAgentDescription agent = null;
		try {
			SearchConstraints c = new SearchConstraints();
			c.setMaxResults(new Long(1)); 
			DFAgentDescription[] DFAgents = DFService.search(this, dfd, c);
			if (DFAgents.length>0) {
				agent = DFAgents[0]; 	return agent.getName();
			}else{
				System.out.println("["+getLocalName()+"]:"+" Receiver not found"); return null;
			}
		} catch (FIPAException e) {
			System.out.println("["+getLocalName()+"]:"+" Receiver not found"); return null;
		}
	}
}
