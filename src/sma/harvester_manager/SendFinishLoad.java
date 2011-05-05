package sma.harvester_manager;
import jade.core.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;
import jade.lang.acl.*;

public class SendFinishLoad extends Agent{
	
	private String pos;
	
	public SendFinishLoad (Agent myAgent, String content)
	{	//Send to manager harvester that all garbage is load, and content have the distance of recycling center.
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		msg.setProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		msg.setContent(content);
		msg.setSender(myAgent.getAID());
		msg.addReceiver(getIdReceiver());
		myAgent.send(msg);
		
		//Now wait to response , will be "confirm" or "failure". Content is the position that should go.		
		MessageTemplate mt = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		ACLMessage msg2=myAgent.blockingReceive(mt);
		//Returns the integer corresponding to the performative, 1 if is agree.
		if (msg2.getPerformative()==1){
			//Content have the position of central recycling.
			setPos(msg2.getContent());						
			////!!!!!return pos; ficar el pos en alguna variable? que retorni algu el constructor?
			System.out.println("El manager: "+msg2.getSender()+", ha enviat el content: "+getPos()+".");
			//int 14 is refuse
		}else if (msg2.getPerformative()==14) System.out.println("El manager: "+msg2.getSender()+", ha refusat.");
		
		//Send confirm to the manager and communication is finished.
		ACLMessage msg3 = new ACLMessage(ACLMessage.CONFIRM);
		msg3.setProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		msg3.setSender(myAgent.getAID());
		msg3.addReceiver(msg2.getSender());
		myAgent.send(msg3);
		//End of this communication FIPA Query.
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
	
	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

}
