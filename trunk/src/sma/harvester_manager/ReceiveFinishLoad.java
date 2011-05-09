package sma.harvester_manager;
import java.io.IOException;

import sma.ontology.Cell;
import sma.ontology.InfoGame;
import jade.core.*;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.*;
import jade.proto.AchieveREResponder;

public class ReceiveFinishLoad{
	InfoGame game;
	
	public void addBehaviour(Agent agent, InfoGame game){
		this.game=game;
		MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
		agent.addBehaviour(new RecieveFinishL(agent,MessageTemplate.and(mt1, mt2)));
	}
	
	public class RecieveFinishL extends AchieveREResponder{		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RecieveFinishL(Agent arg0, MessageTemplate arg1){
			super(arg0, arg1);
		}
		
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			DistanceList dist=null;			
			
			ACLMessage r= arg0.createReply();
			try {
				dist = (DistanceList) arg0.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			//In dist have all the distance to recycling center.
			//Choose the cell that i decided and return that cell.
			r.setPerformative(ACLMessage.AGREE);
			Cell cel = game.getCell(1, 2);
			try {
				r.setContentObject(cel);
			} catch (IOException e) {
				e.printStackTrace();
			}	
			System.out.println("Receive from harvester that distancelist, dist 1: "+dist.getDistances().get(0)+"dist 2: "+dist.getDistances().get(1)+"...");
			return r;
		}		
	}	
}

/*public class ReciveFinishLoad extends Agent{
	
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
		msg2.setContent("la posici� final del centre de reciclatge");
		msg2.setSender(myAgent.getAID());
		msg2.addReceiver(msg.getSender());
		myAgent.send(msg2);
		
		//Wait to message from harvester, will be Confirma or failure.
		MessageTemplate mt = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		ACLMessage msg3=myAgent.blockingReceive(mt);
		//Returns the integer corresponding to the performative, 4 if is confirm.
		if (msg3.getPerformative()==4){
			//finish of comunication.						
			System.out.println("El harverster: "+msg3.getSender()+", ha enviat confirm i es tanca aquesta comunicaci�.");
			//int 6 is failure
		}else if (msg3.getPerformative()==6) System.out.println("El harvester: "+msg3.getSender()+", ha donat error en la comunicaci� pel motiu "+msg3.getContent()+".");
	}
		
	public String getPositions() {
		return positions;
	}

	public void setPositions(String positions) {
		this.positions = positions;
	}
}*/
